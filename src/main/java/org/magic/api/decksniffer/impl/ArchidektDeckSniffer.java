package org.magic.api.decksniffer.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.beans.technical.RetrievableDeck;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;

import com.google.gson.JsonElement;

public class ArchidektDeckSniffer extends AbstractDeckSniffer {


	private static final String BASE_URI="https://archidekt.com";
	private String endpoint="_XCgIDt-I_rfG6NsvNy49";

	@Override
	public String[] listFilter() {
		return new String[] { "Standard","Modern","Commander / EDH","Legacy","Vintage","Pauper","Custom","Frontier","Future Standard","Penny Dreadful","1v1 Commander","Duel Commander","Brawl","Oathbreaker","Pioneer","Historic","Pauper EDH","Alchemy","Explorer","Historic Brawl","Gladiator","Premodern"};
	}

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

	
	@Override
	public MTGDeck getDeck(RetrievableDeck info) throws IOException {

		logger.debug("sniff deck at {}",info.getUrl());

		var deck = info.toBaseDeck();
		
		var obj = RequestBuilder.build()
				   .setClient(URLTools.newClient())
				   .url(info.getUrl().toString())
				   .get()
				   .toJson().getAsJsonObject();

		
		deck.setDescription("imported from "+BASE_URI+"/decks/"+obj.get("id").getAsString());
		
		var cards = obj.get("cards").getAsJsonArray();

		for(JsonElement e : cards)
		{
			try {
				var qty = e.getAsJsonObject().get("quantity").getAsInt();

				MTGEdition ed = null;
				if( e.getAsJsonObject().get("card").getAsJsonObject().get("edition")!=null) {
					var edcode = e.getAsJsonObject().get("card").getAsJsonObject().get("edition").getAsJsonObject().get("editioncode").getAsString();
					ed = getEnabledPlugin(MTGCardsProvider.class).getSetById(edcode);
				}
				var number = e.getAsJsonObject().get("card").getAsJsonObject().get("collectorNumber").getAsString();
				var mc = getEnabledPlugin(MTGCardsProvider.class).getCardByNumber(number, ed);

				if(mc!=null)
				{
					deck.getMain().put(mc, qty);
					notify(mc);
					
					if(e.getAsJsonObject().get("categories").getAsJsonArray().get(0).getAsString().equals("Commander"))
						deck.setCommander(mc);
					
					
				}

			}
			catch(Exception ex)
			{
				logger.error("error parsing {}",e, ex);
			}

		}

		return deck;
	}
	
	
	@Override
	public boolean hasCardFilter() {
		return true;
	}
	
	@Override
	public List<RetrievableDeck> getDeckList(String filter, MTGCard mc) throws IOException {
		List<RetrievableDeck> ret = new ArrayList<>();

		
		for(var i = 1; i<=getInt("MAX_PAGE");i++)
		{

			
			var q = RequestBuilder.build()
							.setClient(URLTools.newClient())
							.url(BASE_URI+"/_next/data/"+endpoint+"/search/decks.json")
							.get()
							.addContent("orderBy", "-createdAt")
							.addContent("deckFormat", String.valueOf(ArrayUtils.indexOf(listFilter(), filter)+1))
							.addContent("pageSize", "50")
							.addContent("page",String.valueOf(i))
							.addHeader("accept", URLTools.HEADER_JSON);
			
			
				if(mc!=null)
					q.addContent("cardName",mc.getName());
			
			
			
			var arr = q.toJson().getAsJsonObject().get("pageProps").getAsJsonObject().get("deckResults").getAsJsonObject().get("results").getAsJsonArray();


			for(JsonElement el : arr)
			{
				try {
					var d = new RetrievableDeck();
								d.setAuthor(el.getAsJsonObject().get("owner").getAsJsonObject().get("username").getAsString());
								d.setName(el.getAsJsonObject().get("name").getAsString());
						
								var build = new StringBuilder();
									build.append(BASE_URI).append("/api/decks/").append(el.getAsJsonObject().get("id").getAsInt()).append("/");

								d.setUrl(new URI(build.toString()));
								var tmp = new StringBuilder("");

								for(var s : el.getAsJsonObject().get("colors").getAsJsonObject().entrySet())
									{
										if(s.getValue().getAsInt()>0)
											tmp.append("{").append(s.getKey()).append("}");
									}

								d.setColor(tmp.toString());

					ret.add(d);

				}
				catch(Exception ex)
				{
					logger.error("error parsing {}",el, ex);
				}
		}
		
		}

		return ret;
	}

	@Override
	public String getName() {
		return "Archidekt";
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		
		var m = super.getDefaultAttributes();
		m.put("MAX_PAGE", MTGProperty.newIntegerProperty("2", "number of page to query", 1, 10));
		
		return m;
	}

}
