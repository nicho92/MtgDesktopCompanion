package org.magic.api.providers.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat;
import org.magic.api.beans.MagicFormat.AUTHORIZATION;
import org.magic.api.beans.MagicRuling;
import org.magic.api.beans.enums.MTGColor;
import org.magic.api.beans.enums.MTGLayout;
import org.magic.api.criterias.MTGCrit;
import org.magic.api.interfaces.abstracts.AbstractCardsProvider;
import org.magic.services.MTGConstants;
import org.magic.tools.URLTools;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MagicTheGatheringIOProvider extends AbstractCardsProvider {

	private static final String WATERMARK = "watermark";
	private static final String FLAVOR = "flavor";
	private static final String TYPE = "type";
	private static final String JSON_URL = "JSON_URL";
	private static final String LOYALTY = "loyalty";
	private static final String MANA_COST = "manaCost";
	private static final String NAME = "name";
	private static final String TOUGHNESS = "toughness";
	private static final String POWER = "power";
	private static final String MULTIVERSEID = "multiverseid";
	private static final String RARITY = "rarity";
	private static final String NUMBER = "number";
	private static final String LAYOUT = "layout";
	private static final String FOREIGN_NAMES = "foreignNames";
	private static final String TEXT = "text";
	private static final String ORIGINAL_TEXT = "originalText";
	private static final String ARTIST = "artist";
	private File fcacheCount = new File(MTGConstants.DATA_DIR, "mtgio.cache");
	private Properties propsCache;
	
	
	@Override
	public List<MagicCard> searchByCriteria(MTGCrit<?>[] crits) throws IOException {
		throw new IOException("Not implemented");
	}
	
	@Override
	public void initDefault() {
		setProperty(JSON_URL, "https://api.magicthegathering.io/v1");
	}
	
	@Override
	public void init() {
		propsCache = new Properties();
		try {
			propsCache.load(new FileReader(fcacheCount));
		} catch (FileNotFoundException e) {
			try {
				FileUtils.touch(fcacheCount);
			} catch (IOException e1) {
				logger.error("couldn't create " + fcacheCount, e1);

			}
		} catch (IOException e) {
			logger.error(e);

		}
	}

	@Override
	public MagicCard getCardById(String id,MagicEdition ed) throws IOException {
		try {
		return searchCardByCriteria("id", id, ed, true).get(0);
		}catch(IndexOutOfBoundsException e)
		{
			return null;
		}
	}


	@Override
	public List<MagicCard> searchCardByCriteria(String att, String crit, MagicEdition me, boolean exact)throws IOException {
		List<MagicCard> lists = new ArrayList<>();
		URLConnection con = null;
		int page = 1;
		String url = getString(JSON_URL) + "/cards?" + att + "=" + URLTools.encode(crit);
		logger.debug(url);

		con = URLTools.openConnection(url);
	
		int count = 0;
		int totalcount = con.getHeaderFieldInt("Total-Count", 0);

		while (count < totalcount) {
			url = getString(JSON_URL) + "/cards?" + att + "=" + URLTools.encode(crit) + "&page=" + page++;
			logger.trace(url);
			JsonArray jsonList = URLTools.extractJson(url).getAsJsonObject().getAsJsonArray("cards");
			for (int i = 0; i < jsonList.size(); i++) {
				lists.add(loadCard(jsonList.get(i).getAsJsonObject()));
			}
			count += con.getHeaderFieldInt("Count", 0);
		}
		return lists;
	}

	@Override
	public MagicCard getCardByNumber(String id, MagicEdition me) throws IOException {
		return searchCardByCriteria(NUMBER, id, me, true).get(0);
	}


	private MagicCard loadCard(JsonObject obj){
		
		try {
			return cacheCards.get(obj.get("id").getAsString(), new Callable<MagicCard>() {
				
				@Override
				public MagicCard call() throws Exception {
					return generateCard(obj);
				}
			});
		} catch (ExecutionException e) {
			logger.error(e);
			return null;
		}
		
		
	}
	
	
	private MagicCard generateCard(JsonObject obj) {
		MagicCard mc = new MagicCard();

		if (obj.get("id") != null)
			mc.setId(obj.get("id").getAsString());
		
		if (obj.get(NAME) != null)
			mc.setName(obj.get(NAME).getAsString());

		if (obj.get(MANA_COST) != null)
			mc.setCost(obj.get(MANA_COST).getAsString());

		if (obj.get(TEXT) != null)
			mc.setText(obj.get(TEXT).getAsString());

		if (obj.get(ORIGINAL_TEXT) != null)
			mc.setOriginalText(obj.get(ORIGINAL_TEXT).getAsString());

	
		if (obj.get(ARTIST) != null)
			mc.setArtist(obj.get(ARTIST).getAsString());

		if (obj.get("cmc") != null)
			mc.setCmc(obj.get("cmc").getAsInt());

		if (obj.get(LAYOUT) != null)
			mc.setLayout(MTGLayout.parseByLabel(obj.get(LAYOUT).getAsString()));

		if (obj.get(POWER) != null)
			mc.setPower(obj.get(POWER).getAsString());

		if (obj.get(TOUGHNESS) != null)
			mc.setToughness(obj.get(TOUGHNESS).getAsString());

		if (obj.get(LOYALTY) != null)
			mc.setLoyalty(obj.get(LOYALTY).getAsInt());

		if (obj.get("colors") != null) {
			Iterator<JsonElement> it = obj.get("colors").getAsJsonArray().iterator();
			while (it.hasNext())
				mc.getColors().add(MTGColor.colorByName(it.next().getAsString()));
		}
		if (obj.get("colorIdentity") != null) {
			Iterator<JsonElement> it = obj.get("colorIdentity").getAsJsonArray().iterator();
			while (it.hasNext())
				mc.getColorIdentity().add(MTGColor.colorByCode(it.next().getAsString()));
		}
		
		

		if (obj.get("types") != null) {
			Iterator<JsonElement> it = obj.get("types").getAsJsonArray().iterator();
			while (it.hasNext())
				mc.getTypes().add(it.next().getAsString());
		}

		if (obj.get("supertypes") != null) {
			Iterator<JsonElement> it = obj.get("supertypes").getAsJsonArray().iterator();
			while (it.hasNext())
				mc.getSupertypes().add(it.next().getAsString());
		}

		if (obj.get("subtypes") != null) {
			Iterator<JsonElement> it = obj.get("subtypes").getAsJsonArray().iterator();
			while (it.hasNext())
				mc.getSubtypes().add(it.next().getAsString());
		}

		if (obj.get("legalities") != null) {
			JsonArray arr = obj.get("legalities").getAsJsonArray();
			for (int i = 0; i < arr.size(); i++) {
				JsonObject k = arr.get(i).getAsJsonObject();
				MagicFormat format = new MagicFormat(k.get("format").getAsString(),AUTHORIZATION.valueOf(k.get("legality").getAsString().toUpperCase()));
				mc.getLegalities().add(format);
			}
		}

		if (obj.get("rulings") != null) {
			JsonArray arr = obj.get("rulings").getAsJsonArray();
			for (int i = 0; i < arr.size(); i++) {
				JsonObject k = arr.get(i).getAsJsonObject();
				MagicRuling rule = new MagicRuling();
				rule.setDate(k.get("date").getAsString());
				rule.setText(k.get(TEXT).getAsString());
				mc.getRulings().add(rule);
			}
		}

//		if (obj.get("names") != null) {
//			List<String> list = new ArrayList<>();
//			list.remove(mc.getName());
//			String rotateName = (list.get(list.size() - 1));
//			mc.setRotatedCardName(rotateName);
//		}

		String currentSet = obj.get("set").getAsString();
		MagicEdition currentEd = getSetById(currentSet);

		if (obj.get(MULTIVERSEID) != null)
			currentEd.setMultiverseid(obj.get(MULTIVERSEID).getAsString());

		if (obj.get(RARITY) != null)
			currentEd.setRarity(obj.get(RARITY).getAsString());

		if (obj.get(NUMBER) != null)
			currentEd.setNumber(obj.get(NUMBER).getAsString());

		mc.getEditions().add(0, currentEd);

		if (obj.get("printings") != null) {
			JsonArray arr = obj.get("printings").getAsJsonArray();
			for (int i = 0; i < arr.size(); i++) {
				String k = arr.get(i).getAsString();
				if (!k.equals(currentSet)) {
					MagicEdition ed = getSetById(k);
					//TODO this function is long initOtherEdVariable
					mc.getEditions().add(ed);
				}
			}
		}

		MagicCardNames defaultMcn = new MagicCardNames();
		defaultMcn.setName(mc.getName());
		defaultMcn.setLanguage("English");
		try {
			defaultMcn.setGathererId(Integer.parseInt(currentEd.getMultiverseid()));
		} catch (Exception e) {
			defaultMcn.setGathererId(0);
		}

		mc.getForeignNames().add(defaultMcn);

		if (obj.get(FOREIGN_NAMES) != null) {
			JsonArray arr = obj.get(FOREIGN_NAMES).getAsJsonArray();
			for (int i = 0; i < arr.size(); i++) {
				JsonObject lang = arr.get(i).getAsJsonObject();
				MagicCardNames mcn = new MagicCardNames();
				mcn.setName(lang.get(NAME).getAsString());
				mcn.setLanguage(lang.get("language").getAsString());

				if (lang.get(MULTIVERSEID) != null)
					mcn.setGathererId(lang.get(MULTIVERSEID).getAsInt());

				mc.getForeignNames().add(mcn);
			}
		}
		notify(mc);

		cacheCards.put(mc.getId(), mc);
		
		
		return mc;
	}

	public void initOtherEdVariable(MagicCard mc, MagicEdition ed) {
		JsonObject root = null;
		JsonObject temp = null;
		try {
			root = URLTools.extractJson(getString(JSON_URL) + "/cards?set=" + ed.getId() + "&name=" + URLTools.encode(mc.getName())).getAsJsonObject();

			temp = root.get("cards").getAsJsonArray().get(0).getAsJsonObject();

			if (temp.get(RARITY) != null)
				ed.setRarity(temp.get(RARITY).getAsString());
			if (temp.get(MULTIVERSEID) != null)
				ed.setMultiverseid(temp.get(MULTIVERSEID).getAsString());
			if (temp.get(NUMBER) != null)
				ed.setNumber(temp.get(NUMBER).getAsString());

		} catch (Exception e) {
			logger.error("ERROR on " + ed.getId() + " " + mc.getName() + ": " + e);
		}

	}

	private MagicEdition generateEdition(JsonObject obj) throws IOException {
		MagicEdition ed = new MagicEdition();
		ed.setId(obj.get("code").getAsString());
		ed.setSet(obj.get(NAME).getAsString());
		ed.setType(obj.get(TYPE).getAsString());
		ed.setReleaseDate(obj.get("releaseDate").getAsString());

		if (obj.get("mkm_id") != null) {
			ed.setMkmid(obj.get("mkm_id").getAsInt());
			ed.setMkmName(obj.get("mkm_name").getAsString());
		}

		if (propsCache.getProperty(ed.getId()) != null)
			ed.setCardCount(Integer.parseInt(propsCache.getProperty(ed.getId())));
		else
			ed.setCardCount(getCount(ed.getId()));
	
		return ed;
	}

	private int getCount(String id) throws IOException {
		int count = URLTools.openConnection(getString(JSON_URL) + "/cards?set=" + id).getHeaderFieldInt("Total-Count", 0);
		propsCache.put(id, String.valueOf(count));
		try {
			logger.trace("update cache " + id);

			try (FileOutputStream fos = new FileOutputStream(fcacheCount)) {
				propsCache.store(fos, new Date().toString());
			}

		} catch (Exception e) {
			logger.error("error in count for " + id, e);
		}
		return count;
	}


	@Override
	public List<MagicEdition> loadEditions() throws IOException {
			JsonArray root = URLTools.extractJson(getString(JSON_URL) + "/sets").getAsJsonObject().get("sets").getAsJsonArray();
			List<MagicEdition> eds = new ArrayList<>();
			for (int i = 0; i < root.size(); i++) {
				JsonObject e = root.get(i).getAsJsonObject();
				MagicEdition ed = generateEdition(e.getAsJsonObject());
				eds.add(ed);
			}
		return eds;
	}

	public String[] getLanguages() {
		return new String[] { "English", "Chinese Simplified", "Chinese Traditional", "French", "German", "Italian",
				"Japanese", "Korean", "Portugese", "Russian", "Spanish" };
	}

	@Override
	public List<String> loadQueryableAttributs() {
		return Lists.newArrayList(NAME, FOREIGN_NAMES, TEXT, ARTIST, TYPE, RARITY, FLAVOR, "cmc", WATERMARK, POWER, TOUGHNESS, LAYOUT);
	}

	@Override
	public String getVersion() {
		return "v1";
	}

	@Override
	public URL getWebSite() throws MalformedURLException {
		return new URL("https://magicthegathering.io/");
	}

	public String getName() {
		return "MTG Developpers.io";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
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
