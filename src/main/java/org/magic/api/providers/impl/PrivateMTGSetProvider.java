package org.magic.api.providers.impl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractCardsProvider;
import org.magic.services.MTGConstants;
import org.magic.tools.FileTools;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class PrivateMTGSetProvider extends AbstractCardsProvider {

	private static final String CARDS = "cards";
	
	private String ext = ".json";
	private File setDirectory;
	
	public void removeEdition(MagicEdition me) {
		File f = new File(setDirectory, me.getId() + ext);
		try {
			logger.debug("delete : " + f);
			FileTools.deleteFile(f);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	public boolean removeCard(MagicEdition me, MagicCard mc) throws IOException {
		File f = new File(setDirectory, me.getId() + ext);
		JsonObject root = FileTools.readJson(f).getAsJsonObject();
		JsonArray cards = root.get(CARDS).getAsJsonArray();

		for (int i = 0; i < cards.size(); i++) {
			JsonElement el = cards.get(i);
			if (el.getAsJsonObject().get("id").getAsString().equals(mc.getId())) {
				cards.remove(el);
				FileTools.saveFile(new File(setDirectory, me.getId() + ext), root.toString());
				return true;
			}
		}
		return false;
	}

	public List<MagicCard> getCards(MagicEdition me) throws IOException {
		JsonObject root =  FileTools.readJson(new File(setDirectory, me.getId() + ext)).getAsJsonObject();
		JsonArray arr = (JsonArray) root.get(CARDS);
		Type listType = new TypeToken<ArrayList<MagicCard>>() {}.getType();
		return new Gson().fromJson(arr, listType);
	}

	public void addCard(MagicEdition me, MagicCard mc) throws IOException {
		File f = new File(setDirectory, me.getId() + ext);
		JsonObject root = FileTools.readJson(f).getAsJsonObject();
		JsonArray cards = root.get(CARDS).getAsJsonArray();

		int index = indexOf(mc, cards);

		if (index > -1) {
			cards.set(index, new Gson().toJsonTree(mc));
		} else {
			cards.add(new Gson().toJsonTree(mc));
			me.setCardCount(me.getCardCount() + 1);
			root.addProperty("cardCount", me.getCardCount());
		}
		FileTools.saveFile(f, root.toString());
	}

	private int indexOf(MagicCard mc, JsonArray arr) {
		for (int i = 0; i < arr.size(); i++)
			if (arr.get(i).getAsJsonObject().get("id") != null
					&& (arr.get(i).getAsJsonObject().get("id").getAsString().equals(mc.getId())))
				return i;

		return -1;
	}

	private MagicEdition getEdition(File f) throws IOException {
		JsonObject root = FileTools.readJson(f).getAsJsonObject();
		return new Gson().fromJson(root.get("main"), MagicEdition.class);
	}

	public void saveEdition(MagicEdition ed, List<MagicCard> cards2) {
		
		cards2.forEach(mc->{
			try {
				removeCard(ed, mc);
				addCard(ed, mc);
				saveEdition(ed);
			} catch (IOException e) {
				logger.error(e);
			}
		});
		
	}

	
	
	
	public void saveEdition(MagicEdition me) throws IOException {
		int cardCount = 0;
		try {
			cardCount = getCards(me).size();

		} catch (Exception e) {
			logger.error(e);
		}

		me.setCardCount(cardCount);

		JsonObject jsonparams = new JsonObject();
		jsonparams.add("main", new Gson().toJsonTree(me));

		if (cardCount == 0)
			jsonparams.add(CARDS, new JsonArray());
		else
			jsonparams.add(CARDS, new Gson().toJsonTree(getCards(me)));

		FileTools.saveFile(new File(setDirectory, me.getId() + ext), jsonparams.toString());

	}

	public void init() {
		setDirectory = getFile("DIRECTORY");
		logger.debug("Opening directory " + setDirectory);
		if (!setDirectory.exists())
		{
			logger.debug("Directory " + setDirectory + " doesn't exist");
			setDirectory.mkdir();
		}
	}

	public MagicCard getCardById(String id, MagicEdition ed) {
		try {
			return searchCardByCriteria("id", id, ed, true).get(0);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public MagicCard getCardById(String id) throws IOException {
		try {
			return searchCardByCriteria("id", id, null, true).get(0);
		}
		catch(IndexOutOfBoundsException e)
		{
			return null;
		}
	}
	
	
	
	@Override
	public List<MagicCard> searchCardByEdition(MagicEdition ed) throws IOException {
		return getCards(ed);
	}
	

	@Override
	public List<MagicCard> searchCardByCriteria(String att, String crit, MagicEdition me, boolean exact)throws IOException {
		List<MagicCard> res = new ArrayList<>();
		if (me == null) {
			for (MagicEdition ed : listEditions())
				for (MagicCard mc : getCards(ed))
					if (hasValue(mc, att, crit))
						res.add(mc);
		} else {
			for (MagicCard mc : getCards(me)) {
				if (hasValue(mc, att, crit))
					res.add(mc);
			}
		}

		return res;
	}

	private boolean hasValue(MagicCard mc, String att, String val) {
		try {
			return BeanUtils.getProperty(mc, att).toUpperCase().contains(val.toUpperCase());
		} catch (Exception e) {
			logger.error("error loading " + mc +" " + att +" " + val,e);
			return false;
		}
	}

	@Override
	public MagicCard getCardByNumber(String id, MagicEdition me) throws IOException {

		MagicEdition ed = getSetById(me.getId());

		for (MagicCard mc : getCards(ed))
			if (mc.getCurrentSet().getNumber().equals(id))
				return mc;

		return null;

	}

	public List<MagicEdition> loadEditions() throws IOException {

		List<MagicEdition> ret = new ArrayList<>();
		for (File f : setDirectory.listFiles(pathname -> pathname.getName().endsWith(ext))) {
			ret.add(getEdition(f));
		}

		return ret;
	}

	@Override
	public MagicEdition getSetById(String id){
		try {
			return getEdition(new File(setDirectory, id + ext));
		} catch (IOException e) {
			return new MagicEdition(id,id);
		}
	}

	@Override
	public String[] getLanguages() {
		return new String[] { "French" };
	}

	@Override
	public List<String> loadQueryableAttributs() {
		try {
				Set<String> keys = BeanUtils.describe(new MagicCard()).keySet();
				return keys.stream().collect(Collectors.toList());
			} catch (Exception e) {
			logger.error(e);
			return new ArrayList<>();
		}
	}

	@Override
	public String getVersion() {
		return "0.1";
	}

	@Override
	public URL getWebSite() throws MalformedURLException {
		return new URL("https://github.com/nicho92/MtgDesktopCompanion");
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public String getName() {
		return "Personnal Data Set Provider";
	}

	@Override
	public void initDefault() {
		setProperty("DIRECTORY",new File(MTGConstants.DATA_DIR, "privateSets").getAbsolutePath());
	}
	

	@Override
	public boolean equals(Object obj) {
		
		if(obj ==null)
			return false;
		
		return hashCode()==obj.hashCode();
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode();
	}

}
