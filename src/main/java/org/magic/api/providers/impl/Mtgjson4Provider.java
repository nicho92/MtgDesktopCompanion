package org.magic.api.providers.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat;
import org.magic.api.beans.MagicFormat.AUTHORIZATION;
import org.magic.api.beans.MagicRuling;
import org.magic.api.beans.enums.MTGColor;
import org.magic.api.beans.enums.MTGFrameEffects;
import org.magic.api.beans.enums.MTGLayout;
import org.magic.api.beans.enums.MTGRarity;
import org.magic.api.criterias.MTGCrit;
import org.magic.api.interfaces.abstracts.AbstractMTGJsonProvider;
import org.magic.services.MTGConstants;
import org.magic.tools.URLTools;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
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


public class Mtgjson4Provider extends AbstractMTGJsonProvider {

	
	private static final String CARDS_ROOT_SEARCH = ".cards[?(@.";
	private ReadContext ctx;
	
	
	@Override
	public File getDataFile() {
		return new File(MTGConstants.DATA_DIR, "AllSets-x4.json");
	}
	
	@Override
	public String getOnlineDataFileZip() {
		return "https://mtgjson.com/json/AllSets.json.zip";
	}
	
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEPRECATED;
	}
	
	
	public Mtgjson4Provider() {
		super();
		if(CacheProvider.getCache()==null)
			CacheProvider.setCache(new LRUCache(getInt("LRU_CACHE")));
		
	}

	public void init() {
		logger.info("init " + this);
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

			download();
			chrono.start();
			logger.debug(this + " : parsing db file");
			ctx = JsonPath.parse(getDataFile());
			logger.debug(this + " : parsing OK in " + chrono.stop()+"s");
			
		} catch (Exception e1) {
			logger.error(e1);
		}
	}
	

	@Override
	public List<MagicCard> searchByCriteria(MTGCrit<?>... crits) throws IOException {
		return search("$..cards"+getMTGQueryManager().build(crits));
	}

	@Override
	public MagicCard getCardByNumber(String num, MagicEdition me) throws IOException {
		
		if(me==null)
			throw new IOException("Edition must not be null");
		
		String jsquery = "$." + me.getId().toUpperCase() + ".cards[?(@.number == '" + num + "')]";
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

		if (att.equalsIgnoreCase(SET_FIELD)) 
		{
				jsquery = "$." + crit.toUpperCase() + ".cards";
		}
		else if(att.equals("jsonpath"))
		{
			jsquery = crit;
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
				currentSet.add(fr.path().substring(fr.path().indexOf("$[") + 3, fr.path().indexOf(']') - 1));
			}
			return null;
		}).read(jsquery, List.class);
		
		logger.debug("parsing " + jsquery);
		
		int indexSet = 0;
		for (Map<String, Object> map : cardsElement) 
		{
				MagicCard mc = new MagicCard();
				  mc.setId(String.valueOf(map.get("uuid").toString()));
				  mc.setText(String.valueOf(map.get(TEXT)));
						  
				if (map.get(NAME) != null)
					mc.setName(String.valueOf(map.get(NAME)));
						  
				if (map.get(MANA_COST) != null)
					mc.setCost(String.valueOf(map.get(MANA_COST)));
				else
					mc.setCost("");
				
				if (map.get(RARITY) != null)
					mc.setRarity(MTGRarity.rarityByName(String.valueOf(map.get(RARITY))));
			
				if (map.get(TEXT) != null)
					mc.setText(String.valueOf(map.get(TEXT)));
				
				if (map.get(CONVERTED_MANA_COST) != null)
					mc.setCmc((int)Double.parseDouble(map.get(CONVERTED_MANA_COST).toString()));
				
				if (map.get(FRAME_VERSION) != null)
					mc.setFrameVersion(String.valueOf(map.get(FRAME_VERSION)));
				
				if (map.get("flavorName") != null)
					mc.setFlavorName(String.valueOf(map.get("flavorName")));
				
				if (map.get("side") != null)
					mc.setSide(String.valueOf(map.get("side")));
				
				if (map.get(ARTIST) != null)
					mc.setArtist(String.valueOf(map.get(ARTIST)));
			
				if (map.get(IS_RESERVED) != null)
					mc.setReserved(Boolean.valueOf(String.valueOf(map.get(IS_RESERVED))));
				
				if (map.get("isOversized") != null)
					mc.setOversized(Boolean.valueOf(String.valueOf(map.get("isOversized"))));
			
				if (map.get(LAYOUT) != null)
					mc.setLayout(MTGLayout.parseByLabel(String.valueOf(map.get(LAYOUT))));
				
				if (map.get(FLAVOR_TEXT) != null)
					mc.setFlavor(String.valueOf(map.get(FLAVOR_TEXT)));
				
				if (map.get("tcgplayerProductId") != null) {
					mc.setTcgPlayerId((int)Double.parseDouble(map.get("tcgplayerProductId").toString()));
				}
				

				if(map.get("isStorySpotlight")!=null)
					mc.setStorySpotlight(Boolean.valueOf(map.get("isStorySpotlight").toString()));
				
				
				if (map.get("mcmId") != null) {
					mc.setMkmId((int)Double.parseDouble(map.get("mcmId").toString()));
				}
				
				if (map.get("mtgstocksId") != null) {
					mc.setMtgstocksId(Double.valueOf(map.get("mtgstocksId").toString()).intValue());
				}
				
				if (map.get("edhrecRank") != null) {
					mc.setEdhrecRank(Double.valueOf(map.get("edhrecRank").toString()).intValue());
				}
				
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
				
				if (map.get("hasNoDeckLimit") != null)
					mc.setHasAlternativeDeckLimit(Boolean.valueOf(map.get("hasNoDeckLimit").toString()));

				
				 
				
				if (map.get(TOUGHNESS) != null)
					mc.setToughness(String.valueOf(map.get(TOUGHNESS)));
				
				if (map.get(COLORS) != null)
					mc.getColors().addAll(MTGColor.parseByCode(((List<String>) map.get(COLORS))));
			
				if (map.get(COLOR_IDENTITY) != null)
					mc.getColorIdentity().addAll(MTGColor.parseByCode(((List<String>) map.get(COLOR_IDENTITY))));
				
				if (map.get("frameEffects") != null)
					mc.getFrameEffects().addAll(MTGFrameEffects.parseByLabel(((List<String>) map.get("frameEffects"))));
				
				if (map.get("isMtgo") != null)
					mc.setMtgoCard(Boolean.valueOf(map.get("isMtgo").toString()));
				
				if (map.get("isArena") != null)
					mc.setArenaCard(Boolean.valueOf(map.get("isArena").toString()));
				
				if (map.get("watermark") != null)
					mc.setWatermarks(String.valueOf(map.get("watermark")));
				
				if (map.get("isPromo") != null)
					mc.setPromoCard(Boolean.valueOf(map.get("isPromo").toString()));
				
				if (map.get("mtgArenaId") != null)
					mc.setMtgArenaId(Double.valueOf(map.get("mtgArenaId").toString()).intValue());
			
				if (map.get("isReprint") != null)
					mc.setReprintedCard(Boolean.valueOf(map.get("isReprint").toString()));
				
				if (map.get("scryfallIllustrationId") != null)
					mc.setScryfallIllustrationId(String.valueOf(map.get("scryfallIllustrationId")));
				
				if (map.get(SCRYFALL_ID) != null)
					mc.setScryfallId(String.valueOf(map.get(SCRYFALL_ID)));
				
				if (map.get(LOYALTY) != null) {
					try {
						mc.setLoyalty((int) Double.parseDouble(map.get(LOYALTY).toString()));
					} catch (Exception e) {
						mc.setLoyalty(0);
					}
				}
				
				if (map.get(LEGALITIES) != null) {
					
					for (Map.Entry<String,String> mapFormats : ((Map<String,String>) map.get(LEGALITIES)).entrySet()) {
						MagicFormat mf = new MagicFormat(String.valueOf(mapFormats.getKey()),AUTHORIZATION.valueOf(String.valueOf(mapFormats.getValue()).toUpperCase()));
						mc.getLegalities().add(mf);
					}
				}
				
				if (map.get(RULINGS) != null) {
					for (Map<String, Object> mapRules : (List<Map<String,Object>>) map.get(RULINGS)) {
						MagicRuling mr = new MagicRuling();
						mr.setDate(String.valueOf(mapRules.get("date")));
						mr.setText(String.valueOf(mapRules.get(TEXT)));
						mc.getRulings().add(mr);
					}
				}
				
				MagicCardNames defnames = new MagicCardNames();
				
						if(map.get(MULTIVERSE_ID)!=null)
							   defnames.setGathererId((int)Double.parseDouble(map.get(MULTIVERSE_ID).toString()));
						
							   defnames.setLanguage("English");
							   defnames.setName(mc.getName());
							   defnames.setText(mc.getText());
							   defnames.setType(mc.getFullType());
			
				mc.getForeignNames().add(defnames);
				
				if (map.get(FOREIGN_DATA) != null) {
					for (Map<String, Object> mapNames : (List<Map<String, Object>>) map.get(FOREIGN_DATA)) {
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
							 me.setFlavor(mc.getFlavor());
							 me.setScryfallId(mc.getScryfallId());

							 if (map.get(NUMBER) != null)
							 {
								 me.setNumber(String.valueOf(map.get(NUMBER)));
							 }
							 
							 if(map.get(MULTIVERSE_ID)!=null)
							 {
								 defnames.setGathererId((int)Double.parseDouble(map.get(MULTIVERSE_ID).toString()));
								 me.setMultiverseid(String.valueOf((int)Double.parseDouble(map.get(MULTIVERSE_ID).toString())));
							 }
							
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
					me.setMultiverseid(String.valueOf((int)Double.parseDouble(map.get(MULTIVERSE_ID).toString())));
				} catch (Exception e) {
					//do nothing
				}
				
				try {
					me.setScryfallId(map.get(SCRYFALL_ID).toString());
				} catch (Exception e) {
					//do nothing
				}
			}
	}

	@Override
	public List<MagicEdition> loadEditions() throws IOException {
		String jsquery = "$.*";
		chrono.start();
		
		List<MagicEdition> eds = new ArrayList<>();
		try {		
		
		URLTools.extractJson(MTG_JSON_SETS_LIST).getAsJsonArray().forEach(e->{
				String codeedition = e.getAsJsonObject().get("code").getAsString().toUpperCase();
				eds.add(generateEdition(codeedition));
		});
		
		}catch(Exception ex)
		{
			
			logger.error("Error loading set List from " + MTG_JSON_SETS_LIST +". Loading manually");
			final List<String> codeEd = new ArrayList<>();
			ctx.withListeners(fr -> {
				if (fr.path().startsWith("$"))
					codeEd.add(fr.path().substring(fr.path().indexOf("$[") + 3, fr.path().indexOf(']') - 1));
				return null;

			}).read(jsquery, List.class);
			codeEd.stream().map(this::generateEdition).forEach(eds::add);
		}

		logger.debug("Loading editions OK in " + chrono.stop() + " sec.");
		
		return eds;
	}

	private MagicEdition generateEdition(String id) {
		
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
			ed.setMkmName(ctx.read(base + ".mcmName", String.class));
		} catch (PathNotFoundException pnfe) {
			// do nothing
		}
		
		try {
			ed.setMkmid(ctx.read(base + ".mcmId", Integer.class));
		} catch (PathNotFoundException pnfe) {
			// do nothing
		}
		
		
		try {
			ed.setCardCountOfficial(ctx.read(base + ".baseSetSize", Integer.class));
		} catch (PathNotFoundException pnfe) {
			// do nothing
		}
		
		try {
			ed.setTcgplayerGroupId(ctx.read(base + ".tcgplayerGroupId", Integer.class));
		} catch (PathNotFoundException pnfe) {
			// do nothing
		}
		
		
		try {
			ed.setKeyRuneCode(ctx.read(base+".keyruneCode",String.class));
		}catch(PathNotFoundException pnfe)
		{
			//do nothing
		}
		
		try {
			JsonObject o = ctx.read(base+".translations",JsonObject.class);
			
			o.keySet().forEach(key->ed.getTranslations().put(key, o.get(key).getAsString()));
		}catch(Exception pnfe)
		{
			//do nothing
		}
		
		
		
		try {
			ed.setCardCount(ctx.read(base + ".totalSetSize", Integer.class));
		} catch (PathNotFoundException pnfe) {
			logger.warn("totalSetSize not found in " + ed.getId() + ", manual calculation");
			if (ed.getCardCount() == 0)
				try {
					ed.setCardCount(ctx.read(base + ".cards.length()"));
				} catch (Exception e) {
					ed.setCardCount(0);
				}
		}
		return ed;
	}


	@Override
	public String getName() {
		return "MTGJson4";
	}


	

	@Override
	public void initDefault() {
		super.initDefault();
		setProperty("LRU_CACHE", "400");
	}
	
}
