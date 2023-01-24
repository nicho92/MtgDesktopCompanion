package org.magic.api.ia.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractIA;
import org.magic.services.MTGConstants;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.URLTools;
import org.magic.services.tools.UITools;

import com.google.gson.JsonObject;

public class ChatGPT extends AbstractIA {

	
	private MTGHttpClient client;

	
	private HttpResponse query( JsonObject obj) throws IOException
	{
		
		if(client==null)
			client = URLTools.newClient();
		
		
		if(getAuthenticator().get("TOKEN")==null)
			throw new IOException("Please fill TOKEN value in Account configuration");
		
		
		var headers = new HashMap<String, String>();
		headers.put("Authorization", "Bearer " + getAuthenticator().get("TOKEN"));
		headers.put(URLTools.CONTENT_TYPE, URLTools.HEADER_JSON);
		
		return client.doPost("https://api.openai.com/v1/completions", new StringEntity(obj.toString(), MTGConstants.DEFAULT_ENCODING), headers);
	}
	
	

	@Override
	public void suggestDeckWith(MagicDeck deck) throws IOException {
		
		if(deck.isEmpty())
			throw new IOException("You should add some cards before calling IA");
		
		var result = ask("Build me a deck with " + deck.getMain().keySet().stream().map(MagicCard::getName).collect(Collectors.joining(",")));
		
		var pattern = Pattern.compile("(\\d+)x (.*?)$");
		for(String s : UITools.stringLineSplit(result, true)) {
				
			logger.info("{}", s);

			var m = pattern.matcher(s);
			
		}
			
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

		var resp = query(obj);
		var json = URLTools.toJson(resp.getEntity().getContent());
		logger.info("{} answer : {} ",getName(), json);
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
	public List<String> listAuthenticationAttributes() {
		return List.of("TOKEN");
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
