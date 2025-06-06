package org.magic.api.ia.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractIA;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.network.URLTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class DeepSeek extends AbstractIA {

	
	private static final String SYSTEM_MSG = "SYSTEM_MSG";
	private static final String API_KEY = "API_KEY";


	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	
	public static void main(String[] args) throws IOException {
		
		MTGControler.getInstance().loadAccountsConfiguration();
		
		
		var ds = new DeepSeek();
		
		
		ds.generateRandomCard(" with theme of Cthulhu and lovecraft universe");
		
		
	}
	
	
	
	private JsonElement query( JsonObject obj,String endpoint) throws IOException
	{
		var	client = URLTools.newClient();
		
		if(getAuthenticator().get(API_KEY)==null)
			throw new IOException("Please fill API_KEY value in Account configuration");
		
		
		var headers = new HashMap<String, String>();
		headers.put("Authorization", "Bearer " + getAuthenticator().get(API_KEY));
		headers.put(URLTools.CONTENT_TYPE, URLTools.HEADER_JSON);
		var resp =  client.doPost("https://api.deepseek.com"+endpoint, new StringEntity(obj.toString(), MTGConstants.DEFAULT_ENCODING), headers);
		logger.debug(resp);
		var ret = URLTools.toJson(resp.getEntity().getContent());
		
		if(ret.getAsJsonObject().get("error")!=null)
			throw new IOException(ret.getAsJsonObject().get("error").getAsJsonObject().get("message").getAsString());
		
		
		
		return ret;
		
	}
	
	
	@Override
	public String ask(String prompt) throws IOException {

		var obj = new JsonObject();
		var arr = new JsonArray();
			obj.addProperty("model", "deepseek-chat");
			obj.addProperty("stream", false);
			obj.add("messages", arr);
			
			
			if(!getString(SYSTEM_MSG).isEmpty())
			{
				var sysObj = new JsonObject();
				sysObj.addProperty("role","system");
				sysObj.addProperty("content", getString(SYSTEM_MSG));
				arr.add(sysObj);
			}
			
			var obj2 = new JsonObject();
				  obj2.addProperty("content", prompt);
				  obj2.addProperty("role", "user");
				  
				  arr.add(obj2);
			obj.add("messages", arr);
		
		

		var query = query(obj,"/chat/completions");
		
		logger.info(query);
		
		return null;
	}

	@Override
	public MTGCard generateRandomCard(String description) throws IOException {
		
		var ret = ask(NEW_CARD_QUERY  +( (description==null || description.isEmpty())?"": " with this description  : "+description));
		
		System.out.println(ret);
		
		return null;
	}

	@Override
	public String getName() {
		return "DeepSeek";
	}

	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of(API_KEY);
	}
	
	
	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var map = super.getDefaultAttributes();
		map.put(SYSTEM_MSG, new MTGProperty("You are a helpful assistant that generate Magic the gathering card in json format.","contextual prompt for the chatbot"));
		return map;
	}
	
}
