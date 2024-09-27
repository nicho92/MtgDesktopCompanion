package org.magic.api.ia.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractIA;
import org.magic.services.MTGConstants;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.URLTools;

import com.google.gson.JsonObject;

public class WriteSonic extends AbstractIA {

	
	private static final String X_API_KEY = "X-API-KEY";
	private MTGHttpClient client;

	
	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var map = super.getDefaultAttributes();
		
		map.put("GOOGLE_RESULTS", MTGProperty.newBooleanProperty("false", "use google search for result"));
		map.put("ENABLE_MEMORY", MTGProperty.newBooleanProperty("true", "ChatSonic can maintain the context of your conversations just like you would with a person. It remembers past questions or comments in your conversation and can easily answer follow-up questions."));
		return map;
	}
	
	
	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of(X_API_KEY);
	}
	

	@Override
	public String ask(String prompt) throws IOException {
		return query(prompt).get("message").getAsString();
	}
	
	protected JsonObject query(String prompt) throws IOException
	{

		if(client==null)
			client = URLTools.newClient();
		
		var headers = new HashMap<String, String>();
				headers.put(URLTools.ACCEPT, URLTools.HEADER_JSON);
				headers.put(URLTools.CONTENT_TYPE, URLTools.HEADER_JSON);
				headers.put(X_API_KEY, getAuthenticator().get(X_API_KEY));

		
		var obj = new JsonObject();
				 obj.addProperty("enable_google_results", getBoolean("GOOGLE_RESULTS"));
				 obj.addProperty("enable_memory", getBoolean("ENABLE_MEMORY"));
				 obj.addProperty("input_text", prompt);
		
		var resp = client.doPost("https://api.writesonic.com/v2/business/content/chatsonic?engine=premium", new StringEntity(obj.toString(), MTGConstants.DEFAULT_ENCODING), headers);
		var res = URLTools.toJson(resp.getEntity().getContent());
		
		logger.debug("{} response : {}", getName(),res);
		return res.getAsJsonObject();
	}
	
	

	@Override
	public String getName() {
		return "ChatSonic";
	}


	@Override
	public MTGCard generateRandomCard(String description) throws IOException {
		var ret = ask(NEW_CARD_QUERY  +( (description==null || description.isEmpty())?"": " with this description  : "+description));
		if(ret==null)
			return null;
		
		ret = StringUtils.substringBetween(ret,"\u0060\u0060\u0060").replace("JSON", "").replace("json", "").trim();
		var obj = URLTools.toJson(ret).getAsJsonObject();
		return parseIaCardSuggestion(obj);
	}
	
}
