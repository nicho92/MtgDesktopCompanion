package org.magic.api.decksniffer.impl;

import java.io.IOException;
import java.net.URI;
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
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

import com.google.gson.JsonObject;

public class MoxfieldDeckSniffer extends AbstractDeckSniffer {


	private static final String BASE_URI = "https://api.moxfield.com/v2";
	MTGHttpClient client;


	public MoxfieldDeckSniffer() {
		client = URLTools.newClient();
	}

	@Override
	public String[] listFilter() {
		return new String[]{"Archon","highlanderAustralian","Brawl","highlanderCanadian","Centurion","Commander","CommanderPrecons","Conquest","DuelCommander","Gladiator","Historic","HistoricBrawl","Legacy","Leviathan","Modern","OldSchool","Oathbreaker","Pauper","PauperEDH","PennyDreadful","Pioneer","Precons","Premodern","Primordial","Standard","Vintage","None"};
	}

	@Override
	public MTGDeck getDeck(RetrievableDeck info) throws IOException {
		var deck = info.toBaseDeck();

		var json = RequestBuilder.build()
			  .setClient(client)
			  .url(info.getUrl())
			  .get()
			  .toJson().getAsJsonObject();


		load(json,"mainboard",deck.getMain());
		load(json,"sideboard",deck.getSideBoard());


		if(json.get("commandersCount").getAsInt()>0)
		{
			var map = new HashMap<MTGCard, Integer>();
			load(json,"commanders",map);

			deck.setCommander(map.keySet().iterator().next());
			deck.getMain().put(deck.getCommander(), 1);
		}


		return deck;
	}

	private void load(JsonObject json,String key, Map<MTGCard, Integer> main) {


		json.get(key).getAsJsonObject().entrySet().forEach(entry->{
			try {
				var qty = entry.getValue().getAsJsonObject().get("quantity").getAsInt();
				var scryId = entry.getValue().getAsJsonObject().get("card").getAsJsonObject().get("scryfall_id").getAsString();
				var mc = MTG.getEnabledPlugin(MTGCardsProvider.class).getCardByScryfallId(scryId);
				main.put(mc, qty);
				notify(mc);
			} catch (IOException e) {
				logger.error("{} is not found",entry.getKey());
			}

		});
	}
	
	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var m = super.getDefaultAttributes();
		m.put("MAX_PAGE", MTGProperty.newIntegerProperty("2", "number of page to query", 1, 10));
		return m;
	}
	
	
	@Override
	public boolean hasCardFilter() {
		return true;
	}
		
	@Override
	public List<RetrievableDeck> getDeckList(MTGCard filter) throws IOException {
		return super.getDeckList(filter);
	}
	
	
	

	@Override
	public List<RetrievableDeck> getDeckList(String filter) throws IOException {
		
		var ret = new ArrayList<RetrievableDeck>();
		
		for(var i = 1; i<=getInt("MAX_PAGE");i++)
		{
			var json = RequestBuilder.build()
					  .setClient(client)
					  .get()
					  .url(BASE_URI+"/decks/search?pageNumber="+i+"&pageSize=128&sortType=updated&sortDirection=Descending&fmt="+filter+"&filter=")
					  .toJson();


		
			for(var je : json.getAsJsonObject().get("data").getAsJsonArray())
			{
				var jo = je.getAsJsonObject();
	
				var dekElement = new RetrievableDeck();
					dekElement.setName(jo.get("name").getAsString());
					dekElement.setAuthor(jo.get("authors").getAsJsonArray().get(0).getAsJsonObject().get("userName").getAsString());
					dekElement.setUrl(URI.create(BASE_URI+"/decks/all/"+jo.get("publicId").getAsString()));
					dekElement.setDescription(UITools.formatDateTime(UITools.parseGMTDate(jo.get("createdAtUtc").getAsString())));
				
					var c = new StringBuilder();
					jo.get("colors").getAsJsonArray().asList().stream().map(j->j.getAsString()).forEach(s->c.append("{").append(s).append("}"));
							
					dekElement.setColor(c.toString());
					
				ret.add(dekElement);
			}


		}
		
		



		return ret;

	}

	@Override
	public String getName() {
		return "MoxField";
	}
}
