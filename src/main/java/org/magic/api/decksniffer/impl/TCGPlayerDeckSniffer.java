package org.magic.api.decksniffer.impl;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.technical.RetrievableDeck;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.network.RequestBuilder;
import org.magic.services.tools.MTG;

import com.google.gson.JsonObject;

public class TCGPlayerDeckSniffer extends AbstractDeckSniffer {
	
	@Override
	public String[] listFilter() {
		return new String[]{"standard", "modern", "legacy", "vintage", "commander", "pioneer", "pauper", "historic", "brawl"};
	}

	@Override
	public MTGDeck getDeck(RetrievableDeck info) throws IOException {
		logger.debug("get deck at {}", info.getUrl());
		MTGDeck deck = info.toBaseDeck();
		
		var obj = RequestBuilder.build().newClient().get().url(info.getUrl())
			    .addContent("source", "infinite-content")
			    .addContent("subDecks", "true")
			    .addContent("cards", "true")
			    .addContent("stats", "false")
			    .toJson().getAsJsonObject().get("result").getAsJsonObject();
		
		deck.setDescription("Imported from https://www.tcgplayer.com/content"+obj.get("canonicalURL").getAsString());
		
		fillData(obj, "maindeck", deck.getMain());
		fillData(obj, "sideboard", deck.getSideBoard());
		
		try {
		var cmd = obj.get("deck").getAsJsonObject().get("subDecks").getAsJsonObject().get("commandzone");
		if(cmd!=null)
		{
		    var id = cmd.getAsJsonArray().get(0).getAsJsonObject().get("cardID").getAsString();
		    deck.setCommander(parseCard(obj.get("cards").getAsJsonObject().get(id).getAsJsonObject()));
		    deck.getMain().put(deck.getCommander(), 1);
		}
		}
		catch(Exception _)
		{
		    logger.error("errr getting commander");
		}
		
		
		return deck;

	}
	
	private void fillData(JsonObject obj, String side, Map<MTGCard,Integer> data)
	{
	    if( obj.get("deck").getAsJsonObject().get("subDecks").getAsJsonObject().get(side)==null)
		return;
	    
	    obj.get("deck").getAsJsonObject().get("subDecks").getAsJsonObject().get(side).getAsJsonArray().forEach(je->{
		    
		   var id= je.getAsJsonObject().get("cardID").getAsString();
		   var cardData = obj.get("cards").getAsJsonObject().get(id).getAsJsonObject();
		   var qty = je.getAsJsonObject().get("quantity").getAsInt();
		   
		   var c = parseCard(cardData);
		   if(c!=null)
		       data.put(c, qty);
		   
		});
	}
	

	private MTGCard parseCard(JsonObject obj)  {

	    var name = obj.get("name").getAsString();
	    var set = obj.get("set").getAsString();
	    try {
		var edition = MTG.getEnabledPlugin(MTGCardsProvider.class).getSetById(set);
		var mc = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(obj.get("name").getAsString(), edition, true).getFirst();
		notify(mc);
		return mc;
		
	    } catch (Exception e) {
		logger.error("can't find card for {}/{}",name,set);
	    }
	    
	    return null;
	}

	@Override
	public List<RetrievableDeck> getDeckList(String filter, MTGCard mc) throws IOException {

	    var list = new ArrayList<RetrievableDeck>();

	    var obj = RequestBuilder.build().newClient().get().url("https://infinite-api.tcgplayer.com/content/decks/magic")
	    .addContent("source", "infinite-content")
	    .addContent("rows", "128")
	    .addContent("format", filter)
	    .addContent("isAdmin", "false")
	    .addContent("td", "false")
	    .addContent("sort", "latest")
	    .addContent("offset", "0")
	    .addContent("order", "desc").toJson().getAsJsonObject();
	    
	    
	    for(var d : obj.get("result").getAsJsonArray())
	    {
		var deck = new RetrievableDeck();
	
			deck.setAuthor(d.getAsJsonObject().get("deckData").getAsJsonObject().get("playerName").getAsString());
			deck.setDescription(d.getAsJsonObject().get("date").getAsString());
			deck.setName(d.getAsJsonObject().get("deckData").getAsJsonObject().get("deckName").getAsString());
			deck.setUrl(URI.create("https://infinite-api.tcgplayer.com/deck/magic/"+d.getAsJsonObject().get("deckID").getAsString()));
			
			if(d.getAsJsonObject().get("deckData").getAsJsonObject().get("colors")!=null)
			    deck.setColor(Arrays.stream(d.getAsJsonObject().get("deckData").getAsJsonObject().get("colors").getAsString().split(",")).map(s -> "{" + s + "}").collect(Collectors.joining()));
		list.add(deck);
	    }
	 return list;
	}

	@Override
	public String getName() {
		return "TCGPlayer";
	}

}
