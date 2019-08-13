package org.magic.api.decksniffer.impl;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.MTGControler;
import org.magic.tools.RequestBuilder;
import org.magic.tools.RequestBuilder.METHOD;
import org.magic.tools.URLTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ArchidektDeckSniffer extends AbstractDeckSniffer {

	
	private static final String BASE_URI="https://archidekt.com/api/decks/cards/";
	
	@Override
	public String[] listFilter() {
		return new String[] { "Standard","Modern","Commander / EDH","Legacy","Vintage","Pauper","Frontier","Future Standard"};
	}
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public MagicDeck getDeck(RetrievableDeck info) throws IOException {
		
		logger.debug("sniff deck at " + info.getUrl());
		
		MagicDeck deck = new MagicDeck();
				  deck.setName(info.getName());
				  deck.setDescription("Import from : " + info.getUrl());
				  
				  	   JsonObject obj = RequestBuilder.build()
									   .setClient(URLTools.newClient())
									   .url(info.getUrl().toString())
									   .method(METHOD.GET)
									   .toJson().getAsJsonObject();
		
		
		JsonArray cards = obj.get("cards").getAsJsonArray();
		
		for(JsonElement e : cards)
		{
			try {
				int qty = e.getAsJsonObject().get("quantity").getAsInt();
				
				MagicEdition ed = null;
				if( e.getAsJsonObject().get("card").getAsJsonObject().get("edition")!=null) {
					String edcode = e.getAsJsonObject().get("card").getAsJsonObject().get("edition").getAsJsonObject().get("editioncode").getAsString();
					ed = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getSetById(edcode);
				}
				String number = e.getAsJsonObject().get("card").getAsJsonObject().get("collectorNumber").getAsString();
				MagicCard mc = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getCardByNumber(number, ed);
				
				if(mc!=null)
				{
					deck.getMap().put(mc, qty);
					notify(mc);
				}
				
			}
			catch(Exception ex)
			{
				logger.error("error parsing " + e, ex);
			}
			
		}
		
		return deck;
	}

	
	public static void main(String[] args) throws IOException {
		
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).init();
		ArchidektDeckSniffer snif =  new ArchidektDeckSniffer();
		
		RetrievableDeck d = snif.getDeckList().get(1);
		
		snif.getDeck(d);
		
		
		
	}
	
	@Override
	public List<RetrievableDeck> getDeckList() throws IOException {
		List<RetrievableDeck> ret = new ArrayList<>();
		
		
		JsonArray arr = RequestBuilder.build()
						.setClient(URLTools.newClient())
						.url(BASE_URI).method(METHOD.GET)
						.addContent("orderBy", "-createdAt")
						.addContent("formats", String.valueOf(ArrayUtils.indexOf(listFilter(), getString("FORMAT"))+1))
						.addContent("pageSize", getString("PAGE_SIZE"))
						.addContent("page","1")
						.addHeader("accept", URLTools.HEADER_JSON)
						.toJson().getAsJsonObject().get("results").getAsJsonArray();
		
		
	
		for(JsonElement el : arr)
		{
			try {
				RetrievableDeck d = new RetrievableDeck();
							d.setAuthor(el.getAsJsonObject().get("owner").getAsJsonObject().get("username").getAsString());
							d.setName(el.getAsJsonObject().get("name").getAsString());
							d.setUrl(new URI("https://archidekt.com/api/decks/"+el.getAsJsonObject().get("id").getAsInt()+"/"));
							StringBuilder tmp = new StringBuilder("");
							
							for(String s : el.getAsJsonObject().get("colors").getAsJsonObject().keySet())
								tmp.append("{").append(s).append("}");
							
							d.setColor(tmp.toString());
				
				ret.add(d);		
							
			}
			catch(Exception ex)
			{
				logger.error("error parsing " + el, ex);
			}
		}
		
		return ret;
	}

	@Override
	public String getName() {
		return "Archidekt";
	}
	
	@Override
	public void initDefault() {
		setProperty("FORMAT", "");
		setProperty("PAGE_SIZE","50");
	}

}
