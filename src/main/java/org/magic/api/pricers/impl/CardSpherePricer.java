package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGPrice;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.logging.MTGLogger;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;

public class CardSpherePricer extends AbstractPricesProvider {

	private MTGHttpClient client;


	@Override
	public String getName() {
		return "CardSphere";
	}
	
	
	public CardSpherePricer() {
		client = URLTools.newClient();
	}
	
	public static void main(String[] args) throws IOException {
		
		MTGLogger.changeLevel(Level.DEBUG);
		
		var c = new MTGCard();
			  c.setScryfallId("59b6fe0c-7bbe-433f-8400-4be4ce0e3f15");
			  
			  new CardSpherePricer().getLocalePrice(c);
	}
	

	@Override
	protected List<MTGPrice> getLocalePrice(MTGCard card) throws IOException {
		var ret = new ArrayList<MTGPrice>();
		
		var arr = RequestBuilder.build()
						.setClient(client)
						.url("https://www.multiversebridge.com/api/v1/cards/scryfall/"+card.getScryfallId())
						.get()
						.toJson().getAsJsonArray();
		
		
			arr.forEach(je->{
				
				
				try {
					var obj = je.getAsJsonObject();
					var foil = obj.get("is_foil").getAsBoolean();
					
					RequestBuilder.build().url("https://www.cardsphere.com"+obj.get("url").getAsString()).setClient(client).get().execute();
					
					var doc = RequestBuilder.build().url("https://www.cardsphere.com/buynow"+obj.get("url").getAsString()+"/condition/0/language/ANY")
									.setClient(client)
									.addHeader(URLTools.ACCEPT,"*/*")
									.addHeader(URLTools.ACCEPT_LANGUAGE,"fr-FR,fr;q=0.9,en;q=0.8")
									.addHeader(URLTools.ACCEPT_ENCODING,"gzip, deflate, br, zstd")
									
									.addHeader("Pragma","no-cache")
									.addHeader("Sec-Fetch-Dest", "document")
									.addHeader("Sec-Fetch-Mode", "navigate")
									.addHeader("Sec-Fetch-Site", "cross-site")
									.addHeader("Sec-Ch-Ua-Platform", "\"Windows\"")
									.addHeader("Sec-Ch-Ua-Mobile", "?0")
									.addHeader("Sec-Fetch-User", "?1")
									.addHeader("Upgrade-Insecure-Requests", "1")
									.addHeader("X-Requested-With", "XMLHttpRequest")
									.get()
									.toHtml();
		
					
					System.out.println(doc);
					
				}
				catch(Exception e)
				{
					//do nothing; https://www.cardsphere.com/buynow/cards/69169/condition/0/language/ANY 
				}
			});
		
		
		return ret;
	}

}
