package org.magic.api.providers.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat;
import org.magic.api.beans.MagicRuling;
import org.magic.api.interfaces.abstracts.AbstractCardsProvider;
import org.magic.services.MTGConstants;
import org.magic.tools.Chrono;
import org.magic.tools.ColorParser;
import org.magic.tools.FileTools;
import org.magic.tools.URLTools;

import com.google.gson.JsonElement;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.ReadContext;
import com.jayway.jsonpath.spi.cache.CacheProvider;
import com.jayway.jsonpath.spi.cache.LRUCache;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;


public class Mtgjson4Provider extends AbstractCardsProvider {

	
	private static final String PRINTINGS = "printings";
	private static final String ARTIST = "artist";
	private static final String TYPE = "type";
	private static final String FOREIGN_DATA = "foreignData";
	private static final String RULINGS = "rulings";
	private static final String LEGALITIES = "legalities";
	private static final String LOYALTY = "loyalty";
	private static final String COLOR_IDENTITY = "colorIdentity";
	private static final String COLORS = "colors";
	private static final String TOUGHNESS = "toughness";
	private static final String POWER = "power";
	private static final String SUBTYPES = "subtypes";
	private static final String TYPES = "types";
	private static final String SUPERTYPES = "supertypes";
	private static final String ORIGINAL_TYPE = "originalType";
	private static final String ORIGINAL_TEXT = "originalText";
	private static final String FLAVOR_TEXT = "flavorText";
	private static final String LAYOUT = "layout";
	private static final String IS_RESERVED = "isReserved";
	private static final String FRAME_VERSION = "frameVersion";
	private static final String CONVERTED_MANA_COST = "convertedManaCost";
	private static final String TEXT = "text";
	private static final String NUMBER = "number";
	private static final String RARITY = "rarity";
	private static final String MULTIVERSE_ID = "multiverseId";
	private static final String MANA_COST = "manaCost";
	private static final String NAME = "name";
	private static final String CARDS_ROOT_SEARCH = ".cards[?(@.";
	private static final String NAMES = "names";

	public static final String URL_JSON_VERSION = "https://mtgjson.com/json/version.json";
	public static final String URL_JSON_ALL_SETS = "https://mtgjson.com/json/AllSets.json";
	public static final String URL_JSON_SETS_LIST="https://mtgjson.com/json/SetList.json";
	public static final String URL_JSON_KEYWORDS="https://mtgjson.com/json/Keywords.json";
	public static final String URL_JSON_ALL_SETS_ZIP ="https://mtgjson.com/json/AllSets.json.zip";
	public static final String URL_JSON_DECKS_LIST = "https://mtgjson.com/json/DeckLists.json";
	public static final String URL_DECKS_URI = "https://mtgjson.com/json/decks/";
	private File fileSetJsonTemp = new File(MTGConstants.DATA_DIR,"AllSets-x4.json.zip");
	private File fileSetJson = new File(MTGConstants.DATA_DIR, "AllSets-x4.json");
	private File fversion = new File(MTGConstants.DATA_DIR, "version4");
	
	private String version;
	private Chrono chrono;
	private ReadContext ctx;
	
	
	public Mtgjson4Provider() {
		super();
		if(CacheProvider.getCache()==null)
			CacheProvider.setCache(new LRUCache(getInt("LRU_CACHE")));
		
	}
	
	

	private boolean hasNewVersion() {
		String temp = "";
		
			try (BufferedReader br = new BufferedReader(new FileReader(fversion))) 
			{
				temp = br.readLine();
			}
			catch(FileNotFoundException ex)
			{
				logger.error(fversion + " doesn't exist"); 
			} catch (IOException e) {
				logger.error(e);
			}
			
			try {
			logger.debug("check new version of " + toString() + " (" + temp + ")");

			JsonElement d = URLTools.extractJson(URL_JSON_VERSION);
			version = d.getAsJsonObject().get("version").getAsString();
			if (!version.equals(temp)) {
				logger.info("new version datafile exist (" + version + "). Downloading it");
				return true;
			}

			logger.debug("check new version of " + this + ": up to date");
			return false;
		} catch (Exception e) {
			version = temp;
			logger.error("Error getting last version ",e);
			return false;
		}

	}

