package org.magic.api.decksniffer.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
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


	private static final String BASE_URI="https://archidekt.com/api";

	@Override
	public String[] listFilter() {
		return new String[] { "Standard","Modern","Commander / EDH","Legacy","Vintage","Pauper","Custom","Frontier","Future Standard","Penny Dreadful","1v1 Commander","Duel Commander","Brawl","Oathbreaker","Pioneer","Historic","Pauper EDH","Alchemy","Explorer","Historic Brawl","Gladiator","Premodern"};
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
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

		
		deck.setDescription("imported from https://archidekt.com/decks/"+obj.get("id").getAsString());
		
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
	public List<RetrievableDeck> getDeckList(String filter) throws IOException {
		List<RetrievableDeck> ret = new ArrayList<>();


		var arr = RequestBuilder.build()
						.setClient(URLTools.newClient())
						.url(BASE_URI+"/decks/cards/")
						.get()
						.addContent("orderBy", "-createdAt")
						.addContent("formats", String.valueOf(ArrayUtils.indexOf(listFilter(), filter)+1))
						.addContent("pageSize", getString("PAGE_SIZE"))
						.addContent("page","1")
						.addHeader("accept", URLTools.HEADER_JSON)
						.toJson().getAsJsonObject().get("results").getAsJsonArray();



		for(JsonElement el : arr)
		{
			try {
				var d = new RetrievableDeck();
							d.setAuthor(el.getAsJsonObject().get("owner").getAsJsonObject().get("username").getAsString());
							d.setName(el.getAsJsonObject().get("name").getAsString());
					
							var build = new StringBuilder();
								build.append(BASE_URI).append("/decks/").append(el.getAsJsonObject().get("id").getAsInt()).append("/");

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

		return ret;
	}

	@Override
	public String getName() {
		return "Archidekt";
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		
		var m = super.getDefaultAttributes();
		
		m.put("PAGE_SIZE", MTGProperty.newIntegerProperty("50", "number of items per page to query", 50, 100));
		
		return m;
	}

}
