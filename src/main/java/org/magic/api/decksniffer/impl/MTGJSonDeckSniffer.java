package org.magic.api.decksniffer.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.api.providers.impl.Mtgjson4Provider;
import org.magic.services.MTGControler;
import org.magic.tools.URLTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MTGJSonDeckSniffer extends AbstractDeckSniffer {

	@Override
	public String[] listFilter() {
		return new String[] {""};
	}

	@Override
	public MagicDeck getDeck(RetrievableDeck info) throws IOException {
		JsonElement el = URLTools.extractJson(info.getUrl().toString());
		JsonArray mainBoard = el.getAsJsonObject().get("mainBoard").getAsJsonArray();
		JsonArray sideBoard=null;
		
		if(el.getAsJsonObject().get("sideBoard")!=null)
			sideBoard = el.getAsJsonObject().get("sideBoard").getAsJsonArray();
		
		
		MagicDeck deck = new MagicDeck();
				  deck.setName(info.getName());
		
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
	
	
	private void init(MagicDeck d, JsonArray arr, boolean side)
	{
			arr.forEach(element->{
			
			String ed = element.getAsJsonObject().get("printings").getAsJsonArray().get(0).getAsString();
			int qty = element.getAsJsonObject().get("count").getAsInt();
			String name = element.getAsJsonObject().get("name").getAsString();
			try {
				MagicCard mc = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName(name, new MagicEdition(ed), true).get(0);
				if(!side)
					d.getMain().put(mc, qty);
				else
					d.getSideBoard().put(mc, qty);
				
				notify(mc);
			} catch (IOException e) {
				logger.error("error loading " + name+"/"+ed,e);
			}
			
		});
	}
	

	@Override
	public List<RetrievableDeck> getDeckList() throws IOException {
		JsonElement d = URLTools.extractJson(Mtgjson4Provider.URL_JSON_DECKS_LIST);
		JsonArray arr = d.getAsJsonObject().get("decks").getAsJsonArray();
		
		List<RetrievableDeck> decks = new ArrayList<>();
		arr.forEach(element ->{
			
			JsonObject ob = element.getAsJsonObject();
			
			RetrievableDeck rd = new RetrievableDeck();
							rd.setName(ob.get("name").getAsString());
							rd.setAuthor("MtgJson");
							try {
								rd.setDescription(MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getSetById(ob.get("code").getAsString()).getSet());
								rd.setUrl(new URL(Mtgjson4Provider.URL_DECKS_URI+ob.get("fileName").getAsString()+"_"+ob.get("code").getAsString()+".json").toURI());
							} catch (Exception e) {
								logger.error(e);
							}
							
			decks.add(rd);				
			
		});
		
		return decks;
		
	}

	@Override
	public String getName() {
		return "MTGJson4";
	}

}
