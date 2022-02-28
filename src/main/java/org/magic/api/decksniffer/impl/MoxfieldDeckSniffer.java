package org.magic.api.decksniffer.impl;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.RequestBuilder.METHOD;
import org.magic.services.network.URLTools;
import org.magic.tools.MTG;
import org.magic.tools.UITools;

import com.google.gson.JsonObject;

public class MoxfieldDeckSniffer extends AbstractDeckSniffer {
	
	
	private static final String BASE_URI = "https://api.moxfield.com/v2";
	MTGHttpClient client;
	
	
	public MoxfieldDeckSniffer() {
		client = URLTools.newClient();
	}
	
	@Override
	public String[] listFilter() {
		return new String[]{"Archon","highlanderAustralian","Brawl","highlanderCanadian","Centurion","Commander","CommanderPrecons","Conquest","DuelCommander","Gladiator","Historic","HistoricBrawl","Legacy","Leviathan","Modern","OldSchool","Oathbreaker","Pauper","PauperEDH","PennyDreadful","Pioneer","Premodern","Primordial","Standard","Vintage","None"};
	}

	@Override
	public MagicDeck getDeck(RetrievableDeck info) throws IOException {
		var deck = info.toBaseDeck();
		
		var json = RequestBuilder.build()
			  .setClient(client)
			  .method(METHOD.GET)
			  .url(info.getUrl())
			  .toJson().getAsJsonObject();
		
		
		load(json,"mainboard",deck.getMain());
		load(json,"sideboard",deck.getSideBoard());
		
		
		if(json.get("commandersCount").getAsInt()>0)
		{
			var map = new HashMap<MagicCard, Integer>();
			load(json,"commanders",map);
			
			deck.setCommander(map.keySet().iterator().next());
			deck.getMain().put(deck.getCommander(), 1);
		}
		
		
		return deck;
	}

	private void load(JsonObject json,String key, Map<MagicCard, Integer> main) {

		
		json.get(key).getAsJsonObject().entrySet().forEach(entry->{
			try {
				var qty = entry.getValue().getAsJsonObject().get("quantity").getAsInt();
				var scryId = entry.getValue().getAsJsonObject().get("card").getAsJsonObject().get("scryfall_id").getAsString();
				var mc = MTG.getEnabledPlugin(MTGCardsProvider.class).getCardByScryfallId(scryId);
				main.put(mc, qty);
				notify(mc);
			} catch (IOException e) {
				logger.error(entry.getKey() + " is not found");
			}
			
		});
		
		
	}

	@Override
	public List<RetrievableDeck> getDeckList(String filter) throws IOException {
		var json = RequestBuilder.build()
					  .setClient(client)
					  .method(METHOD.GET)
					  .url(BASE_URI+"/decks/search?pageNumber=1&pageSize=128&sortType=updated&sortDirection=Descending&fmt="+filter+"&filter=")
					  .toJson();
		
		
		var ret = new ArrayList<RetrievableDeck>();
		for(var je : json.getAsJsonObject().get("data").getAsJsonArray())
		{
			var jo = je.getAsJsonObject();
			
			var dekElement = new RetrievableDeck();
				dekElement.setName(jo.get("name").getAsString());
				dekElement.setAuthor(jo.get("authors").getAsJsonArray().get(0).getAsJsonObject().get("userName").getAsString());
				dekElement.setUrl(URI.create(BASE_URI+"/decks/all/"+jo.get("publicId").getAsString()));
				dekElement.setDescription(UITools.formatDateTime(UITools.parseGMTDate(jo.get("createdAtUtc").getAsString())));
				
				
			ret.add(dekElement);
		}
		
		
		
		
		
		return ret;
		
	}

	@Override
	public String getName() {
		return "MoxField";
	}
}
