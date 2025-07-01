package org.magic.api.ia.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractIA;
import org.magic.services.MTGConstants;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.URLTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class LLamaIA  extends AbstractIA{

	
	private static final String CONTENT = "content";
	private static final String TEMPERATURE = "TEMPERATURE";
	private static final String MAX_TOKEN = "MAX_TOKEN";
	private MTGHttpClient client;
	private static final String TOKEN = "TOKEN";
	
	private JsonElement query(String prompt ) throws IOException
	{
		
		if(client==null)
			client = URLTools.newClient();
		
		if(getAuthenticator().get(TOKEN)==null)
			throw new IOException("Please fill TOKEN value in Account configuration");
		
		
		var headers = new HashMap<String, String>();
		headers.put(URLTools.AUTHORIZATION, "Bearer " + getAuthenticator().get(TOKEN));
		headers.put(URLTools.CONTENT_TYPE, URLTools.HEADER_JSON);
		
	
		var obj = new JsonObject();
		var msgs = new JsonArray();
		
		
		obj.addProperty("model",getString("MODEL"));
		
		
		var sysMsg = new JsonObject();
				sysMsg.addProperty("role", "system");
				sysMsg.addProperty(CONTENT, getString("SYSTEM_MSG"));

		
		var userMsg = new JsonObject();
				userMsg.addProperty("role", "user");
				userMsg.addProperty(CONTENT, prompt);
		
		msgs.add(sysMsg);
		msgs.add(userMsg);
		obj.add("messages", msgs);
		
		
		obj.addProperty("stream", false);
		
		if(!getString(MAX_TOKEN).isEmpty())
			obj.addProperty(MAX_TOKEN.toLowerCase(),getInt(MAX_TOKEN));
		
		if(!getString(TEMPERATURE).isEmpty())
			obj.addProperty(TEMPERATURE,getDouble(TEMPERATURE));
		
		
		logger.debug("Ask = {}", msgs);
		
		var resp =  client.doPost("https://api.llama-api.com/chat/completions", new StringEntity(obj.toString(), MTGConstants.DEFAULT_ENCODING), headers);
		
		if(resp.getStatusLine().getStatusCode()!=200)
			throw new IOException(resp.getStatusLine().getReasonPhrase());
		
		var ret = URLTools.toJson(resp.getEntity().getContent());
		
		logger.debug("response = {}", ret);
		
		return ret;
	}
	
	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of(TOKEN);
	}
	
	
	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
			var map = super.getDefaultAttributes();
			map.put("MODEL", new MTGProperty("llama3.1-70b","choose langage model"));
			map.put(TEMPERATURE, MTGProperty.newIntegerProperty("0", "You can think of temperature like randomness, with 0 being least random (or most deterministic) and 2 being most random (least deterministic)", 0, 2));
			map.put(MAX_TOKEN, MTGProperty.newIntegerProperty("2000","Maximum size of the prompt",50,5000));
			return map;
	}
	
	

	
	
	
	@Override
	public String ask(String prompt) throws IOException {
		return query(prompt).getAsJsonObject().get("choices").getAsJsonArray()
																  .get(0).getAsJsonObject()
																  .get("message").getAsJsonObject()
																  .get(CONTENT).getAsString();
	}

	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	

	@Override
	public String getName() {
		return "LLama";
	}

}
