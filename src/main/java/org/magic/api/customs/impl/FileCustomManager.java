package org.magic.api.customs.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.abstracts.extra.AbstractCustomCardsManager;
import org.magic.services.MTGConstants;
import org.magic.services.tools.FileTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class FileCustomManager extends AbstractCustomCardsManager {
	
	private File setDirectory;
	private JsonExport serializer;

	private static final String CARDS = "cards";
	private String ext = ".json";
	
	
	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("DIRECTORY", MTGProperty.newDirectoryProperty(new File(MTGConstants.DATA_DIR, "privateSets")));
	}
	

	public FileCustomManager() {
		setDirectory = getFile("DIRECTORY");
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
	public boolean deleteCustomCard(MTGCard mc) throws IOException {
		var f = new File(setDirectory, mc.getEdition().getId() + ext);
		var root = FileTools.readJson(f).getAsJsonObject();
		var cards = root.get(CARDS).getAsJsonArray();

		for (var i = 0; i < cards.size(); i++) {
			var el = cards.get(i);
			if (el.getAsJsonObject().get("id").getAsString().equals(mc.getId())) {
				cards.remove(el);
				FileTools.saveFile(new File(setDirectory, mc.getEdition().getId() + ext), root.toString());
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
	public List<MTGEdition> listCustomSets() throws IOException {
		var ret = new ArrayList<MTGEdition>();
		for (File f : setDirectory.listFiles(pathname -> pathname.getName().endsWith(ext))) 
			ret.add(loadEditionFromFile(f));
		
		return ret;
	}
	

	@Override
	public void saveCustomCard(MTGCard mc) throws IOException {
		var f = new File(setDirectory, mc.getEdition().getId() + ext);
		var root = FileTools.readJson(f).getAsJsonObject();
		var cards = root.get(CARDS).getAsJsonArray();
		int index = indexOf(mc, cards);
		var joCard =serializer.toJsonElement(mc); 
		
		if (index > -1) 
		{
			cards.set(index, joCard);
		} 
		else 
		{
			cards.add(joCard);
			root.addProperty("cardCount", cards.size()+1);
		}
		logger.debug("Adding {} card to {} set with id={}",mc,mc.getEdition(),mc.getId());
		notify(mc);
		
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

	@Override
	public String getName() {
		return "File";
	}
	
}
