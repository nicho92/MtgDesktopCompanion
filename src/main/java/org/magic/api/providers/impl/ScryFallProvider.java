package org.magic.api.providers.impl;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.MTGFormat;
import org.magic.api.beans.MTGFormat.AUTHORIZATION;
import org.magic.api.beans.MTGKeyWord;
import org.magic.api.beans.MTGKeyWord.TYPE;
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
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.BeanTools;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class ScryFallProvider extends AbstractCardsProvider {

	private static final String MULTIVERSE_IDS = "multiverse_ids";
	private static final String BASE_SUBURI = "/cards/";
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


	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("LOAD_RULING",MTGProperty.newBooleanProperty(FALSE, "Set to true if you want to load rulings data. take longer time to load"));
	}
	
	public void bulkData() throws IOException
	{
		var k = "Default Cards";
		
		var arr = URLTools.extractAsJson(BASE_URI + "/bulk-data").getAsJsonObject().get("data").getAsJsonArray();
		
		for(var obj : arr)
		{
			var jo = obj.getAsJsonObject();
			
			if(jo.get("name").getAsString().equals(k))
			{
				URLTools.download(jo.get("download_uri").getAsString(), new File(MTGConstants.DATA_DIR,"scryfall.json"));
				return;
			}
		}
		throw new IOException("No bulk data found for "+k);
		
		
	}
	
	
	public String getLanguage(String code) 
	{
		switch (code) 
		{
			case "es": return "Spanish";
			case "fr": return "French"; 
			case "de": return "German"; 
			case "it": return "Italian";
			case "pt": return "Portuguese"; 
			case "ja": return "Japanese";
			case "ko": return "Korean";
			case "ru": return "Russian"; 
			case "zhs": return "Simplified Chinese"; 
			case "zht": return "Traditional Chinese"; 
			case "he": return "Hebrew";
			case "la": return "Latin"; 
			case "grc": return "Ancient Greek"; 
			case "ar": return "Arabic";
			case "sa": return "Sanskrit"; 
			case "ph" : return "Phyrexian";
			default : return "English";
		}	
		
	}

	public JsonObject getJsonFor(MTGCard mc)
	{
			String url = BASE_URI + BASE_SUBURI + mc.getEdition().getId().toLowerCase() + "/" + mc.getNumber();
			return URLTools.extractAsJson(url).getAsJsonObject();
	}

	@Override
	public List<MTGCard> listAllCards() throws IOException {
		throw new IOException("Not Implemented");
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
		throw new IOException("Not Yet Implemented");
	}

	@Override
	public List<MTGCard> searchCardByCriteria(String att, String crit, MTGEdition me, boolean exact) throws IOException {
		List<MTGCard> list = new ArrayList<>();

		var value = crit;
		
		if(exact && att.equals(NAME))
			value="!\""+value+"\"";
		
		
		var q= RequestBuilder.build().setClient(URLTools.newClient()).url(BASE_URI+"/cards/search").get()
				.addContent("unique","prints")						
				.addContent("include_extras","true")
				.addContent("include_multilingual","false")
				.addContent("include_variations","true")
				.addContent("order",SET)
				.addContent("format","json")
				.addContent("pretty","false")
				.addContent("q",att+":"+value + (me!=null?" set:"+me.getId():""));
		
		
		
		
		
		
		if(att.equals(ID))
		{
			q.clearContents();
			q.url(BASE_URI+BASE_SUBURI+crit);
			try {
				list.add(generateCard(q.toJson().getAsJsonObject(),true));
			} catch (ExecutionException e) {
				logger.error(e);
			}
			
			return list;
		}
		
		
		var obj = q.toJson().getAsJsonObject();
		
		
		if(obj.get("error")!=null)
			throw new IOException(obj.get("error").getAsString());
		
			
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
		
			arr.add(new QueryAttribute(SET,MTGEdition.class));
			arr.add(new QueryAttribute(COLOR, EnumColors.class));
			arr.add(new QueryAttribute(COLOR_IDENTITY, EnumColors.class));
			arr.add(new QueryAttribute(LAYOUT,EnumLayout.class));
			arr.add(new QueryAttribute(RARITY,EnumRarity.class));
			arr.add(new QueryAttribute("finishes",EnumFinishes.class));
			arr.add(new QueryAttribute("promo_types",EnumPromoType.class));
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
		return Lists.newArrayList("en","es","fr","de","it","pt","ja","ru","zhs","he","ar");
	}

	@Override
	public String getName() {
		return "Scryfall";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
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
		
		var mc= cacheCards.get(obj.get(ID).getAsString(), new Callable<MTGCard>(){
			@Override
			public MTGCard call() throws Exception {
				var mc = new MTGCard();
				mc.setId(obj.get(ID).getAsString());
				mc.setScryfallId(mc.getId());
				mc.setName(obj.get(NAME).getAsString());
				mc.setArtist(obj.get("artist").getAsString());
				mc.setLayout(EnumLayout.parseByLabel(obj.get(LAYOUT).getAsString()));
				mc.setEdition(getSetById(obj.get(SET).getAsString().toUpperCase()));
				mc.getEditions().add(getSetById(obj.get(SET).getAsString().toUpperCase()));
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
				mc.setDefense(readAsInt(obj, "defense"));
				mc.setMkmId(readAsInt(obj,"cardmarket_id"));
				mc.setTcgPlayerId(readAsInt(obj,"tcgplayer_id"));
				mc.setPower(readAsString(obj,POWER));
				mc.setToughness(readAsString(obj,TOUGHNESS));
				mc.setLoyalty(readAsInt(obj, LOYALTY));
				mc.setEdhrecRank(readAsInt(obj,"edhrec_rank"));;
				mc.setNumber(readAsString(obj,"collector_number"));
				mc.setStorySpotlight(obj.get("story_spotlight").getAsBoolean());
				mc.setScryfallIllustrationId(readAsString(obj,"illustration_id"));
				mc.setHasContentWarning(readAsBoolean(obj,"content_warning"));		
				mc.setFlavorName(readAsString(obj,"flavor_name"));
				mc.setOriginalReleaseDate(readAsString(obj,"released_at"));
			
				generateTypes(mc, obj.get("type_line").getAsString());
						
				
			if (obj.get("games") != null) {
				mc.setArenaCard(obj.get("games").getAsJsonArray().contains(new JsonPrimitive("arena")));
				mc.setMtgoCard(obj.get("games").getAsJsonArray().contains(new JsonPrimitive("mtgo")));
			}
				
			
			
			if(obj.get("frame_effects")!=null)
				obj.get("frame_effects").getAsJsonArray().forEach(je->mc.getFrameEffects().add(EnumFrameEffects.parseByLabel(je.getAsString())));

			if(obj.get("colors")!=null)
				obj.get("colors").getAsJsonArray().forEach(je->mc.getColors().add(EnumColors.colorByCode(je.getAsString())));
			
			if(obj.get("promo_types")!=null)
				obj.get("promo_types").getAsJsonArray().forEach(je->mc.getPromotypes().add(EnumPromoType.parseByLabel(je.getAsString())));
			
			
			obj.get("keywords").getAsJsonArray().forEach(je->mc.getKeywords().add(new MTGKeyWord(je.getAsString(), TYPE.ABILITIES)));
			
			obj.get(COLOR_IDENTITY).getAsJsonArray().forEach(je->mc.getColorIdentity().add(EnumColors.colorByCode(je.getAsString())));
		
			obj.get("finishes").getAsJsonArray().forEach(je->mc.getFinishes().add(EnumFinishes.parseByLabel(je.getAsString())));
				
			if (obj.get("legalities") != null) {
				var legs = obj.get("legalities").getAsJsonObject();
				for (var ent : legs.entrySet()) {
					mc.getLegalities().add(new MTGFormat(ent.getKey(),AUTHORIZATION.valueOf(ent.getValue().getAsString().toUpperCase())));
				}
			}


			if(obj.get(MULTIVERSE_IDS)!=null && !obj.get(MULTIVERSE_IDS).getAsJsonArray().isEmpty())
				mc.setMultiverseid(obj.get(MULTIVERSE_IDS).getAsJsonArray().get(0).getAsString());

			
			if(obj.get("card_faces")!=null)
			{
				initSubCard(mc,obj.get("card_faces").getAsJsonArray());
				
				if(obj.get(MULTIVERSE_IDS)!=null && obj.get(MULTIVERSE_IDS).getAsJsonArray().size()>1)
					mc.getRotatedCard().setMultiverseid(obj.get(MULTIVERSE_IDS).getAsJsonArray().get(1).getAsString());
				
			}
			else
			{
				mc.setUrl(obj.get("image_uris").getAsJsonObject().get("large").getAsString());
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
			
			
			return mc;
			}
		});
		notify(mc);
		return mc;

	}
	
	private void overrideCardFaceData(MTGCard mc, JsonObject obj,String side)
	{
		mc.setName(obj.get(NAME).getAsString());
		generateTypes(mc, obj.get("type_line").getAsString());
		mc.setText(readAsString(obj,"oracle_text"));
		mc.setCost(readAsString(obj,MANA_COST));
		mc.setPower(readAsString(obj,POWER));
		mc.setToughness(readAsString(obj,TOUGHNESS));
		mc.setLoyalty(readAsInt(obj, LOYALTY));
		mc.setScryfallIllustrationId(readAsString(obj,"illustration_id"));
		mc.setSide(side);
		mc.setFlavor(readAsString(obj,"flavor_text"));
		
		
		if(obj.get("colors")!=null)
		{
			mc.getColors().clear();
			obj.get("colors").getAsJsonArray().forEach(je->mc.getColors().add(EnumColors.colorByCode(je.getAsString())));
		}
		
		
		if(obj.get("image_uris")!=null)
			mc.setUrl(obj.get("image_uris").getAsJsonObject().get("large").getAsString());
		
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

		if (obj.get("released_at") != null)
		{
			ed.setReleaseDate(obj.get("released_at").getAsString());
			ed.setPreview(LocalDate.parse(obj.get("released_at").getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).isAfter(LocalDate.now()));
		}

		ed.getBooster().add(EnumExtra.DRAFT);
		
		
		notify(ed);

		return ed;
	}
	
	private void generateTypes(MTGCard mc, String line) {
		
		mc.setTypes(new ArrayList<String>());
		mc.setSupertypes(new ArrayList<String>());
		mc.setSubtypes(new ArrayList<String>());
		
		
		line = line.replace("\"", "");

		for (String k : new String[] { "Legendary", "Basic", "Ongoing", "Snow", "World" }) {
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
