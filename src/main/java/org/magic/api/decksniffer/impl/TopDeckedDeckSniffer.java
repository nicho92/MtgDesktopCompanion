package org.magic.api.decksniffer.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.beans.technical.RetrievableDeck;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.network.URLTools;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

import com.google.gson.JsonObject;

public class TopDeckedDeckSniffer extends AbstractDeckSniffer {

	private static final String MAX_PAGE = "MAX_PAGE";

	private Map<String, JsonObject> cacheDeck;
	
	
	@Override
	public String[] listFilter() {
		return new String[] {  "commander",
	            "legacy",
	            "modern",
	            "pauper",
	            "penny dreadful",
	            "pioneer",
	            "standard",
	            "vintage"};
	}

	public TopDeckedDeckSniffer() {
		cacheDeck = new HashMap<>();
	}
	
	@Override
	public MTGDeck getDeck(RetrievableDeck info) throws IOException {
		
		
		var obj = cacheDeck.get(info.getAuthor()).getAsJsonObject();
		var deck = info.toBaseDeck();
		
		
		for(var cardObj : obj.get("main").getAsJsonArray())
		{
			var mc = MTG.getEnabledPlugin(MTGCardsProvider.class).getCardByScryfallId(cardObj.getAsJsonObject().get("id").getAsString());
			deck.getMain().put(mc, cardObj.getAsJsonObject().get("quantity").getAsInt());
			
			notify(mc);
		}
		
		for(var cardObj : obj.get("sb").getAsJsonArray())
		{
			var mc = MTG.getEnabledPlugin(MTGCardsProvider.class).getCardByScryfallId(cardObj.getAsJsonObject().get("id").getAsString());
			deck.getSideBoard().put(mc, cardObj.getAsJsonObject().get("quantity").getAsInt());
			
			notify(mc);
			
		}
		
		deck.setDescription(obj.get("description").getAsString());
		
		cacheDeck.clear();
		return deck;
	}

	@Override
	public List<RetrievableDeck> getDeckList(String filter, MTGCard mc) throws IOException {
	
		var ret = new ArrayList<RetrievableDeck>();
		
		
		for(var numPage=1; numPage<=getInt(MAX_PAGE);numPage++)
		{	
			var url = "https://api-prod.topdecked.com/metagame/archetypes/"+filter+"?populate=true&deckLimit=0&page="+numPage;
			var results = URLTools.extractAsJson(url).getAsJsonObject().get("docs").getAsJsonArray();

				
				for(var element : results){
					var rd =  new RetrievableDeck();
						var obj = element.getAsJsonObject();
						 
						rd.setName(obj.get("name").getAsString());
						rd.setAuthor(obj.get("id").getAsString());
						rd.setDescription(UITools.formatDate(UITools.parseGMTDate(obj.get("updated").getAsString())));
						 
						 
						 var builder = new StringBuilder();
						 for(var c : obj.get("colors").getAsJsonArray())
							 builder.append("{").append(c.getAsString()).append("}");
						 
						 rd.setColor(builder.toString());
						 
						 
						 ret.add(rd);
						 
					
						 cacheDeck.put(rd.getAuthor(), obj.get("example").getAsJsonObject());
						 
				}			 
		}		
		return ret;
	}
	
	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var m = super.getDefaultAttributes();
		m.put(MAX_PAGE, MTGProperty.newIntegerProperty("2", "number of page to query", 1, 10));
		
		return m;
	}
	
	
	@Override
	public String getName() {
		 return "Top Decked";
	}

}
