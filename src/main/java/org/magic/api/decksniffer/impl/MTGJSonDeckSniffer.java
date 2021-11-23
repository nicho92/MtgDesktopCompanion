package org.magic.api.decksniffer.impl;

import static org.magic.tools.MTG.getEnabledPlugin;

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
import org.magic.api.interfaces.abstracts.AbstractMTGJsonProvider;
import org.magic.services.network.URLTools;

import com.google.gson.JsonArray;

public class MTGJSonDeckSniffer extends AbstractDeckSniffer {

	@Override
	public String[] listFilter() {
		return new String[] {""};
	}

	@Override
	public MagicDeck getDeck(RetrievableDeck info) throws IOException {
		var el = URLTools.extractAsJson(info.getUrl().toString()).getAsJsonObject();
		var mainBoard = el.get("data").getAsJsonObject().get("mainBoard").getAsJsonArray();
		JsonArray sideBoard=null;
		
		if(el.getAsJsonObject().get("sideBoard")!=null)
			sideBoard = el.getAsJsonObject().get("sideBoard").getAsJsonArray();
		
		
		MagicDeck deck = info.toBaseDeck();
		
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
			
			var ed = element.getAsJsonObject().get("printings").getAsJsonArray().get(0).getAsString();
			var qty = element.getAsJsonObject().get("count").getAsInt();
			var name = element.getAsJsonObject().get("name").getAsString();
			try {
				MagicCard mc = getEnabledPlugin(MTGCardsProvider.class).searchCardByName(name, new MagicEdition(ed), true).get(0);
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
								rd.setUrl(new URL(AbstractMTGJsonProvider.MTG_JSON_DECKS+ob.get("fileName").getAsString()+".json").toURI());
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
