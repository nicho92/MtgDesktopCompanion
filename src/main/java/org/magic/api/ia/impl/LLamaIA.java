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
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.URLTools;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class LLamaIA  extends AbstractIA{

	
	private MTGHttpClient client;
	private static final String TOKEN = "TOKEN";
	
	private JsonElement query(String prompt ) throws IOException
	{
		
		if(client==null)
			client = URLTools.newClient();
		
		if(getAuthenticator().get(TOKEN)==null)
			throw new IOException("Please fill TOKEN value in Account configuration");
		
		
		var headers = new HashMap<String, String>();
		headers.put(URLTools.AUTHORIZATION, "Token " + getAuthenticator().get(TOKEN));
		headers.put(URLTools.CONTENT_TYPE, URLTools.HEADER_JSON);
		
		var obj = new JsonObject();
			  obj.addProperty("version","02e509c789964a7ea8736978a43525956ef40397be9033abf9fd2badfe68c9e3");
			  obj.addProperty("model",getString("MODEL"));
				
			  
		var input= new JsonObject();
			 input.addProperty("prompt", prompt);
			 input.addProperty("debug",false);
			 input.addProperty("top_k",50);
			 input.addProperty("top_p",1);
			 input.addProperty("temperature",getDouble("TEMPERATURE"));
			 input.addProperty("max_new_tokens",getInt("MAX_TOKEN"));
			 input.addProperty("min_new_tokens",-1);
			 input.addProperty("system_prompt",getString("SYSTEM_MSG"));
		obj.add("input", input);
		
		var resp =  client.doPost("https://api.replicate.com/v1/predictions", new StringEntity(obj.toString(), MTGConstants.DEFAULT_ENCODING), headers);
		
		
		if(resp.getStatusLine().getStatusCode()!=200)
			throw new IOException(resp.getStatusLine().getReasonPhrase());
		
		var ret = URLTools.toJson(resp.getEntity().getContent());
		var getUrl = ret.getAsJsonObject().get("urls").getAsJsonObject().get("get").getAsString();
		ret= URLTools.toJson(client.doGet(getUrl, Map.of(URLTools.AUTHORIZATION, "Token " + getAuthenticator().get(TOKEN)), null).getEntity().getContent());
		
		while(!ret.getAsJsonObject().get("status").getAsString().equalsIgnoreCase("Succeeded"))
		{
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new IOException(e);
			}
			ret= URLTools.toJson(client.doGet(getUrl, Map.of(URLTools.AUTHORIZATION, "Token " + getAuthenticator().get(TOKEN)), null).getEntity().getContent());
			logger.debug(ret.getAsJsonObject().get("status").getAsString());
		}
		
		return ret;
		
	}
	
	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of(TOKEN);
	}
	
	
	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
			var map = super.getDefaultAttributes();
			map.put("MODEL", new MTGProperty("meta/llama-2-70b-chat","choose langage model"));
			map.put("TEMPERATURE", MTGProperty.newIntegerProperty("0", "You can think of temperature like randomness, with 0 being least random (or most deterministic) and 2 being most random (least deterministic)", 0, 2));
			map.put("MAX_TOKEN", MTGProperty.newIntegerProperty("2000","Maximum size of the prompt",50,5000));
			map.put("SYSTEM_MSG", new MTGProperty("You are a helpful assistant that generate Magic the gathering card in json format.","contextual prompt for the chatbot"));

			return map;
	}
	
	

	
	
	
	@Override
	public String ask(String prompt) throws IOException {
		
		var arr = query(prompt).getAsJsonArray();
		
		logger.info(arr);
		
		
		
		return "";
		
	}

	@Override
	public MTGCard generateRandomCard(String prompt) throws IOException {
		
		query(prompt).getAsJsonArray();
		
		
		
		return null;
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
