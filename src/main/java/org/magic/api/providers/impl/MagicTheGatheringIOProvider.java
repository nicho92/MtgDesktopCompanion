package org.magic.api.providers.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.Booster;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat;
import org.magic.api.beans.MagicRuling;
import org.magic.api.interfaces.abstracts.AbstractCardsProvider;
import org.magic.services.MTGConstants;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class MagicTheGatheringIOProvider extends AbstractCardsProvider {

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
	
	
	private String jsonUrl = "https://api.magicthegathering.io/v1";
	private File fcacheCount = new File(confdir, "mtgio.cache");
	private Properties propsCache;
	
	public MagicTheGatheringIOProvider() {
		super();
		init();

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
	public MagicCard getCardById(String id) throws IOException {
		return searchCardByCriteria("id", id, null, true).get(0);
	}

	@Override
	public List<MagicCard> searchCardByCriteria(String att, String crit, MagicEdition me, boolean exact)
			throws IOException {
		List<MagicCard> lists = new ArrayList<>();
		URLConnection con = null;
		int page = 1;
		String url = jsonUrl + "/cards?" + att + "=" + URLEncoder.encode(crit, MTGConstants.DEFAULT_ENCODING);
		logger.debug(url);

		con = getConnection(url);
		JsonReader reader;

		int count = 0;
		int totalcount = con.getHeaderFieldInt("Total-Count", 0);

		while (count < totalcount) {
			url = jsonUrl + "/cards?" + att + "=" + URLEncoder.encode(crit, MTGConstants.DEFAULT_ENCODING) + "&page=" + page++;
			logger.debug(url);
			con = getConnection(url);
			reader = new JsonReader(new InputStreamReader(con.getInputStream(), MTGConstants.DEFAULT_ENCODING));
			JsonArray jsonList = new JsonParser().parse(reader).getAsJsonObject().getAsJsonArray("cards");
			for (int i = 0; i < jsonList.size(); i++) {
				lists.add(generateCard(jsonList.get(i).getAsJsonObject()));
			}
			count += con.getHeaderFieldInt("Count", 0);
		}
		return lists;
	}

	@Override
	public MagicCard getCardByNumber(String id, MagicEdition me) throws IOException {
		return searchCardByCriteria(NUMBER, id, me, true).get(0);
	}

	private MagicCard generateCard(JsonObject obj) throws IOException {
		MagicCard mc = new MagicCard();

		if (obj.get("id") != null)
			mc.setId(obj.get("id").getAsString());

		
		if(cacheCards.get(mc.getId())!=null)
			return cacheCards.get(mc.getId());
		
		if (obj.get("name") != null)
			mc.setName(obj.get("name").getAsString());

		if (obj.get("manaCost") != null)
			mc.setCost(obj.get("manaCost").getAsString());

		if (obj.get(TEXT) != null)
			mc.setText(obj.get(TEXT).getAsString());

		if (obj.get(ORIGINAL_TEXT) != null)
			mc.setOriginalText(obj.get(ORIGINAL_TEXT).getAsString());

	
		if (obj.get(ARTIST) != null)
			mc.setArtist(obj.get(ARTIST).getAsString());

		if (obj.get("cmc") != null)
			mc.setCmc(obj.get("cmc").getAsInt());

		if (obj.get(LAYOUT) != null)
			mc.setLayout(obj.get(LAYOUT).getAsString());

		if (obj.get(NUMBER) != null)
			mc.setNumber(obj.get(NUMBER).getAsString());

		if (obj.get(POWER) != null)
			mc.setPower(obj.get(POWER).getAsString());

		if (obj.get(TOUGHNESS) != null)
			mc.setToughness(obj.get(TOUGHNESS).getAsString());

		if (obj.get("loyalty") != null)
			mc.setLoyalty(obj.get("loyalty").getAsInt());

		if (obj.get("mciNumber") != null)
			mc.setMciNumber(String.valueOf(obj.get("mciNumber")));

		if (obj.get("colors") != null) {
			Iterator<JsonElement> it = obj.get("colors").getAsJsonArray().iterator();
			while (it.hasNext())
				mc.getColors().add(it.next().getAsString());
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
				MagicFormat format = new MagicFormat();
				format.setFormat(k.get("format").getAsString());
				format.setLegality(k.get("legality").getAsString());
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

		if (obj.get("names") != null) {
			JsonArray arr = obj.get("names").getAsJsonArray();

			List<String> list = new ArrayList<>();
			

			list.remove(mc.getName());

			String rotateName = (list.get(list.size() - 1));
			mc.setRotatedCardName(rotateName);

			if (mc.getLayout().equals("flip"))
				mc.setFlippable(true);
			if (mc.getLayout().equals("double-faced") || mc.getLayout().equals("meld"))
				mc.setTranformable(true);
		}

		String currentSet = obj.get("set").getAsString();
		MagicEdition currentEd = getSetById(currentSet);

		if (obj.get(MULTIVERSEID) != null)
			currentEd.setMultiverseid(obj.get(MULTIVERSEID).getAsString());

		if (obj.get(RARITY) != null)
			currentEd.setRarity(obj.get(RARITY).getAsString());

		currentEd.setNumber(mc.getNumber());

		mc.getEditions().add(0, currentEd);

		if (obj.get("printings") != null) {
			JsonArray arr = obj.get("printings").getAsJsonArray();
			for (int i = 0; i < arr.size(); i++) {
				String k = arr.get(i).getAsString();
				if (!k.equals(currentSet)) {
					MagicEdition ed = getSetById(k);
					initOtherEdVariable(mc, ed);
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
				mcn.setName(lang.get("name").getAsString());
				mcn.setLanguage(lang.get("language").getAsString());

				if (lang.get(MULTIVERSEID) != null)
					mcn.setGathererId(lang.get(MULTIVERSEID).getAsInt());

				mc.getForeignNames().add(mcn);
			}
		}
		setChanged();
		notifyObservers(mc);

		cacheCards.put(mc.getId(), mc);
		
		
		return mc;
	}

	private void initOtherEdVariable(MagicCard mc, MagicEdition ed) {
		JsonReader reader;
		JsonObject root = null;
		JsonObject temp = null;
		try {
			reader = new JsonReader(new InputStreamReader(getConnection(
					jsonUrl + "/cards?set=" + ed.getId() + "&name=" + URLEncoder.encode(mc.getName(), MTGConstants.DEFAULT_ENCODING))
							.getInputStream(),
					MTGConstants.DEFAULT_ENCODING));
			root = new JsonParser().parse(reader).getAsJsonObject();

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
		ed.setSet(obj.get("name").getAsString());
		ed.setType(obj.get("type").getAsString());
		ed.setBorder(obj.get("border").getAsString());
		ed.setReleaseDate(obj.get("releaseDate").getAsString());

		if (obj.get("mkm_id") != null) {
			ed.setMkmid(obj.get("mkm_id").getAsInt());
			ed.setMkmName(obj.get("mkm_name").getAsString());
		}

		if (obj.get("magicCardsInfoCode") != null)
			ed.setMagicCardsInfoCode(obj.get("magicCardsInfoCode").getAsString());

		if (propsCache.getProperty(ed.getId()) != null)
			ed.setCardCount(Integer.parseInt(propsCache.getProperty(ed.getId())));
		else
			ed.setCardCount(getCount(ed.getId()));

		return ed;
	}

	private int getCount(String id) throws IOException {
		int count = getConnection(jsonUrl + "/cards?set=" + id).getHeaderFieldInt("Total-Count", 0);
		propsCache.put(id, String.valueOf(count));
		try {
			logger.info("update cache " + id);

			try (FileOutputStream fos = new FileOutputStream(fcacheCount)) {
				propsCache.store(fos, new Date().toString());
			}

		} catch (Exception e) {
			logger.error("error in count for " + id, e);
		}
		return count;
	}

	private URLConnection getConnection(String url) throws IOException {
		logger.debug("get stream from " + url);
		URLConnection connection = new URL(url).openConnection();
		connection.setRequestProperty("User-Agent",MTGConstants.USER_AGENT);
		connection.connect();
		return connection;

	}

	@Override
	public List<MagicEdition> loadEditions() throws IOException {
		if (cacheEditions.size() == 0) {
			String url = jsonUrl + "/sets";
			String rootKey = "sets";

			logger.info("connect to " + url);

			URLConnection con = getConnection(url);

			JsonReader reader = new JsonReader(new InputStreamReader(con.getInputStream(), MTGConstants.DEFAULT_ENCODING));
			JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
			for (int i = 0; i < root.get(rootKey).getAsJsonArray().size(); i++) {
				JsonObject e = root.get(rootKey).getAsJsonArray().get(i).getAsJsonObject();
				MagicEdition ed = generateEdition(e.getAsJsonObject());
				cacheEditions.put(ed.getId(), ed);
			}
		}
		return new ArrayList<>(cacheEditions.values());
	}

	@Override
	public MagicEdition getSetById(String id) throws IOException {
		logger.debug("get Set " + id);

		if (cacheEditions.get(id) != null)
			return cacheEditions.get(id);

		JsonReader reader = new JsonReader(
				new InputStreamReader(getConnection(jsonUrl + "/sets/" + id).getInputStream(), MTGConstants.DEFAULT_ENCODING));
		JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
		return generateEdition(root.getAsJsonObject("set"));
	}

	public String[] getLanguages() {
		return new String[] { "English", "Chinese Simplified", "Chinese Traditional", "French", "German", "Italian",
				"Japanese", "Korean", "Portugese", "Russian", "Spanish" };
	}

	@Override
	public String[] getQueryableAttributs() {
		return new String[] { "name", FOREIGN_NAMES, TEXT, ARTIST, "type", RARITY, "flavor", "cmc", "set",
				"watermark", POWER, TOUGHNESS, LAYOUT };
	}

	@Override
	public Booster generateBooster(MagicEdition me) throws IOException {
		return null;
	}

	@Override
	public String getVersion() {
		return "v1";
	}

	@Override
	public URL getWebSite() throws MalformedURLException {
		return new URL("http://magicthegathering.io/");
	}

	public String getName() {
		return "MTG Developpers.io";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public void initDefault() {
		//do nothing

	}

}
