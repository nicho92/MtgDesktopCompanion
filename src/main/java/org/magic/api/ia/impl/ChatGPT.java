package org.magic.api.ia.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.technical.MTGDocumentation;
import org.magic.api.beans.technical.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractIA;
import org.magic.services.MTGConstants;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.URLTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ChatGPT extends AbstractIA {

	
	private static final String CONTENT = "content";
	private MTGHttpClient client;
	private static final String TOKEN = "TOKEN";
	
	private JsonElement query( JsonObject obj,String endpoint) throws IOException
	{
		
		if(client==null)
			client = URLTools.newClient();
		
		if(getAuthenticator().get(TOKEN)==null)
			throw new IOException("Please fill TOKEN value in Account configuration");
		
		
		var headers = new HashMap<String, String>();
		headers.put("Authorization", "Bearer " + getAuthenticator().get(TOKEN));
		headers.put(URLTools.CONTENT_TYPE, URLTools.HEADER_JSON);
		var resp =  client.doPost("https://api.openai.com/"+getVersion()+endpoint, new StringEntity(obj.toString(), MTGConstants.DEFAULT_ENCODING), headers);
		logger.debug(resp);
		return URLTools.toJson(resp.getEntity().getContent());
		
	}
	
	@Override
	public String ask(String prompt) throws IOException
	{
		logger.debug("chat : {} ",prompt);
		var obj = new JsonObject();
					obj.addProperty("model", getString("MODEL"));
					obj.addProperty("temperature", getDouble("TEMPERATURE"));
					obj.addProperty("max_tokens", getInt("MAX_TOKEN"));
					
					var arr = new JsonArray();
					
					if(!getString("SYSTEM_MSG").isEmpty())
					{
						var sysObj = new JsonObject();
						sysObj.addProperty("role","system");
						sysObj.addProperty(CONTENT, getString("SYSTEM_MSG"));
						arr.add(sysObj);
					}
					
					var obj2 = new JsonObject();
						  obj2.addProperty(CONTENT, prompt);
						  obj2.addProperty("role", "user");
						  
						  arr.add(obj2);
					obj.add("messages", arr);
					
		var jsonReponse = query(obj,"/chat/completions");
		
		
		if(jsonReponse.getAsJsonObject().get("error")!=null)
		{
			throw new IOException(jsonReponse.getAsJsonObject().get("error").getAsJsonObject().get("message").getAsString());
		}
		
		try {
			
			var ret = jsonReponse.getAsJsonObject().get("choices").getAsJsonArray().get(0).getAsJsonObject().get("message").getAsJsonObject().get(CONTENT).getAsString();
			logger.debug("{} answer : {} ",getName(), ret);
			return ret;
		}
		catch(Exception e)
		{
			logger.error("response {}", jsonReponse.getAsJsonObject());
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
	public Map<String, MTGProperty> getDefaultAttributes() {
			var map = super.getDefaultAttributes();
			map.put("MODEL", new MTGProperty("gpt-3.5-turbo","choose langage model","gpt-4o","gpt-4o-mini", "gpt-4-turbo", "gpt-4", "gpt-3.5-turbo"));
			map.put("TEMPERATURE", MTGProperty.newIntegerProperty("0", "You can think of temperature like randomness, with 0 being least random (or most deterministic) and 2 being most random (least deterministic)", 0, 2));
			map.put("MAX_TOKEN", MTGProperty.newIntegerProperty("2000","Maximum size of the prompt",50,5000));
			return map;
	}
	
		
	@Override
	public MTGDocumentation getDocumentation() {
		return new MTGDocumentation("https://platform.openai.com/docs/models",FORMAT_NOTIFICATION.HTML);
	}
	
	
	
	@Override
	public MTGCard generateRandomCard(String description) throws IOException {
		
		var ret = ask(NEW_CARD_QUERY  +( (description==null || description.isEmpty())?"": " with this description  : "+description));
			
		if(ret==null)
			return null;
		
		ret = StringUtils.substringBetween(ret,"\u0060\u0060\u0060");
		
		var obj = URLTools.toJson(ret).getAsJsonObject();
		return parseIaCardSuggestion(obj);
	}
}
