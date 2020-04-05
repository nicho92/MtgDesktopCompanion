package org.magic.api.providers.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.apache.commons.beanutils.BeanUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat;
import org.magic.api.beans.MagicRuling;
import org.magic.api.beans.enums.MTGColor;
import org.magic.api.beans.enums.MTGFrameEffects;
import org.magic.api.beans.enums.MTGLayout;
import org.magic.api.interfaces.abstracts.AbstractCardsProvider;
import org.magic.services.MTGConstants;
import org.magic.services.threads.ThreadManager;
import org.magic.tools.InstallCert;
import org.magic.tools.URLTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ScryFallProvider extends AbstractCardsProvider {

	private static final String CARDS = "/cards/";
	private static final String ILLUSTRATION_ID = "illustration_id";
	private static final String FRAME = "frame";
	private static final String SEARCH_Q = "search?q=";
	private static final String COLOR = "color";
	private static final String WATERMARK = "watermark";
	private static final String TYPE_LINE = "type_line";
	private static final String POWER = "power";
	private static final String LOYALTY = "loyalty";
	private static final String TOUGHNESS = "toughness";
	private static final String RARITY = "rarity";
	private static final String MULTIVERSE_ID = "multiverse_id";
	private static final String DIGITAL = "digital";
	private static final String COLORS = "colors";
	private static final String COLLECTOR_NUMBER = "collector_number";
	private static final String ARTIST = "artist";
	private static final String CARD_FACES = "card_faces";
	private static final String BORDER = "border";
	private static final String NAME = "name";
	private static final String LOAD_CERTIFICATE = "LOAD_CERTIFICATE";
	private static final String BULK_FILE_URL="https://archive.scryfall.com/json/scryfall-all-cards.json";
	
	private String baseURI = "";
	public ScryFallProvider() {
		super();
		if(getBoolean(LOAD_CERTIFICATE))
		{
			try {
				InstallCert.installCert("scryfall.com");
				setProperty(LOAD_CERTIFICATE, "false");
			} catch (Exception e1) {
				logger.error(e1);
			}
		}
	}
	
	
	public void downloadBulkFile(File f) throws IOException
	{
		URLTools.download(BULK_FILE_URL, f);
	}

	
	@Override
	public void initDefault() {
		setProperty(LOAD_CERTIFICATE, "true");
		setProperty("URL", "https://api.scryfall.com");
		setProperty("MULTILANG","false");
		setProperty("LOAD_RULING","true");
	}
	
	@Override
	public void init() {
		baseURI=getString("URL");
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
	public List<MagicCard> listAllCards() throws IOException {
		throw new IOException("Not Implemented");
	}
	
	@Override
	public List<MagicCard> searchCardByCriteria(String att, String crit, MagicEdition me, boolean exact) throws IOException {
		List<MagicCard> list = new ArrayList<>();

		String comparator = crit;

		if (exact)
			comparator = "!\"" + crit + "\"";

		StringBuilder url = new StringBuilder(baseURI);
				url.append(CARDS);
				
		if (att.equals(NAME))
			url.append(SEARCH_Q).append(URLTools.encode("++" + comparator + " include:extras"));
		else if (att.equals("custom"))
			url.append(SEARCH_Q).append(URLTools.encode(crit));
		else if (att.equals("set"))
			url.append(SEARCH_Q).append(URLTools.encode("++e:" + crit));
		else if (att.equals("id"))
			url.append(URLTools.encode(crit));
		else
			url.append(SEARCH_Q).append(URLTools.encode(att + ":" + comparator + " include:extras"));

		if (me != null)
			url.append("%20").append(URLTools.encode("e:" + me.getId()));

		boolean hasMore = true;
		while (hasMore) {

			logger.debug(URLDecoder.decode(url.toString(), MTGConstants.DEFAULT_ENCODING.displayName()));
			try {
				JsonElement el = URLTools.extractJson(url.toString()) ;

				if (att.equals("id")) {
					list.add(loadCard(el.getAsJsonObject(), exact, crit));
					hasMore = false;
				} else {
					JsonArray jsonList = el.getAsJsonObject().getAsJsonArray("data");
					for (int i = 0; i < jsonList.size(); i++) {
						MagicCard mc = loadCard(jsonList.get(i).getAsJsonObject(), exact, crit);
						list.add(mc);
						
					}
					hasMore = el.getAsJsonObject().get("has_more").getAsBoolean();

					if (hasMore)
						url = new StringBuilder(el.getAsJsonObject().get("next_page").getAsString());

					Thread.sleep(50);
					
				}
			} catch (Exception e) {
				logger.error("erreur", e);
				hasMore = false;
			}
		}
		return list;
	}



	@Override
	public MagicCard getCardByNumber(String id, MagicEdition me) throws IOException {
		String url = baseURI + CARDS + me.getId().toLowerCase() + "/" + id;
		JsonObject root =  URLTools.extractJson(url).getAsJsonObject();
		return loadCard(root, true, null);
	}

	@Override
	public List<MagicEdition> loadEditions() throws IOException {
		if (cacheEditions.size() <= 0) {
			String url = baseURI + "/sets";
			JsonObject root = URLTools.extractJson(url).getAsJsonObject(); 
			for (int i = 0; i < root.get("data").getAsJsonArray().size(); i++) {
				JsonObject e = root.get("data").getAsJsonArray().get(i).getAsJsonObject();
				MagicEdition ed = generateEdition(e.getAsJsonObject());
				cacheEditions.put(ed.getId(), ed);
			}
		}
		return new ArrayList<>(cacheEditions.values());
	}

	@Override
	public MagicEdition getSetById(String id) throws IOException {
		
		if (cacheEditions.size() > 0) {
			for (MagicEdition ed : cacheEditions.values())
				if (ed.getId().equalsIgnoreCase(id))
					try {
						return (MagicEdition) BeanUtils.cloneBean(ed);
					} catch (Exception e) {
						new MagicEdition(id);
					}
		}
		try {
			JsonObject root =URLTools.extractJson(baseURI + "/sets/" + id.toLowerCase()).getAsJsonObject();
			return generateEdition(root.getAsJsonObject());
		} catch (Exception e) {
			return new MagicEdition(id,id);
		}

	}

	@Override
	public String[] getLanguages() {
		return new String[] { "en","es","fr","de","it","pt","ja","ru","zhs","he","ar"};
	}

	@Override
	public String[] getQueryableAttributs() {
		return new String[] { NAME, "custom", "type", COLOR, "oracle", "mana", "cmc", POWER, TOUGHNESS,LOYALTY, "is", RARITY, "cube", ARTIST, "flavor", WATERMARK, BORDER, FRAME, "set" };
	}


	@Override
	public String getVersion() {
		return "2.0";
	}

	@Override
	public URL getWebSite() throws MalformedURLException {
		return new URL("https://scryfall.com/");
	}

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

	@Override
	public String getName() {
		return "ScryFall";
	}

	private MagicCard loadCard(JsonObject obj, boolean exact, String search){
		
		try {
			return cacheCards.get(obj.get("id").getAsString(), new Callable<MagicCard>() {
				
				@Override
				public MagicCard call() throws Exception {
					return generateCard(obj, exact, search);
				}
			});
		} catch (ExecutionException e) {
			logger.error("error loading cards", e);
			return null;
		}
		
		
	}
	

	private MagicCard generateCard(JsonObject obj, boolean exact, String search) throws IOException {
		MagicCard mc = new MagicCard();

		mc.setId(obj.get("id").getAsString());
		mc.setName(obj.get(NAME).getAsString());
		mc.setCmc(obj.get("cmc").getAsInt());
		mc.setLayout(MTGLayout.parseByLabel(obj.get("layout").getAsString()));
		mc.setOversized(obj.get("oversized").getAsBoolean());
		try {
			mc.setText(obj.get("oracle_text").getAsString());
		} catch (NullPointerException e) {
			mc.setText("");
		}
		try {
			mc.setCost(obj.get("mana_cost").getAsString());
		} catch (NullPointerException e) {
			mc.setCost("");
		}
		try {
			mc.setFlavor(obj.get("flavor_text").getAsString());
		} catch (NullPointerException e) {
			mc.setFlavor("");
		}

		if (obj.get(TYPE_LINE) != null)
			generateTypes(mc, String.valueOf(obj.get(TYPE_LINE)));

		MagicCardNames n = new MagicCardNames();
		n.setLanguage("English");
		n.setName(mc.getName());
		try {
			n.setGathererId(obj.get(MULTIVERSE_ID).getAsInt());
		} catch (NullPointerException e) {
			n.setGathererId(0);
		}

		mc.getForeignNames().add(n);

		try {
			mc.setArtist(obj.get(ARTIST).getAsString());
		} catch (NullPointerException e) {
			logger.trace("artist not found");
		}
		try {
			mc.setReserved(obj.get("reserved").getAsBoolean());
		} catch (NullPointerException e) {
			logger.trace("reserved not found");
		}
		try {
			mc.setPower(obj.get(POWER).getAsString());
		} catch (NullPointerException e) {
			logger.trace("power not found");
		}
		try {
			mc.setToughness(obj.get(TOUGHNESS).getAsString());
		} catch (NullPointerException e) {
			logger.trace("toughness not found");
		}
		try {
			mc.setLoyalty(obj.get(LOYALTY).getAsInt());
		} catch (Exception e) {
			logger.trace("loyalty not found");
		}
		try {
			mc.setWatermarks(obj.get(WATERMARK).getAsString());
		} catch (NullPointerException e) {
			logger.trace("watermark not found");
		}
		try {
			mc.setFrameVersion(obj.get(FRAME).getAsString());
		} catch (NullPointerException e) {
			logger.trace("frame not found");
		}
		
		try {
			mc.setTcgPlayerId(obj.get("tcgplayer_id").getAsInt());
		} catch (NullPointerException e) {
			logger.trace("tcgplayerid not found");
		}
		
		try {
			mc.setImageName(obj.get(ILLUSTRATION_ID).getAsString());
		} catch (NullPointerException e) {
			logger.trace("illustration_id not found");
		}
		
		if(obj.get("frame_effects")!=null) {
			Iterator<JsonElement> it = obj.get("frame_effects").getAsJsonArray().iterator();
				while (it.hasNext())
					mc.getFrameEffects().add(MTGFrameEffects.parseByLabel(it.next().getAsString()));
		}
		
		
		if (obj.get(COLORS) != null) {
			Iterator<JsonElement> it = obj.get(COLORS).getAsJsonArray().iterator();
			while (it.hasNext())
				mc.getColors().add(MTGColor.colorByCode(it.next().getAsString()));

		}

		if (obj.get("color_identity") != null) {
			Iterator<JsonElement> it = obj.get("color_identity").getAsJsonArray().iterator();
			while (it.hasNext())
				mc.getColorIdentity().add(MTGColor.colorByCode(it.next().getAsString()));
		}

		if (obj.get("legalities") != null) {
			JsonObject legs = obj.get("legalities").getAsJsonObject();
			Iterator<Entry<String, JsonElement>> it = legs.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, JsonElement> ent = it.next();
				MagicFormat format = new MagicFormat();
				format.setFormat(ent.getKey());
				format.setLegality(ent.getValue().getAsString().equalsIgnoreCase("legal"));
				mc.getLegalities().add(format);
			}
		}

		mc.setTranformable(mc.getLayout()==MTGLayout.TRANSFORM || mc.getLayout()==MTGLayout.MELD);
		mc.setFlippable(mc.getLayout()==MTGLayout.FLIP);
		
		int idface = 0;

		if (mc.getName().contains("//")) {
			String[] names = mc.getName().split(" // ");
			if (exact)
			{
				if (names[0].equals(search)) {
					idface = 0;
				} else {
					idface = 1;
				}
			}
		}
		if (obj.get(CARD_FACES) != null) {
			mc.setText(obj.get(CARD_FACES).getAsJsonArray().get(idface).getAsJsonObject().get("oracle_text")
					.getAsString());
			mc.setCost(obj.get(CARD_FACES).getAsJsonArray().get(idface).getAsJsonObject().get("mana_cost")
					.getAsString());
			mc.setRotatedCardName(
					obj.get(CARD_FACES).getAsJsonArray().get(1).getAsJsonObject().get(NAME).getAsString());
			
			if(obj.get(CARD_FACES).getAsJsonArray().get(idface).getAsJsonObject().get(ILLUSTRATION_ID)!=null)
				mc.setImageName(obj.get(CARD_FACES).getAsJsonArray().get(idface).getAsJsonObject().get(ILLUSTRATION_ID).getAsString());

			generateTypes(mc, obj.get(CARD_FACES).getAsJsonArray().get(idface).getAsJsonObject().get(TYPE_LINE)
					.getAsString());

			try {
				mc.setLoyalty(
						obj.get(CARD_FACES).getAsJsonArray().get(idface).getAsJsonObject().get(LOYALTY).getAsInt());
			} catch (Exception e) {
				logger.error(mc.getName() + " has no loyalty: " + e);
			}

			try {
				Iterator<JsonElement> it = obj.get(CARD_FACES).getAsJsonArray().get(idface).getAsJsonObject()
						.get(COLORS).getAsJsonArray().iterator();
				while (it.hasNext())
					mc.getColors().add(MTGColor.colorByCode(it.next().getAsString()));
			} catch (Exception e) {
				logger.error(mc.getName() + " has no colors: " + e);
			}
			try {
				mc.setPower(obj.get(CARD_FACES).getAsJsonArray().get(idface).getAsJsonObject().get(POWER)
						.getAsString());
				mc.setToughness(obj.get(CARD_FACES).getAsJsonArray().get(idface).getAsJsonObject().get(TOUGHNESS)
						.getAsString());
			} catch (Exception e) {
				logger.error(mc.getName() + " has no power/toughness: " + e);

			}
		}

		// meld
		if (obj.get("all_parts") != null) {
			JsonArray arr = obj.get("all_parts").getAsJsonArray();

			int index = -1;
			for (int i = 0; i < arr.size(); i++) {
				if (arr.get(i).getAsJsonObject().get(NAME).getAsString().equals(mc.getName())) {
					index = i;
					break;
				}

			}
			arr.remove(index);
			if (arr.size() == 1)
				mc.setRotatedCardName(arr.get(0).getAsJsonObject().get(NAME).getAsString());
		}

		MagicEdition ed = null;
		
			try {
				ed = (MagicEdition) BeanUtils.cloneBean(getSetById(obj.get("set").getAsString()));
				ed.setRarity(obj.get(RARITY).getAsString());
				ed.setOnlineOnly(obj.get(DIGITAL).getAsBoolean());
				ed.setNumber(obj.get(COLLECTOR_NUMBER).getAsString());
				ed.setArtist(mc.getArtist());
				mc.getEditions().add(ed);
			} catch (Exception e2) {
				throw new IOException(e2);
			} 

			
			if (obj.get("multiverse_ids") != null) {
				try {
					ed.setMultiverseid(String.valueOf(obj.get("multiverse_ids").getAsJsonArray().get(idface).getAsInt()));
				} catch (Exception e1) {
					logger.error("No multiverseID found for " + mc.getName() + " face : " + idface);
				}

			}
		
		
		ThreadManager.getInstance().executeThread(() -> {
			try {
				if (!mc.isBasicLand())
					initOtherEdition(mc);
					
				generateRules(mc);
			} catch (Exception e) {
				logger.error("error in initOtherEdition :" + e.getMessage());
			}
		}, "other editions");

		notify(mc);
		return mc;

	}

	private void generateRules(MagicCard mc) throws IOException {
		
		if(getBoolean("LOAD_RULING"))
		{
			String url = getString("URL")+CARDS + mc.getId() + "/rulings";
		
			JsonElement el = URLTools.extractJson(url);
			JsonArray arr = el.getAsJsonObject().get("data").getAsJsonArray();
	
			for (int i = 0; i < arr.size(); i++) {
				JsonObject obr = arr.get(i).getAsJsonObject();
				MagicRuling rul = new MagicRuling();
				rul.setDate(obr.get("published_at").getAsString());
				rul.setText(obr.get("comment").getAsString());
	
				mc.getRulings().add(rul);
			}
		}
	}

	private void generateTypes(MagicCard mc, String line) {

		line = line.replaceAll("\"", "");

		for (String k : new String[] { "Legendary", "Basic", "Ongoing", "Snow", "World" }) {
			if (line.contains(k)) {
				mc.getSupertypes().add(k);
				line = line.replaceAll(k, "").trim();
			}
		}

		String sep = "\u2014";

		if (line.contains(sep)) {

			for (String s : line.substring(0, line.indexOf(sep)).trim().split(" "))
				mc.getTypes().add(s.replaceAll("\"", ""));

			for (String s : line.substring(line.indexOf(sep) + 1).trim().split(" "))
				mc.getSubtypes().add(s);
		} else {
			for (String s : line.split(" "))
				mc.getTypes().add(s.replaceAll("\"", ""));
		}

	}

	private void initOtherEdition(MagicCard mc) throws IOException {

		String url = baseURI + "/cards/search?q=+" + URLTools.encode("++!\"" + mc.getName() + "\"")+ "%20include:extras" + "%20-s:" + mc.getCurrentSet().getId();

		logger.trace("initOtherEdition " + URLDecoder.decode(url, MTGConstants.DEFAULT_ENCODING.displayName()));

		boolean hasMore = true;
		while (hasMore) {
	
			try {
				JsonElement el = URLTools.extractJson(url);

				JsonArray jsonList = el.getAsJsonObject().getAsJsonArray("data");
				for (int i = 0; i < jsonList.size(); i++) {
					JsonObject obj = jsonList.get(i).getAsJsonObject();
					MagicEdition ed = getSetById(obj.get("set").getAsString());

					if (obj.get(ARTIST) != null)
						ed.setArtist(obj.get(ARTIST).getAsString());

					if (obj.get(MULTIVERSE_ID) != null)
						ed.setMultiverseid(obj.get(MULTIVERSE_ID).getAsString());

					if (obj.get(RARITY) != null)
						ed.setRarity(obj.get(RARITY).getAsString());

					if (obj.get(COLLECTOR_NUMBER) != null)
						ed.setNumber(obj.get(COLLECTOR_NUMBER).getAsString());

					mc.getEditions().add(ed);
				}
				hasMore = el.getAsJsonObject().get("has_more").getAsBoolean();

				if (hasMore)
					url = el.getAsJsonObject().get("next_page").getAsString();

				Thread.sleep(50);
			} catch (Exception e) {
				logger.trace(e);
				hasMore = false;
			}
		}
	}

	private MagicEdition generateEdition(JsonObject obj) {
		MagicEdition ed = new MagicEdition();
		ed.setId(obj.get("code").getAsString());
		ed.setSet(obj.get(NAME).getAsString());
		ed.setType(obj.get("set_type").getAsString());

		if (obj.get(DIGITAL) != null)
			ed.setOnlineOnly(obj.get(DIGITAL).getAsBoolean());

		if (obj.get(BORDER) != null)
			ed.setBorder(obj.get(BORDER).getAsString());

		if(obj.get("foil_only") !=null)
			ed.setFoilOnly(obj.get("foil_only").getAsBoolean());
		
		ed.setCardCount(obj.get("card_count").getAsInt());
		ed.setCardCountOfficial(ed.getCardCount());
		
		
		if (obj.get("block") != null)
			ed.setBlock(obj.get("block").getAsString());

		if (obj.get("released_at") != null)
			ed.setReleaseDate(obj.get("released_at").getAsString());

		notify(ed);
		
		return ed;
	}


}
