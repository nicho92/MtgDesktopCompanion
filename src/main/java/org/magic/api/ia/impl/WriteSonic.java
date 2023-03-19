package org.magic.api.ia.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.abstracts.AbstractIA;
import org.magic.services.MTGConstants;
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
				headers.put("X-API-KEY", getAuthenticator().get("X-API-KEY"));

		
		var obj = new JsonObject();
				 obj.addProperty("enable_google_results", getBoolean("GOOGLE_RESULTS"));
				 obj.addProperty("enable_memory", getBoolean("ENABLE_MEMORY"));
				 obj.addProperty("input_text", prompt);
		
		var resp = client.doPost("https://api.writesonic.com/v2/business/content/chatsonic?engine=premium", new StringEntity(obj.toString(), MTGConstants.DEFAULT_ENCODING), headers);
		return URLTools.toJson(resp.getEntity().getContent()).getAsJsonObject();
	}
	
	

	@Override
	public String getName() {
		return "ChatSonic";
	}


	@Override
	public MagicCard generateRandomCard(String desc) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}


}
