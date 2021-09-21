package org.beta;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGControler;
import org.magic.tools.RequestBuilder;
import org.magic.tools.RequestBuilder.METHOD;
import org.magic.tools.URLTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class CardTraderPricer extends AbstractCardExport {

	private static final String TOKEN_FULL = "TOKEN";
	private String baseUrl = "https://api.cardtrader.com/api/full/"; 
	private HashMap<String,Integer> mapExpension;
	
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	public CardTraderPricer() {
		mapExpension=new HashMap<>();
	}
	
		
	@Override
	public String getVersion() {
		return "v1";
	}

	@Override
	public String getName() {
		return "CardTrader";
	}
	
	protected void test() throws IOException {
		
		var cardName = "Esper Sentinel";
		var setId = "MH2";
		
		
		
		String url = baseUrl+getVersion()+"/blueprints/export";
		JsonArray ids = RequestBuilder.build().setClient(URLTools.newClient()).method(METHOD.GET).url(url)
							.addContent("category_id", "1")
							.addContent("game_id", "1")
							.addContent("name", cardName)
							.addHeader("Authorization", "Bearer "+getAuthenticator().get(TOKEN_FULL))
							.toJson().getAsJsonArray();
		
		var idSet = getExpensions().get(setId);
		
		var idBluePrints=-1;
		
		for(JsonElement el : ids)
		{
			if(el.getAsJsonObject().get("expansion_id").getAsInt()==idSet)
			{
				idBluePrints = el.getAsJsonObject().get("id").getAsInt();
				break;
			}
		}
		
		logger.debug(ids);
		logger.debug(idBluePrints);
		
	
		
		
		
	}
	

	public static void main(String[] args) throws IOException {
		new CardTraderPricer().test();
	}
	
	
	public Map<String, Integer> getExpensions() throws IOException
	{
		if(mapExpension.isEmpty())
		{
			String url = baseUrl+getVersion()+"/expansions";
			JsonArray expensions = RequestBuilder.build().setClient(URLTools.newClient()).method(METHOD.GET).url(url)
								.addHeader("Authorization", "Bearer "+getAuthenticator().get(TOKEN_FULL))
								.toJson().getAsJsonArray();
	
			expensions.forEach(c->{
				if(c.getAsJsonObject().get("game_id").getAsInt()==1)
					mapExpension.put(c.getAsJsonObject().get("code").getAsString().toUpperCase(),c.getAsJsonObject().get("id").getAsInt());
			});
		}
		
		
		return mapExpension;
	}

	@Override
	public String getFileExtension() {
		return ".csv";
	}

	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
		
	
}
