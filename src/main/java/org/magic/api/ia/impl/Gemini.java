package org.magic.api.ia.impl;

import java.io.IOException;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.magic.api.interfaces.abstracts.AbstractIA;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.URLTools;

public class Gemini extends AbstractIA {
	
	
	private MTGHttpClient client;

	public Gemini() {
		client = URLTools.newClient();
	}
		
	@Override
	public String ask(String prompt) throws IOException {
		
		var k = getAuthenticator().get("API_KEY");
		var m = client.buildMap().put(URLTools.CONTENT_TYPE, URLTools.HEADER_JSON).put("X-goog-api-key", k).build();
		
		var obj = """
				{
				    "contents": [
				      {
				        "parts": [
				          {
				            "text": "PROMPT"
				          }
				        ]
				      }
				    ]
				  }
				""";
		
		
		obj = obj.replace("PROMPT", prompt);
		
		
		var resp = client.doPost("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent", new StringEntity(obj.toString(), MTGConstants.DEFAULT_ENCODING), m);
		var ret= URLTools.toJson(resp.getEntity().getContent());
		
		logger.info("return {}", ret);
		
		return ret.getAsJsonObject()
								.get("candidates").getAsJsonArray()
								.get(0).getAsJsonObject()
								.get("content").getAsJsonObject()
								.get("parts").getAsJsonArray()
								.get(0).getAsJsonObject()
								.get("text").getAsString().replace("json", "");	


	}

	@Override
	public String getName() {
		return "Gemini";
	}
	
	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of("API_KEY");
	}
	

}
