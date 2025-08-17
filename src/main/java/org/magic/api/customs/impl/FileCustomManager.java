package org.magic.api.customs.impl;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.CustomCardsManager;
import org.magic.api.sorters.CardsEditionSorter;
import org.magic.services.logging.MTGLogger;
import org.magic.services.tools.BeanTools;
import org.magic.services.tools.CryptoUtils;
import org.magic.services.tools.FileTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class FileCustomManager implements CustomCardsManager {
	
	private File setDirectory;
	private JsonExport serializer;

	private static final String CARDS = "cards";
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	private String ext = ".json";
	
	

	public FileCustomManager(File directory) {
		setDirectory = directory;
		serializer = new JsonExport();
		
		logger.debug("Opening directory {}",setDirectory);
		if (!setDirectory.exists())
		{
			logger.debug("Directory {} doesn't exist",setDirectory);
			setDirectory.mkdir();
		}
	}
	
	@Override
	public void deleteCustomSet(MTGEdition me) {
		var f = new File(setDirectory, me.getId() + ext);
		try {
			logger.debug("delete : {}",f);
			FileTools.deleteFile(f);
		} catch (IOException e) {
			logger.error(e);
		}
	}


	@Override
	public boolean deleteCustomCard(MTGEdition me, MTGCard mc) throws IOException {
		var f = new File(setDirectory, me.getId() + ext);
		var root = FileTools.readJson(f).getAsJsonObject();
		var cards = root.get(CARDS).getAsJsonArray();

		for (var i = 0; i < cards.size(); i++) {
			var el = cards.get(i);
			if (el.getAsJsonObject().get("id").getAsString().equals(mc.getId())) {
				cards.remove(el);
				FileTools.saveFile(new File(setDirectory, me.getId() + ext), root.toString());
				return true;
			}
		}
		return false;
	}
	
	
	
	@Override
	public List<MTGCard> listCustomsCards(MTGEdition me) throws IOException {
		
		var f = new File(setDirectory, me.getId() + ext);
		security(f);

		if(!f.exists())
			return new ArrayList<>();
		
		
		var root = FileTools.readJson(f).getAsJsonObject().get(CARDS);
		
		if(root==null || root.isJsonNull())
			return new ArrayList<>();
		
		return serializer.fromJsonList(root.toString(), MTGCard.class);

	}
	
	private void security(File f) throws SecurityException {
		if(!f.toPath().normalize().startsWith(setDirectory.getAbsolutePath()))
			throw new SecurityException("Entry is outside of the target directory");
	}


	@Override
	public List<MTGEdition> loadCustomSets() throws IOException {
		var ret = new ArrayList<MTGEdition>();
		for (File f : setDirectory.listFiles(pathname -> pathname.getName().endsWith(ext))) 
			ret.add(loadEditionFromFile(f));
		
		return ret;
	}
	

	@Override
	public void addCustomCard(MTGEdition me, MTGCard mc) throws IOException {
		var f = new File(setDirectory, me.getId() + ext);
		var root = FileTools.readJson(f).getAsJsonObject();
		var cards = root.get(CARDS).getAsJsonArray();
		mc.setEdition(me);
	
		if (mc.getId() == null)
			mc.setId(CryptoUtils.sha256Hex(Instant.now().toEpochMilli()+ me.getSet() + mc.getName()));
		
		int index = indexOf(mc, cards);
		
		var joCard =serializer.toJsonElement(mc); 
		
		if (index > -1) 
		{
			cards.set(index, joCard);
		} 
		else 
		{
			cards.add(joCard);
			me.setCardCount(me.getCardCount() + 1);
			root.addProperty("cardCount", me.getCardCount());
		}
		logger.debug("Adding {} card to {} set with id={}",mc,me,mc.getId());
		FileTools.saveFile(f, root.toString());
	}

	private int indexOf(MTGCard mc, JsonArray arr) {
		for (var i = 0; i < arr.size(); i++)
			if (arr.get(i).getAsJsonObject().get("id") != null && (arr.get(i).getAsJsonObject().get("id").getAsString().equals(mc.getId())))
				return i;

		return -1;
	}

	private MTGEdition loadEditionFromFile(File f) throws IOException {
			
		security(f);
	
		var root = FileTools.readJson(f).getAsJsonObject();
		return serializer.fromJson(root.get("main").toString(), MTGEdition.class);
	}

	@Override
	public void saveCustomSet(MTGEdition ed, List<MTGCard> cards) {
		
		cards.forEach(mc->{
			try {
				deleteCustomCard(ed, mc);
				addCustomCard(ed, mc);
				saveCustomSet(ed);
			} catch (IOException e) {
				logger.error(e);
			}
		});
	}

	@Override
	public void rebuild(MTGEdition ed) throws IOException {
		var cards = listCustomsCards(ed);
		ed.setCardCount(cards.size());
		ed.setCardCountOfficial((int)cards.stream().filter(mc->mc.getSide().equals("a")).count());
		ed.setCardCountPhysical(ed.getCardCountOfficial());
		
		cards.forEach(mc->{
			mc.getEditions().clear();
			try {
				mc.getEditions().add(BeanTools.cloneBean(ed));
				mc.setEdition(mc.getEditions().get(0));
				mc.setNumber(null);
			} catch (Exception e) {
				logger.error(e);
			} 
		});
		Collections.sort(cards,new CardsEditionSorter());

		for(var i=0;i<cards.size();i++)
			cards.get(i).setNumber(String.valueOf((i+1)));

		saveCustomSet(ed,cards);
	}


	@Override
	public void saveCustomSet(MTGEdition me) throws IOException {

		var cards= listCustomsCards(me);
		
		me.setCardCount(cards.size());
		me.setCardCountOfficial((int)cards.stream().filter(mc->mc.getSide().equals("a")).count());
		me.setCardCountPhysical(me.getCardCountOfficial());
		
		var jsonparams = new JsonObject();
		jsonparams.add("main", serializer.toJsonElement(me));

		if (cards.isEmpty())
			jsonparams.add(CARDS, new JsonArray());
		else
			jsonparams.add(CARDS, serializer.toJsonElement(cards));

		FileTools.saveFile(new File(setDirectory, me.getId() + ext), jsonparams.toString());

	}


	@Override
	public MTGEdition getCustomSet(String id) {
		try {
			return loadEditionFromFile(new File(setDirectory, id + ext));
		} catch (IOException _) {
			return new MTGEdition(id,id);
		}
	}
	
}
