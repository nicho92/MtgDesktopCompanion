package org.magic.api.decksniffer.impl;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.tools.MTGArenaTools;

import com.google.gson.JsonObject;

public class MTGArenaDeckSniffer extends AbstractDeckSniffer {


	private static final String ARENA_LOG_FILE = "ARENA_LOG_FILE";

	private MTGArenaTools arena ;
	
	
	private void init()
	{
		try {
			arena = new MTGArenaTools(getFile(ARENA_LOG_FILE));
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	@Override
	public String[] listFilter() {
		return new String[] { "Game"};
	}

	@Override
	public MagicDeck getDeck(RetrievableDeck info) throws IOException {
		
		if(arena==null)
			init();
		
			var d = info.toBaseDeck();
			arena.readDecks().get("payload").getAsJsonArray().forEach(je->{
				var obj = je.getAsJsonObject();
				if(obj.get("id").getAsString().equals(info.getUrl().toString()))
				{
					load(obj,d.getMain(),"mainDeck");
					load(obj,d.getSideBoard(),"sideboard");
				}
			});
				  
				  
		
		return d;
	}
	
	private void load(JsonObject obj, Map<MagicCard, Integer> map, String string) {
		var arr = obj.get(string).getAsJsonArray();
		
		List<String> ids= new ArrayList<>();
		List<Integer> qtys = new ArrayList<>();
		
		
		for(var i=0;i<arr.size();i=i+2)
		{
			ids.add(arr.get(i).getAsString());
		}
		
		for(var i=1;i<arr.size();i=i+2)
		{
			qtys.add(arr.get(i).getAsInt());
		}
		
		logger.debug("found " + ids.size() +" ids and " + qtys.size() +" qty");
		
		
		for(var i=0;i<ids.size();i++)
		{
			try {
				var mc = getEnabledPlugin(MTGCardsProvider.class).getCardByArenaId(ids.get(i));
				map.put(mc, qtys.get(i));
				notify(mc);
			} catch (IOException e) {
				logger.error("no cards found for " + ids.get(i));
			}
		}
		
	}

	@Override
	public List<RetrievableDeck> getDeckList(String filter) throws IOException {
		
		if(arena==null)
			init();
		
		List<RetrievableDeck> ret = new ArrayList<>();
		JsonObject json = arena.readDecks();
		
		json.get("payload").getAsJsonArray().forEach(je->{
			
			var obj = je.getAsJsonObject();
			var d = new RetrievableDeck();
							d.setName(obj.get("name").getAsString());
							
							if(d.getName().startsWith("?"))
							{
								d.setName(d.getName().substring(d.getName().indexOf("_")+1));
								d.setAuthor("Preconstruct");
							}
							else
							{
								d.setAuthor(arena.getAccount());
							}
							
							try {
							d.setDescription(obj.get("description").getAsString());
							}
							catch(Exception e)
							{
								//do nothing
							}
							
							try {
								d.setUrl(new URI(obj.get("id").getAsString()));
							} catch (URISyntaxException e) {
								logger.error(e);
							}
							
							ret.add(d);
		});
		
		return ret;
	}

	@Override
	public String getName() {
		return "MTGArena";
	}
	
	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of(ARENA_LOG_FILE,"C:\\Users\\"+System.getProperty("user.name")+"\\AppData\\LocalLow\\Wizards Of The Coast\\MTGA\\Player.log");
	}
}
