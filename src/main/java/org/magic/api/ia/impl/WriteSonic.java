package org.magic.api.ia.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.entity.StringEntity;
import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.abstracts.AbstractIA;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.URLTools;

import com.google.gson.JsonObject;

public class WriteSonic extends AbstractIA {

	
	private MTGHttpClient client;

	
	@Override
	public Map<String, String> getDefaultAttributes() {
		
		var map = super.getDefaultAttributes();
		map.put("GOOGLE_RESULTS", FALSE);
		map.put("ENABLE_MEMORY", TRUE);
		return map;
	}
	
	
	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of("X-API-KEY");
	}
	
	private JsonObject query(String prompt) throws IOException
	{

		if(client==null)
			client = URLTools.newClient();
		
		var headers = new HashMap<String, String>();
		headers.put("accept", URLTools.HEADER_JSON);
		headers.put(URLTools.CONTENT_TYPE, URLTools.HEADER_JSON);
		headers.put("X-API-KEY", getAuthenticator().get("X-API-KEY"));

		
		JsonObject obj = new JsonObject();
						 obj.addProperty("enable_google_results", getBoolean("GOOGLE_RESULTS"));
						 obj.addProperty("enable_memory", getBoolean("ENABLE_MEMORY"));
						 obj.addProperty("input_text", prompt);
		
		var resp = client.doPost("https://api.writesonic.com/v2/business/content/chatsonic?engine=premium", new StringEntity(obj.toString(), MTGConstants.DEFAULT_ENCODING), headers);
		return URLTools.toJson(resp.getEntity().getContent()).getAsJsonObject();
	}
	
	
	@Override
	public String ask(String prompt) throws IOException {
		return query(prompt).get("message").getAsString();
	}
	
	@Override
	public String suggestDeckWith(List<MagicCard> cards) throws IOException {
		if(cards.isEmpty())
			throw new IOException("You should add some cards before asking n IA");
		
		return ask("Build a magic the gathering deck with this cards : " + cards.stream().map(MagicCard::getName).collect(Collectors.joining("/")));
	}

	@Override
	public String describe(MagicCard card) throws IOException {
		if(card ==null)
			throw new IOException("You should select a card before calling IA");
		
		return  ask("tell me more about MTG card \"" + card.getName() +"\" in "+MTGControler.getInstance().getLocale().getDisplayLanguage(Locale.US));
	}

	@Override
	public String getName() {
		return "ChatSonic";
	}

}