	public void init() {
		logger.info("init " + this);

		chrono=new Chrono();
		cacheBoosterCards = new HashMap<>();

		Configuration.setDefaults(new Configuration.Defaults() {

			private final JsonProvider jsonProvider = new GsonJsonProvider();
			private final MappingProvider mappingProvider = new GsonMappingProvider();

			@Override
			public JsonProvider jsonProvider() {
				return jsonProvider;
			}

			@Override
			public MappingProvider mappingProvider() {
				return mappingProvider;
			}

			@Override
			public Set<Option> options() {
				return EnumSet.noneOf(Option.class);
			}

		});
		Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);

		try {

			logger.debug("loading file " + fileSetJson);

			if (hasNewVersion()||!fileSetJson.exists() || fileSetJson.length() == 0) {
				logger.info("Downloading "+version + " datafile");
				URLTools.download(URL_JSON_ALL_SETS_ZIP, fileSetJsonTemp);
				FileTools.unZipIt(fileSetJsonTemp,fileSetJson);
				FileUtils.writeStringToFile(fversion,version,MTGConstants.DEFAULT_ENCODING,false);
			}
			
			logger.debug(this + " : parsing db file");
			ctx = JsonPath.parse(fileSetJson);
			logger.debug(this + " : parsing OK ");
			
		} catch (Exception e1) {
			logger.error(e1);
		}
	}

	@Override
	public MagicCard getCardById(String id) throws IOException {
		return searchCardByCriteria("uuid", id, null, true).get(0);
	}
	
	@Override
	public List<MagicCard> searchCardByCriteria(String att, String crit, MagicEdition ed, boolean exact) throws IOException {
		
		String filterEdition = ".";

		if (ed != null)
		{
			if(ed.getId().equals("NMS"))
				ed.setId("NEM");
				
			filterEdition = filterEdition + ed.getId().toUpperCase();
		}

		
		String jsquery = "$" + filterEdition + CARDS_ROOT_SEARCH + att + " =~ /^.*" + crit.replaceAll("\\+", " ")+ ".*$/i)]";

		if (exact)
			jsquery = "$" + filterEdition + CARDS_ROOT_SEARCH + att + " == \"" + crit.replaceAll("\\+", " ") + "\")]";

		if (att.equalsIgnoreCase("set")) 
		{
			if (cacheBoosterCards.get(crit) != null) {
				logger.debug(crit + " is already in cache. Loading from it");
				return cacheBoosterCards.get(crit);
			}
			else {
				jsquery = "$." + crit.toUpperCase() + ".cards";
			}
		}
		else if(StringUtils.isNumeric(crit)) {
			jsquery = "$" + filterEdition + CARDS_ROOT_SEARCH + att + " == " + crit + ")]";
		}
		return search(jsquery);
		
	}
	
	@SuppressWarnings("unchecked")
	private List<MagicCard> search(String jsquery) {
		
		List<String> currentSet = new ArrayList<>();
		ArrayList<MagicCard> ret = new ArrayList<>();
		List<Map<String, Object>> cardsElement = ctx.withListeners(fr -> {
			if (fr.path().startsWith("$")) {
				currentSet.add(fr.path().substring(fr.path().indexOf("$[") + 3, fr.path().indexOf("]") - 1));
			}
			return null;
		}).read(jsquery, List.class);
		
		logger.debug("parsing " + jsquery);
		
		int indexSet = 0;
		for (Map<String, Object> map : cardsElement) 
		{
						MagicCard mc = new MagicCard();
						  mc.setFlippable(false);
						  mc.setTranformable(false);
						  mc.setId(String.valueOf(map.get("uuid").toString()));
						  mc.setText(String.valueOf(map.get(TEXT)));
						  
				if (map.get(NAME) != null)
					mc.setName(String.valueOf(map.get(NAME)));
						  
				if (map.get(MANA_COST) != null)
					mc.setCost(String.valueOf(map.get(MANA_COST)));
				else
					mc.setCost("");
				
				if (map.get(MULTIVERSE_ID) != null)
					mc.setMultiverseid((int)Double.parseDouble(map.get(MULTIVERSE_ID).toString()));
				
				if (map.get(RARITY) != null)
					mc.setRarity(String.valueOf(map.get(RARITY)));
				
				if (map.get(NUMBER) != null)
					mc.setNumber(String.valueOf(map.get(NUMBER)));
			
				if (map.get(TEXT) != null)
					mc.setText(String.valueOf(map.get(TEXT)));
				
				if (map.get(CONVERTED_MANA_COST) != null)
					mc.setCmc((int)Double.parseDouble(map.get(CONVERTED_MANA_COST).toString()));
				
				if (map.get(FRAME_VERSION) != null)
					mc.setFrameVersion(String.valueOf(map.get(FRAME_VERSION)));
				
				if (map.get(ARTIST) != null)
					mc.setArtist(String.valueOf(map.get(ARTIST)));
			
				if (map.get(IS_RESERVED) != null)
					mc.setReserved(Boolean.valueOf(String.valueOf(map.get(IS_RESERVED))));
			
				if (map.get(LAYOUT) != null)
					mc.setLayout(String.valueOf(map.get(LAYOUT)));
				
				if (map.get(FLAVOR_TEXT) != null)
					mc.setFlavor(String.valueOf(map.get(FLAVOR_TEXT)));
				
				if (map.get(ORIGINAL_TEXT) != null)
					mc.setOriginalText(String.valueOf(map.get(ORIGINAL_TEXT)));
				
				if (map.get(ORIGINAL_TYPE) != null)
					mc.setOriginalType(String.valueOf(map.get(ORIGINAL_TYPE)));
				
				if (map.get(SUPERTYPES) != null)
					mc.getSupertypes().addAll((List<String>) map.get(SUPERTYPES));
			
				if (map.get(TYPES) != null)
					mc.getTypes().addAll((List<String>) map.get(TYPES));
			
				if (map.get(SUBTYPES) != null)
					mc.getSubtypes().addAll((List<String>) map.get(SUBTYPES));
			
				if (map.get(POWER) != null)
					mc.setPower(String.valueOf(map.get(POWER)));
				
				if (map.get(TOUGHNESS) != null)
					mc.setToughness(String.valueOf(map.get(TOUGHNESS)));
				
				if (map.get(COLORS) != null)
					((List<String>) map.get(COLORS)).forEach(s->mc.getColors().add(ColorParser.getNameByCode(s)));
			
				if (map.get(COLOR_IDENTITY) != null)
					mc.getColorIdentity().addAll((List<String>) map.get(COLOR_IDENTITY));
				
				
				if (map.get(LOYALTY) != null) {
					try {
						mc.setLoyalty((int) Double.parseDouble(map.get(LOYALTY).toString()));
					} catch (Exception e) {
						mc.setLoyalty(0);
					}
				}
				
				if (map.get(LEGALITIES) != null) {
					
					for (Map.Entry<String,String> mapFormats : ((Map<String,String>) map.get(LEGALITIES)).entrySet()) {
						MagicFormat mf = new MagicFormat();
						mf.setFormat(String.valueOf(mapFormats.getKey()));
						mf.setLegality(String.valueOf(mapFormats.getValue()));
						mc.getLegalities().add(mf);
					}
				}
				
				if (map.get(RULINGS) != null) {
					for (Map<String, Object> mapRules : (List<Map>) map.get(RULINGS)) {
						MagicRuling mr = new MagicRuling();
						mr.setDate(String.valueOf(mapRules.get("date")));
						mr.setText(String.valueOf(mapRules.get(TEXT)));
						mc.getRulings().add(mr);
					}
				}
				
				MagicCardNames defnames = new MagicCardNames();
							   if (mc.getMultiverseid() != null)
								   defnames.setGathererId(mc.getMultiverseid());			 
							   defnames.setLanguage("English");
							   defnames.setName(mc.getName());
							   defnames.setText(mc.getText());
							   defnames.setType(mc.getFullType());
			
				mc.getForeignNames().add(defnames);
				
				if (map.get(FOREIGN_DATA) != null) {
					for (Map<String, Object> mapNames : (List<Map>) map.get(FOREIGN_DATA)) {
						MagicCardNames fnames = new MagicCardNames();
									   fnames.setLanguage(String.valueOf(mapNames.get("language")));
									   fnames.setName(String.valueOf(mapNames.get(NAME)));
									   
									   if (mapNames.get(TEXT) != null)
										   fnames.setText(String.valueOf(mapNames.get(TEXT)));
									   
									   if (mapNames.get(TYPE) != null)
										   fnames.setType(String.valueOf(mapNames.get(TYPE)));
									   
									   if (mapNames.get(MULTIVERSE_ID) != null)
										   fnames.setGathererId((int) (double) mapNames.get(MULTIVERSE_ID));
									   
									   if (mapNames.get(FLAVOR_TEXT) != null)
										   fnames.setFlavor(String.valueOf(mapNames.get(FLAVOR_TEXT)));
									   
			
						mc.getForeignNames().add(fnames);
					}
				}
				
				
				String codeEd;
				if (currentSet.size() <= 1)
					codeEd = currentSet.get(0);
				else
					codeEd = currentSet.get(indexSet++);
			
				MagicEdition me = getSetById(codeEd);
							 me.setRarity(mc.getRarity());
							 me.setNumber(mc.getNumber());
							 me.setFlavor(mc.getFlavor());
							 if(mc.getMultiverseid()!=null)
								 me.setMultiverseid(String.valueOf(mc.getMultiverseid()));
							
				mc.getEditions().add(me);
				
				if (!mc.isBasicLand() && map.get(PRINTINGS) != null)
				{
					for (String print : (List<String>) map.get(PRINTINGS)) {
						if (!print.equalsIgnoreCase(codeEd)) {
							MagicEdition meO = getSetById(print);
							initOtherEditionCardsVar(mc, meO);
							mc.getEditions().add(meO);
						}
					}
			
				}
				
				if (mc.getLayout().equals("double-faced") || mc.getLayout().equals("meld") || mc.getLayout().equals("transform"))
					mc.setTranformable(true);
			
				if (mc.getLayout().equals("flip"))
					mc.setFlippable(true);
				
				if( map.get(NAMES) !=null)
				{
					List<String> names = ((List<String>)map.get(NAMES));
					
					if(names.size()==2)
					{
						names.remove(mc.getName());
						mc.setRotatedCardName(names.get(0));
					}
					else if(names.size()>2)
					{
						mc.setRotatedCardName(names.get(1));
						//[Bruna, the Fading Light, Brisela, Voice of Nightmares, Gisela, the Broken Blade]
					}
					
				}
			
		notify(mc);
		ret.add(mc);
	}
		return ret;
	}
	
	private void initOtherEditionCardsVar(MagicCard mc, MagicEdition me) {
		String edCode = me.getId();

		if (!edCode.startsWith("p"))
			edCode = edCode.toUpperCase();

		String jsquery = "$." + edCode + ".cards[?(@.name==\""+ mc.getName().replaceAll("\\+", " ").replaceAll("\"", "\\\\\"") + "\")]";

		List<Map<String, Object>> cardsElement = null;
		try {
			cardsElement = ctx.read(jsquery, List.class);
		} catch (Exception e) {
			logger.error("error in " + jsquery +" " +  e);
			return ;
		}

		if (cardsElement != null)
			for (Map<String, Object> map : cardsElement) {
				try {
					me.setRarity(String.valueOf(map.get(RARITY)));
				} catch (Exception e) {
					me.setRarity(mc.getRarity());
				}
				
				try {
					me.setFlavor(String.valueOf(map.get(FLAVOR_TEXT)));
				} catch (Exception e) {
					me.setFlavor(mc.getFlavor());
				}

				try {

					me.setNumber(String.valueOf(map.get(NUMBER)));
				} catch (Exception e) {
					logger.trace("initOtherEditionCardsVar number not found");
				}
				
				try {

					me.setArtist(String.valueOf(map.get(ARTIST)));
				} catch (Exception e) {
					me.setArtist(mc.getArtist());
				}

				try {
					me.setMultiverseid(String.valueOf((int) (double) map.get(MULTIVERSE_ID)));
				} catch (Exception e) {
					//do nothing
				}
			}
	}

	@Override
	public List<MagicEdition> loadEditions() throws IOException {
		String jsquery = "$.*";

		if (!cacheEditions.values().isEmpty()) {
			logger.trace("editions already loaded.Return cache");
			return new ArrayList<>(cacheEditions.values());
		}
		logger.debug("load editions");
		chrono.start();
		
		try {		
		
		URLTools.extractJson(URL_JSON_SETS_LIST).getAsJsonArray().forEach(e->{
			String codeedition = e.getAsJsonObject().get("code").getAsString().toUpperCase();
			cacheEditions.put(codeedition, getSetById(codeedition));
		});
		
		}catch(Exception ex)
		{
			
			logger.error("Error loading set List from " + URL_JSON_SETS_LIST +". Loading manually");
			final List<String> codeEd = new ArrayList<>();
			ctx.withListeners(fr -> {
				if (fr.path().startsWith("$"))
					codeEd.add(fr.path().substring(fr.path().indexOf("$[") + 3, fr.path().indexOf("]") - 1));
				return null;

			}).read(jsquery, List.class);
			codeEd.forEach(codeedition->cacheEditions.put(codeedition, getSetById(codeedition)));

			
			
		}

		logger.debug("Loading editions OK in " + chrono.stop() + " sec.");
		
		return new ArrayList<>(cacheEditions.values());
	}

	@Override
	public MagicEdition getSetById(String id) {
		
		if(id.startsWith("p"))
			id=id.toUpperCase();
		
		MagicEdition ed = new MagicEdition(id);
		String base = "$." + id.toUpperCase();
		try{
		ed.setSet(ctx.read(base + ".name", String.class));
		}
		catch(PathNotFoundException pnfe)
		{	
			ed.setSet(id);
		}
		
		try{
			ed.setOnlineOnly(ctx.read(base + ".isOnlineOnly", Boolean.class));
		}
		catch(PathNotFoundException pnfe)
		{ 
			//do nothing
		}
		
		try{
			ed.setFoilOnly(ctx.read(base + ".isFoilOnly", Boolean.class));
		}
		catch(PathNotFoundException pnfe)
		{ 
			//do nothing
		}
			
		
		
		try{
		ed.setReleaseDate(ctx.read(base + ".releaseDate", String.class));
		}
		catch(PathNotFoundException pnfe)
		{ 
			//do nothing
		}
		
		try{
		ed.setType(ctx.read(base + ".type", String.class));
		}catch(PathNotFoundException pnfe)
		{ 
			//do nothing
		}
		
		try{
			ed.setBlock(ctx.read(base + ".block", String.class));
		}catch(PathNotFoundException pnfe)
		{ 
			//do nothing
		}
		
		try{
			ed.setBorder(ctx.read(base + ".cards[0].borderColor", String.class));
		}catch(PathNotFoundException pnfe)
		{ 
			//do nothing
		}
		
		try{
			ed.setGathererCode(ctx.read(base + ".mtgoCode", String.class));
		}catch(PathNotFoundException pnfe)
		{ 
			//do nothing
		}
		
		try {
			ed.setCardCountOfficial(ctx.read(base + ".baseSetSize", Integer.class));
		} catch (PathNotFoundException pnfe) {
			// do nothing
		}
		
		
		
		try {
			ed.setCardCount(ctx.read(base + ".totalSetSize", Integer.class));
		} catch (PathNotFoundException pnfe) {
			logger.warn("totalSetSize not found in " + ed.getId() + ", manual calculation");
			if (ed.getCardCount() == 0)
				try {
					Integer i = ctx.read(base + ".cards.length()");
					ed.setCardCount(i);
				} catch (Exception e) {
					ed.setCardCount(0);
				}
		}
		return ed;
	}

	@Override
	public String[] getQueryableAttributs() {
		return new String[] { NAME,"set",ARTIST,TEXT,CONVERTED_MANA_COST,POWER,TOUGHNESS,FLAVOR_TEXT,FRAME_VERSION,IS_RESERVED,LAYOUT,MANA_COST,MULTIVERSE_ID,NUMBER,RARITY,"hasFoil","hasNonFoil"};
	}

	@Override
	public String getName() {
		return "MTGJson4";
	}

	@Override
	public String[] getLanguages() {
		return new String[] { "English", "Spanish", "French", "German", "Italian", "Portuguese", "Japanese", "Korean", "Russian", "Simplified Chinese","Traditional Chinese","Hebrew","Latin","Ancient Greek", "Arabic", "Sanskrit","Phyrexian" };
	}

	@Override
	public MagicCard getCardByNumber(String num, MagicEdition me) throws IOException {
		String jsquery = "$." + me.getId().toUpperCase() + ".cards[?(@.number == '" + num + "')]";
		logger.debug("search " + jsquery);
		try {
			MagicCard mc = search(jsquery).get(0);
			mc.getEditions().add(me);
			return mc;
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}
	
	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public URL getWebSite() throws MalformedURLException {
		return new URL("https://mtgjson.com/v4");
	}


	@Override
	public void initDefault() {
		setProperty("LRU_CACHE", "400");
	}
	
	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}
	

}
