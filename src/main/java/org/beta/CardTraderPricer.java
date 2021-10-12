package org.beta;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.api.cardtrader.modele.BluePrint;
import org.api.cardtrader.modele.Categorie;
import org.api.cardtrader.modele.Expansion;
import org.api.cardtrader.services.CardTraderService;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGControler;
import org.magic.tools.RequestBuilder;
import org.magic.tools.RequestBuilder.METHOD;
import org.magic.tools.URLTools;

import com.google.gson.JsonArray;

public class CardTraderPricer extends AbstractCardExport {

	private static final String TOKEN_FULL = "TOKEN";
	private String baseUrl = "https://api.cardtrader.com/api/full/"; 
	private HashMap<String,Integer> mapExpension;
	private CardTraderService service;
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	public CardTraderPricer() {
		mapExpension=new HashMap<>();
	}
	
	
	private void init()
	{
		if(service==null)
			service = new CardTraderService(getAuthenticator().get(TOKEN_FULL));
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
		init();
		var cardName = "Esper Sentinel";
		var setId = "MH2";
		
		List<BluePrint> p = service.listBluePrintsByIds(1, cardName, service.listExpansions().stream().filter(c->c.getCode().equalsIgnoreCase(setId)).map(Expansion::getId).findFirst().orElse(null));
	

	}
	

	public static void main(String[] args) throws IOException {
		MTGControler.getInstance().loadAccountsConfiguration();
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
