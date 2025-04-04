package org.magic.api.providers.impl;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.MTGFormat;
import org.magic.api.beans.MTGFormat.AUTHORIZATION;
import org.magic.api.beans.MTGKeyWord;
import org.magic.api.beans.MTGKeyWord.TYPE;
import org.magic.api.beans.MTGRuling;
import org.magic.api.beans.enums.EnumBorders;
import org.magic.api.beans.enums.EnumColors;
import org.magic.api.beans.enums.EnumExtra;
import org.magic.api.beans.enums.EnumFinishes;
import org.magic.api.beans.enums.EnumFrameEffects;
import org.magic.api.beans.enums.EnumLayout;
import org.magic.api.beans.enums.EnumPromoType;
import org.magic.api.beans.enums.EnumRarity;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.criterias.MTGCrit;
import org.magic.api.criterias.MTGQueryBuilder;
import org.magic.api.criterias.QueryAttribute;
import org.magic.api.criterias.builders.ScryfallCriteriaBuilder;
import org.magic.api.interfaces.abstracts.AbstractCardsProvider;
import org.magic.services.MTGConstants;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.threads.MTGRunnable;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.BeanTools;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.UITools;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class ScryFallProvider extends AbstractCardsProvider {

	private static final String KEYWORDS = "keywords";
	private static final String ORACLE_ID = "oracle_id";
	private static final String DEFENSE = "defense";
	private static final String GAMES = "games";
	private static final String RELEASED_AT = "released_at";
	private static final String IMAGE_URIS = "image_uris";
	private static final String TYPE_LINE = "type_line";
	private static final String LEGALITIES = "legalities";
	private static final String FINISHES = "finishes";
	private static final String CARD_FACES = "card_faces";
	private static final String PROMO_TYPES = "promo_types";
	private static final String COLORS = "colors";
	private static final String FRAME_EFFECTS = "frame_effects";
	private static final String MULTIVERSE_IDS = "multiverse_ids";
	private static final String MANA_COST = "mana_cost";
	private static final String COLOR = "color";
	private static final String SET = "set";
	private static final String CMC = "cmc";
	private static final String COLOR_IDENTITY = "color_identity";
	private static final String LAYOUT = "layout";
	private static final String RARITY = "rarity";
	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String TOUGHNESS = "toughness";
	private static final String LOYALTY = "loyalty";
	private static final String POWER = "power";
	private static final String BASE_URI = "https://api.scryfall.com";
	private static final String BASE_SUBURI = "/cards/";
	private Map<String, String> languages;
	private Map<String, Set<String>> mapOtherSet;
	private Map<String, Set<MTGRuling>> mapRules;
	
	
	public enum BULKTYPE {ORACLE_CARDS,UNIQUE_ARTWORK,DEFAULT_CARDS, ALL_CARDS,RULINGS}
	
	
	@Override
	public void init() {
		
			ThreadManager.getInstance().executeThread(new MTGRunnable() {
				
				@Override
				protected void auditedRun() {
					
					try {
						initMapOtherSet();
					} catch (IOException e) {
						logger.error("error init other sets {}",e.getMessage());
					}
					
				}
			}, "init scryfall Sets");
			
			ThreadManager.getInstance().executeThread(new MTGRunnable() {
				
				@Override
				protected void auditedRun() {
					
					try {
						initMapRules();
					} catch (IOException e) {
						logger.error("error init rules {}",e.getMessage());
					}
					
				}
			}, "init scryfall rules");
			
	}

	private void initMapOtherSet() throws IOException
	{
		
		var f = bulkData(BULKTYPE.DEFAULT_CARDS);
		
		for(var e :  URLTools.toJson(FileTools.readFile(f)).getAsJsonArray())
		{
			if(e.getAsJsonObject().get(ORACLE_ID)!=null)
				mapOtherSet.computeIfAbsent(e.getAsJsonObject().get(ORACLE_ID).getAsString(),v->new HashSet<>()).add(e.getAsJsonObject().get("set").getAsString());
		}
	}
	
	private void initMapRules()throws IOException
	{
	
			var f = bulkData(BULKTYPE.RULINGS);
			URLTools.toJson(FileTools.readFile(f)).getAsJsonArray().forEach(e->{
				
				var r = new MTGRuling();
				r.setDate(readAsDate(e.getAsJsonObject(),"published_at"));
				r.setText(readAsString(e.getAsJsonObject(),"comment"));
				if(e.getAsJsonObject().get(ORACLE_ID)!=null)
					mapRules.computeIfAbsent(e.getAsJsonObject().get(ORACLE_ID).getAsString(),v->new HashSet<>()).add(r);
			});
	}
	
	public ScryFallProvider() {
		 languages = new HashMap<>();
		 mapOtherSet = new HashMap<>();
		 mapRules = new HashMap<>();
		 
		 
			languages.put("es","Spanish");
			languages.put("fr","French"); 
			languages.put("de","German"); 
			languages.put("it","Italian");
			languages.put("pt","Portuguese"); 
			languages.put("ja","Japanese");
			languages.put("ko","Korean");
			languages.put("ru","Russian"); 
			languages.put("zhs","Simplified Chinese"); 
			languages.put("zht","Traditional Chinese"); 
			languages.put("he","Hebrew");
			languages.put("la","Latin"); 
			languages.put("grc","Ancient Greek"); 
			languages.put("ar","Arabic");
			languages.put("sa","Sanskrit"); 
			languages.put("ph" ,"Phyrexian");
	}
	
	private File bulkData(BULKTYPE t) throws IOException
	{
		var f = new File(MTGConstants.DATA_DIR,t.name().toLowerCase()+".json");
		
		if(f.exists() && FileTools.daysBetween(f)<=getInt("DAY_RETENTION"))
			return f;
		
		logger.info("{} will be update",f);
		var arr = URLTools.extractAsJson(BASE_URI + "/bulk-data").getAsJsonObject().get("data").getAsJsonArray();
		
		for(var obj : arr)
		{
			var jo = obj.getAsJsonObject();
			
			if(jo.get("type").getAsString().equalsIgnoreCase(t.name()))
			{
				URLTools.download(jo.get("download_uri").getAsString(), f);
				return f;
			}
		}
		throw new IOException("No bulk data found for "+t);
	}
	
	public JsonObject getJsonFor(MTGCard mc)
	{
			return URLTools.extractAsJson(BASE_URI + BASE_SUBURI + mc.getEdition().getId().toLowerCase() + "/" + mc.getNumber()).getAsJsonObject();
	}
	
	@Override
	public List<MTGCard> listAllCards() throws IOException {
		
		var f = bulkData(BULKTYPE.DEFAULT_CARDS);
		
		var ret = new ArrayList<MTGCard>();
		
		for(var obj : URLTools.toJson(FileTools.readFile(f)).getAsJsonArray())
		{
			try {
				ret.add(generateCard(obj.getAsJsonObject(),true));
			} catch (ExecutionException e) {
				logger.error("error generating",e);
			}
		}
		
		return ret;
		
	}

	@Override
	public MTGCard getCardByArenaId(String id) throws IOException {
		return searchCardByCriteria("arenaId",id, null, true).get(0);
	}

	@Override
	public MTGCard getCardByScryfallId(String crit) throws IOException {
		return getCardById(crit);
	}
	
	@Override
	public MTGCard getCardById(String id) throws IOException {
		try {
			return searchCardByCriteria(ID, id, null, true).get(0);
		}catch(IndexOutOfBoundsException e)
		{
			return null;
		}
	}

	@Override
	public MTGCard getCardByNumber(String num, MTGEdition me) throws IOException {
		try {
			return searchCardByCriteria("number", num, me, true).get(0);
		}catch(IndexOutOfBoundsException e)
		{
			return null;
		}
	}

	@Override
	public List<MTGCard> searchByCriteria(MTGCrit<?>... crits) throws IOException {
		var query=createQuery(getMTGQueryManager().build(crits).toString());
		return execute(query);
	}
	
	
	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var map = new HashMap<String, MTGProperty>();
			 map.put("EXTRA", MTGProperty.newBooleanProperty("false", "If true, extra cards (tokens, planes, etc) will be included."));
			 map.put("MULTILINGUAL", MTGProperty.newBooleanProperty("false", "If true, cards in every language supported by Scryfall will be included."));
			 map.put("VARIATIONS",MTGProperty.newBooleanProperty("true", "If true, rare care variants will be included, like the <a href='https://scryfall.com/card/drk/107%E2%80%A0/runesword'>Hairy Runesword</a>"));
			 map.put("DAY_RETENTION", MTGProperty.newIntegerProperty("1", "number of days of retention for rules and cards bulk's data files", 1, -1));
		return map;
	}

	@Override
	public List<MTGCard> searchCardByCriteria(String att, String crit, MTGEdition me, boolean exact) throws IOException {

		var value = crit;
		
		if(exact && att.equals(NAME))
			value="!\""+value+"\"";
		
		var q = createQuery(att+":"+value + (me!=null?" set:"+me.getId():""));
		
		if(att.equals(ID))
		{
			try {
				
				return List.of(cacheCards.get(crit, new Callable<MTGCard>() {
					@Override
					public MTGCard call() throws Exception {
						return generateCard(URLTools.extractAsJson(BASE_URI+BASE_SUBURI+crit).getAsJsonObject(),true);
					}
				}));
				
			} catch (ExecutionException e) {
				logger.error(e);
			}
		}
		
		return execute(q);
		
	}

	private List<MTGCard> execute(RequestBuilder q) throws IOException {
		var obj = q.toJson().getAsJsonObject();
		
		logger.debug("execute q={}, results ={}",q,obj);
		
		var list = new ArrayList<MTGCard>();
		
		if(obj.get("error")!=null)
			throw new IOException(obj.get("error").getAsString());
		
		if(obj.get("object").getAsString().equals("error"))
		{
			logger.error(obj.get("details").getAsString());
			return list;
		}
				
		var hasMore = true;

		while(hasMore)
		{
			
			var arr = obj.get("data").getAsJsonArray();
			
			for(var e : arr)
			{
				try {
					list.add(generateCard(e.getAsJsonObject(),true));
				} catch (ExecutionException e1) {
					logger.error(e1);
				}
			}
			
			hasMore = obj.get("has_more").getAsBoolean();
			
			if (hasMore)
				obj=URLTools.extractAsJson(obj.get("next_page").getAsString()).getAsJsonObject();
			
			
			ThreadManager.getInstance().sleep(50);
		}
		
		return list;
	}


	private RequestBuilder createQuery(String q) {
		return RequestBuilder.build().setClient(URLTools.newClient()).url(BASE_URI+"/cards/search").get()
				.addContent("unique","prints")						
				.addContent("include_extras",getString("EXTRA"))
				.addContent("include_multilingual",getString("MULTILINGUAL"))
				.addContent("include_variations",getString("VARIATIONS"))
				.addContent("order",SET)
				.addContent("format","json")
				.addContent("pretty","false")
				.addContent("q",q);
	}

	@Override
	public List<MTGEdition> loadEditions() throws IOException {
		String url = BASE_URI + "/sets";
		var root = URLTools.extractAsJson(url).getAsJsonObject();
		var eds = new ArrayList<MTGEdition>();
		root.get("data").getAsJsonArray().forEach(je->	eds.add(generateEdition(je.getAsJsonObject())));
		return eds;
	}

	@Override
	protected List<QueryAttribute> loadQueryableAttributs() {
			List<QueryAttribute> arr = new ArrayList<>();
		
			for(String s :Lists.newArrayList(NAME, ID, "type", "oracle", "mana", "cube", "artist", "flavor", "watermark", "border", "frame"))
			{
				arr.add(new QueryAttribute(s,String.class));
			}
		
			for(String s :Lists.newArrayList(CMC, POWER, TOUGHNESS,LOYALTY))
			{
				arr.add(new QueryAttribute(s,Integer.class));
			}
		
			for(String s :Lists.newArrayList("foil","nonfoil","oversized","promo","reprint","story_spotlight","variation","permanent","spell","vanilla","funny","scheme"))
			{
				arr.add(new QueryAttribute(s,Boolean.class));
			}
			
			arr.add(new QueryAttribute(COLOR, EnumColors.class));
			arr.add(new QueryAttribute(COLOR_IDENTITY, EnumColors.class));
			arr.add(new QueryAttribute(LAYOUT,EnumLayout.class));
			arr.add(new QueryAttribute(RARITY,EnumRarity.class));
			arr.add(new QueryAttribute(FINISHES,EnumFinishes.class));
			arr.add(new QueryAttribute(PROMO_TYPES,EnumPromoType.class));
			return arr;
}

	@Override
	public MTGQueryBuilder<?> getMTGQueryManager() {
		var b= new ScryfallCriteriaBuilder();
		initBuilder(b);
		return b;
	}
	
	@Override
	public List<String> loadCardsLangs() throws IOException {
		return languages.values().stream().toList();
	}

	@Override
	public String getName() {
		return "Scryfall";
	}

	@Override
	public String getVersion() {
		return "2.0";
	}
	
	private String readAsString(JsonObject obj , String k)
	{
		try {
			return obj.get(k).getAsString();
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	private Date readAsDate(JsonObject obj , String k)
	{
		try {
			return UITools.parseDate(obj.get(k).getAsString(), "yyyy-MM-dd");
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	
	
	private Integer readAsInt(JsonObject obj , String k)
	{
		try {
			return obj.get(k).getAsInt();
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	private Boolean readAsBoolean(JsonObject obj , String k)
	{
		try {
			return obj.get(k).getAsBoolean();
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	private MTGCard generateCard(JsonObject obj, boolean loadMeld) throws ExecutionException {
		
				var mc = new MTGCard();
				mc.setId(obj.get(ID).getAsString());
				mc.setScryfallId(mc.getId());
				mc.setName(obj.get(NAME).getAsString());
				mc.setArtist(obj.get("artist").getAsString());
				mc.setLayout(EnumLayout.parseByLabel(obj.get(LAYOUT).getAsString()));
				mc.setEdition(getSetById(obj.get(SET).getAsString().toUpperCase()));
				mc.setCmc(readAsInt(obj, CMC));
				mc.setRarity(EnumRarity.rarityByName(obj.get(RARITY).getAsString()));
				mc.setReserved(obj.get("reserved").getAsBoolean());
				mc.setOversized(obj.get("oversized").getAsBoolean());
				mc.setBorder(EnumBorders.parseByLabel(obj.get("border_color").getAsString()));
				mc.setFullArt(obj.get("full_art").getAsBoolean());
				mc.setPromoCard(obj.get("promo").getAsBoolean());
				mc.setFrameVersion(obj.get("frame").getAsString());
				mc.setReprintedCard(obj.get("reprint").getAsBoolean());
				mc.setFlavor(readAsString(obj,"flavor_text"));
				mc.setWatermarks(readAsString(obj,"watermark"));
				mc.setText(readAsString(obj,"oracle_text"));
				mc.setCost(readAsString(obj,MANA_COST));
				mc.setDefense(readAsInt(obj, DEFENSE));
				mc.setMkmId(readAsInt(obj,"cardmarket_id"));
				mc.setTcgPlayerId(readAsInt(obj,"tcgplayer_id"));
				mc.setPower(readAsString(obj,POWER));
				mc.setToughness(readAsString(obj,TOUGHNESS));
				mc.setLoyalty(readAsInt(obj, LOYALTY));
				mc.setEdhrecRank(readAsInt(obj,"edhrec_rank"));
				mc.setNumber(readAsString(obj,"collector_number"));
				mc.setStorySpotlight(obj.get("story_spotlight").getAsBoolean());
				mc.setScryfallIllustrationId(readAsString(obj,"illustration_id"));
				mc.setHasContentWarning(readAsBoolean(obj,"content_warning"));		
				mc.setFlavorName(readAsString(obj,"flavor_name"));
				mc.setOriginalReleaseDate(readAsString(obj,RELEASED_AT));
				//mc.setRulings(generatesRulings(readAsString(obj,"oracle_id")))
				
				generateTypes(mc, obj.get(TYPE_LINE));
				
				
				if(mapOtherSet.get(readAsString(obj,ORACLE_ID))!=null)
					mapOtherSet.get(readAsString(obj,ORACLE_ID)).forEach(s->mc.getEditions().add(getSetById(s.toUpperCase())));
				
				if(mapRules.get(readAsString(obj,ORACLE_ID))!=null)
					mapRules.get(readAsString(obj,ORACLE_ID)).forEach(r->mc.getRulings().add(r));
				
				
				
			if (obj.get(GAMES) != null) {
				mc.setArenaCard(obj.get(GAMES).getAsJsonArray().contains(new JsonPrimitive("arena")));
				mc.setMtgoCard(obj.get(GAMES).getAsJsonArray().contains(new JsonPrimitive("mtgo")));
			}
			
			
			if(obj.get(FRAME_EFFECTS)!=null)
				obj.get(FRAME_EFFECTS).getAsJsonArray().forEach(je->mc.getFrameEffects().add(EnumFrameEffects.parseByLabel(je.getAsString())));

			if(obj.get(COLORS)!=null)
				obj.get(COLORS).getAsJsonArray().forEach(je->mc.getColors().add(EnumColors.colorByCode(je.getAsString())));
			
			if(obj.get(PROMO_TYPES)!=null)
				obj.get(PROMO_TYPES).getAsJsonArray().forEach(je->mc.getPromotypes().add(EnumPromoType.parseByLabel(je.getAsString())));
			
			
			obj.get(KEYWORDS).getAsJsonArray().forEach(je->mc.getKeywords().add(new MTGKeyWord(je.getAsString(), TYPE.ABILITIES)));
			
			obj.get(COLOR_IDENTITY).getAsJsonArray().forEach(je->mc.getColorIdentity().add(EnumColors.colorByCode(je.getAsString())));
		
			obj.get(FINISHES).getAsJsonArray().forEach(je->mc.getFinishes().add(EnumFinishes.parseByLabel(je.getAsString())));
				
			if (obj.get(LEGALITIES) != null) {
				var legs = obj.get(LEGALITIES).getAsJsonObject();
				for (var ent : legs.entrySet()) {
					mc.getLegalities().add(new MTGFormat(ent.getKey(),AUTHORIZATION.valueOf(ent.getValue().getAsString().toUpperCase())));
				}
			}


			if(obj.get(MULTIVERSE_IDS)!=null && !obj.get(MULTIVERSE_IDS).getAsJsonArray().isEmpty())
				mc.setMultiverseid(obj.get(MULTIVERSE_IDS).getAsJsonArray().get(0).getAsString());

			
			if(obj.get(CARD_FACES)!=null)
			{
				initSubCard(mc,obj.get(CARD_FACES).getAsJsonArray());
				
				if(obj.get(MULTIVERSE_IDS)!=null && obj.get(MULTIVERSE_IDS).getAsJsonArray().size()>1)
					mc.getRotatedCard().setMultiverseid(obj.get(MULTIVERSE_IDS).getAsJsonArray().get(1).getAsString());
				
			}
			else
			{
				mc.setUrl(obj.get(IMAGE_URIS).getAsJsonObject().get("large").getAsString());
			}
			
			
			if(obj.get("all_parts")!=null && loadMeld)
			{
				for(var e : obj.get("all_parts").getAsJsonArray())
				{
					if(e.getAsJsonObject().get("component").getAsString().equals("meld_result"))
					{
						try {
							
							var retJson = URLTools.extractAsJson(BASE_URI+BASE_SUBURI+e.getAsJsonObject().get(ID).getAsString()).getAsJsonObject();
							mc.setRotatedCard(generateCard(retJson,false));
							mc.getRotatedCard().setSide("b");
							
						} catch (Exception e1) {
							logger.error(e1);
						}
					}
				}
			}
			
			
			
			
			
			
			postTreatmentCard(mc);
			notify(mc);
			
			
		return mc;

	}
	
	private void overrideCardFaceData(MTGCard mc, JsonObject obj,String side)
	{
		mc.setName(obj.get(NAME).getAsString());
		generateTypes(mc, obj.get(TYPE_LINE));
		mc.setText(readAsString(obj,"oracle_text"));
		mc.setCost(readAsString(obj,MANA_COST));
		mc.setPower(readAsString(obj,POWER));
		mc.setToughness(readAsString(obj,TOUGHNESS));
		mc.setLoyalty(readAsInt(obj, LOYALTY));
		mc.setScryfallIllustrationId(readAsString(obj,"illustration_id"));
		mc.setSide(side);
		mc.setFlavor(readAsString(obj,"flavor_text"));
		
		
		if(obj.get(DEFENSE)!=null)
			mc.setDefense(obj.get(DEFENSE).getAsInt());
			
			
		if(obj.get(COLORS)!=null)
		{
			mc.getColors().clear();
			obj.get(COLORS).getAsJsonArray().forEach(je->mc.getColors().add(EnumColors.colorByCode(je.getAsString())));
		}
		
		
		if(obj.get(IMAGE_URIS)!=null)
			mc.setUrl(obj.get(IMAGE_URIS).getAsJsonObject().get("large").getAsString());
		
		mc.setId(mc.getId()+"_"+side);
		
	}

	private void initSubCard(MTGCard mc, JsonArray arr) {
		
		try {
			var mc2 = BeanTools.cloneBean(mc);
			
			overrideCardFaceData(mc, arr.get(0).getAsJsonObject(),"a");
			overrideCardFaceData(mc2, arr.get(1).getAsJsonObject(),"b");
			mc.setRotatedCard(mc2);
		} catch (Exception e) {
			logger.error("Error getting subcard for {}",mc,e);
		}
	}

	private MTGEdition generateEdition(JsonObject obj) {
		var ed = new MTGEdition();
		ed.setId(obj.get("code").getAsString().toUpperCase());
		ed.setSet(readAsString(obj,NAME));
		ed.setType(readAsString(obj,"set_type"));
		ed.setBlock(readAsString(obj,"block"));
		ed.setCardCount(obj.get("card_count").getAsInt());
		ed.setCardCountOfficial(ed.getCardCount());
		ed.setCardCountPhysical(ed.getCardCount());
		ed.setKeyRuneCode(ed.getId());
		
		if(obj.get("parent_set_code")!=null)
			ed.setParentCode(obj.get("parent_set_code").getAsString().toUpperCase());

		if (obj.get("digital") != null)
			ed.setOnlineOnly(obj.get("digital").getAsBoolean());

		if(obj.get("foil_only") !=null)
			ed.setFoilOnly(obj.get("foil_only").getAsBoolean());

		if (obj.get(RELEASED_AT) != null)
		{
			ed.setReleaseDate(obj.get(RELEASED_AT).getAsString());
			ed.setPreview(LocalDate.parse(obj.get(RELEASED_AT).getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).isAfter(LocalDate.now()));
		}

		ed.getBooster().add(EnumExtra.DRAFT);
		
		
		notify(ed);

		return ed;
	}
	
	private void generateTypes(MTGCard mc, JsonElement obj) {
		
		if(obj ==null)
			return ;
		
		
		var line = obj.getAsString();
		
		mc.setTypes(new ArrayList<>());
		mc.setSupertypes(new ArrayList<>());
		mc.setSubtypes(new ArrayList<>());
		
		
		line = line.replace("\"", "");

		for (String k : new String[] { "Legendary", "Basic", "Host", "Ongoing", "Snow", "World" }) {
			if (line.contains(k)) {
				mc.getSupertypes().add(k);
				line = line.replaceAll(k, "").trim();
			}
		}

		var sep = "\u2014";

		if (line.contains(sep)) {

			for (String s : line.substring(0, line.indexOf(sep)).trim().split(" "))
				mc.getTypes().add(s.replace("\"", ""));

			for (String s : line.substring(line.indexOf(sep) + 1).trim().split(" "))
				mc.getSubtypes().add(s);
		} else {
			for (String s : line.split(" "))
				mc.getTypes().add(s.replace("\"", ""));
		}

	}

}
