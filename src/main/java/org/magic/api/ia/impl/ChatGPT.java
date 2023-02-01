package org.magic.api.ia.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.entity.StringEntity;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractIA;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.URLTools;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ChatGPT extends AbstractIA {

	
	private MTGHttpClient client;
	
	private static String TOKEN = "TOKEN";
	
	private JsonElement query( JsonObject obj) throws IOException
	{
		
		if(client==null)
			client = URLTools.newClient();
		
		
		
		if(getAuthenticator().get(TOKEN)==null)
			throw new IOException("Please fill TOKEN value in Account configuration");
		
		
		var headers = new HashMap<String, String>();
		headers.put("Authorization", "Bearer " + getAuthenticator().get(TOKEN));
		headers.put(URLTools.CONTENT_TYPE, URLTools.HEADER_JSON);
		var resp =  client.doPost("https://api.openai.com/"+getVersion()+"/completions", new StringEntity(obj.toString(), MTGConstants.DEFAULT_ENCODING), headers);
		return URLTools.toJson(resp.getEntity().getContent());
		
	}
	
	

	@Override
	public String suggestDeckWith(List<MagicCard> cards) throws IOException {
			
		if(cards.isEmpty())
			throw new IOException("You should add some cards before asking n IA");
		
		return ask(DECK_QUERY + cards.stream().map(MagicCard::getName).collect(Collectors.joining("/")));
	}
	
	@Override
	public String describe(MagicEdition ed) throws IOException {
		if(ed ==null)
			throw new IOException("You should select a card before calling IA");
		
		return  ask( SET_QUERY+" \"" + ed.getSet() +"\" in "+MTGControler.getInstance().getLocale().getDisplayLanguage(Locale.US));
	}
	

	@Override
	public String describe(MagicCard card) throws IOException {
		
		if(card ==null)
			throw new IOException("You should select a card before calling IA");
		
		return  ask(CARD_QUERY+"\"" + card.getName() +"\" in "+MTGControler.getInstance().getLocale().getDisplayLanguage(Locale.US));
	}
	
	@Override
	public String ask(String prompt) throws IOException
	{
		logger.info("asking : {} ",prompt);
		var obj = new JsonObject();
					obj.addProperty("model", getString("MODEL"));
					obj.addProperty("prompt", prompt);
					obj.addProperty("temperature", getInt("TEMPERATURE"));
					obj.addProperty("max_tokens", getInt("MAX_TOKEN"));

		var json = query(obj);
		
		logger.debug("{} answer : {} ",getName(), json);
		
		try {
			return json.getAsJsonObject().get("choices").getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString();
		}
		catch(Exception e)
		{
			throw new IOException(e);
		}
	}

	@Override
	public String getName() {
		return "ChatGPT";
	}
	
	@Override
	public String getVersion() {
		return "v1";
	}
	
	
	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of(TOKEN);
	}
	
	@Override
	public Map<String, String> getDefaultAttributes() {
			var map = super.getDefaultAttributes();
			map.put("MODEL", "text-davinci-003");
			map.put("TEMPERATURE", "0");
			map.put("MAX_TOKEN", "2000");
			return map;
	}


	
	
}
