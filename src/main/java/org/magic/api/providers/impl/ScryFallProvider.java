package org.magic.api.providers.impl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.Level;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumColors;
import org.magic.api.beans.enums.EnumExtra;
import org.magic.api.beans.enums.EnumFinishes;
import org.magic.api.beans.enums.EnumLayout;
import org.magic.api.beans.enums.EnumPromoType;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.criterias.MTGCrit;
import org.magic.api.criterias.MTGQueryBuilder;
import org.magic.api.criterias.QueryAttribute;
import org.magic.api.criterias.builders.ScryfallCriteriaBuilder;
import org.magic.api.interfaces.abstracts.AbstractCardsProvider;
import org.magic.services.logging.MTGLogger;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

public class ScryFallProvider extends AbstractCardsProvider {

	private static final String BASE_URI = "https://api.scryfall.com";


	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("LOAD_RULING",MTGProperty.newBooleanProperty(FALSE, "Set to true if you want to load rulings data. take longer time to load"));
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
			String url = BASE_URI + "/cards/" + mc.getEdition().getId().toLowerCase() + "/" + mc.getNumber();
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
			return searchCardByCriteria("id", id, null, true).get(0);
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

	
	public static void main(String[] args) throws IOException {
		MTGLogger.changeLevel(Level.DEBUG);
		new ScryFallProvider().searchCardByEdition(new MTGEdition("EMN"));
	}
	
	
	@Override
	public List<MTGCard> searchCardByCriteria(String att, String crit, MTGEdition me, boolean exact) throws IOException {
		List<MTGCard> list = new ArrayList<>();

		var value = crit;
		
		if(exact)
			value="\""+value+"\"";
		
		var obj= RequestBuilder.build().setClient(URLTools.newClient()).url(BASE_URI+"/cards/search").get()
				.addContent("unique","prints")						
				.addContent("include_extras","true")
				.addContent("include_multilingual","false")
				.addContent("include_variations","true")
				.addContent("order","name")
				.addContent("format","json")
				.addContent("q",att+":"+value + (me!=null?" set:"+me.getId():""))
				.toJson().getAsJsonObject();
		
		
		if(obj.get("error")!=null)
			throw new IOException(obj.get("error").getAsString());
		
			
		var hasMore = true;
		
		while(hasMore)
		{
			
			var arr = obj.get("data").getAsJsonArray();
			
			for(var e : arr)
			{
				list.add(loadCard(e.getAsJsonObject()));
			}
			
			hasMore = obj.get("has_more").getAsBoolean();
			
			if (hasMore)
				obj=URLTools.extractAsJson(obj.get("next_page").getAsString()).getAsJsonObject();
			
		}
		
		return list;
	}

	

	@Override
	public List<MTGEdition> loadEditions() throws IOException {
		String url = BASE_URI + "/sets";
		var root = URLTools.extractAsJson(url).getAsJsonObject();
		var eds = new ArrayList<MTGEdition>();
		for (var i = 0; i < root.get("data").getAsJsonArray().size(); i++) {
			var e = root.get("data").getAsJsonArray().get(i).getAsJsonObject();
			eds.add(generateEdition(e.getAsJsonObject()));
		}

		return eds;
	}
	

	@Override
	protected List<QueryAttribute> loadQueryableAttributs() {
			List<QueryAttribute> arr = new ArrayList<>();
		
			for(String s :Lists.newArrayList("name", "custom", "type", "oracle", "mana","rarity", "cube", "artist", "flavor", "watermark", "border", "frame"))
			{
				arr.add(new QueryAttribute(s,String.class));
			}
		
			for(String s :Lists.newArrayList("cmc", "power", "toughness","loyalty"))
			{
				arr.add(new QueryAttribute(s,Integer.class));
			}
		
			for(String s :Lists.newArrayList("is","foil","nonfoil","oversized","promo","reprint","story_spotlight","variation"))
			{
				arr.add(new QueryAttribute(s,Boolean.class));
			}
		
			arr.add(new QueryAttribute("set",MTGEdition.class));
			arr.add(new QueryAttribute("color", EnumColors.class));
			arr.add(new QueryAttribute("color_identity", EnumColors.class));
			arr.add(new QueryAttribute("layout",EnumLayout.class));
			arr.add(new QueryAttribute("finishes",EnumFinishes.class));
			arr.add(new QueryAttribute("promo_types",EnumPromoType.class));
			return arr;
}

	@Override
	public MTGQueryBuilder<?> getMTGQueryManager() {
		MTGQueryBuilder<String> b= new ScryfallCriteriaBuilder();
	
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
	
	private MTGCard loadCard(JsonObject obj){

		try {
			return cacheCards.get(obj.get("id").getAsString(), new Callable<MTGCard>() {

				@Override
				public MTGCard call() throws Exception {
					return generateCard(obj);
				}
			});
		} catch (ExecutionException e) {
			logger.error("error loading cards", e);
			return null;
		}


	}

	private MTGCard generateCard(JsonObject obj) {
		var mc = new MTGCard();
		
		mc.setId(obj.get("id").getAsString());
		mc.setScryfallId(mc.getId());
		mc.setName(obj.get("name").getAsString());
		mc.setArtist(obj.get("artist").getAsString());
		mc.setLayout(EnumLayout.parseByLabel(obj.get("layout").getAsString()));
		mc.setEdition(getSetById(obj.get("set").getAsString()));
		mc.setCmc(obj.get("cmc").getAsInt());
		
		
		//System.out.println(mc + " "+ mc.getEdition() + " "+ mc.getLayout());
		
		
		postTreatmentCard(mc);
		
		notify(mc);
		return mc;
	}

	private MTGEdition generateEdition(JsonObject obj) {
		var ed = new MTGEdition();
		ed.setId(obj.get("code").getAsString());
		ed.setSet(obj.get("name").getAsString());
		ed.setType(obj.get("set_type").getAsString());

		
		if(obj.get("parent_set_code")!=null)
			ed.setParentCode(obj.get("parent_set_code").getAsString());

		if (obj.get("digital") != null)
			ed.setOnlineOnly(obj.get("digital").getAsBoolean());

		if(obj.get("foil_only") !=null)
			ed.setFoilOnly(obj.get("foil_only").getAsBoolean());

		ed.setCardCount(obj.get("card_count").getAsInt());
		ed.setCardCountOfficial(ed.getCardCount());
		ed.setCardCountPhysical(ed.getCardCount());
		ed.setKeyRuneCode(ed.getId());
		
		
		if (obj.get("block") != null)
			ed.setBlock(obj.get("block").getAsString());

		if (obj.get("released_at") != null)
		{
			ed.setReleaseDate(obj.get("released_at").getAsString());
			ed.setPreview(LocalDate.parse(obj.get("released_at").getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).isAfter(LocalDate.now()));
		}

		ed.getBooster().add(EnumExtra.DRAFT);
		
		
		notify(ed);

		return ed;
	}


}
