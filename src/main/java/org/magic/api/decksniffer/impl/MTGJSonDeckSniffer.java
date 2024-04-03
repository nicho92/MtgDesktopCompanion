package org.magic.api.decksniffer.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;


import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.technical.RetrievableDeck;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.api.interfaces.abstracts.extra.AbstractMTGJsonProvider;
import org.magic.services.network.URLTools;

import com.google.gson.JsonArray;

public class MTGJSonDeckSniffer extends AbstractDeckSniffer {

	@Override
	public String[] listFilter() {
		return new String[] {"<no filter>"};
	}

	@Override
	public MTGDeck getDeck(RetrievableDeck info) throws IOException {
		var el = URLTools.extractAsJson(info.getUrl().toString()).getAsJsonObject();
		var mainBoard = el.get("data").getAsJsonObject().get("mainBoard").getAsJsonArray();
		JsonArray sideBoard=null;

		if(el.getAsJsonObject().get("sideBoard")!=null)
			sideBoard = el.getAsJsonObject().get("sideBoard").getAsJsonArray();


		MTGDeck deck = info.toBaseDeck();

		try {
			deck.getTags().add(el.getAsJsonObject().get("type").getAsString());
		}catch(Exception e)
		{
			//do nothing
		}



		init(deck,mainBoard,false);

		if(sideBoard!=null)
			init(deck,sideBoard,true);

		return deck;
	}


	private void init(MTGDeck d, JsonArray arr, boolean side)
	{
			arr.forEach(element->{

			var ed = element.getAsJsonObject().get("printings").getAsJsonArray().get(0).getAsString();
			var qty = element.getAsJsonObject().get("count").getAsInt();
			var name = element.getAsJsonObject().get("name").getAsString();
			try {
				MTGCard mc = getEnabledPlugin(MTGCardsProvider.class).searchCardByName(name, new MTGEdition(ed), true).get(0);
				if(!side)
					d.getMain().put(mc, qty);
				else
					d.getSideBoard().put(mc, qty);

				notify(mc);
			} catch (Exception e) {
				logger.error("error loading {}/{}",name,ed,e);
			}

		});
	}


	@Override
	public List<RetrievableDeck> getDeckList(String filter) throws IOException {
		var d = URLTools.extractAsJson(AbstractMTGJsonProvider.MTG_JSON_DECKS_LIST);
		var arr = d.getAsJsonObject().get("data").getAsJsonArray();

		List<RetrievableDeck> decks = new ArrayList<>();
		arr.forEach(element ->{

			var ob = element.getAsJsonObject();

			var rd = new RetrievableDeck();
							rd.setName(ob.get("name").getAsString());
							rd.setAuthor("MtgJson");
							try {
								rd.setDescription(getEnabledPlugin(MTGCardsProvider.class).getSetById(ob.get("type").getAsString()).getSet());
								rd.setUrl(URI.create(AbstractMTGJsonProvider.MTG_JSON_DECKS+ob.get("fileName").getAsString()+".json"));
							} catch (Exception e) {
								logger.error(e);
							}

			decks.add(rd);

		});

		return decks;

	}

	@Override
	public String getName() {
		return "MTGJson";
	}

}
