package org.magic.api.ia.impl;

import java.util.List;
import java.util.Map;

import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractIA;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;

public class Gemini extends AbstractIA {


	@Override
	public String getName() {
		return "Gemini";
	}
	
	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of("API_KEY");
	}

	@Override
	public ChatModel getEngine(ResponseFormat format) {
		var b= GoogleAiGeminiChatModel.builder() 
				 .apiKey(getAuthenticator().get("API_KEY"))
				 .modelName(getString("MODEL"))
				 .logRequestsAndResponses(getBoolean("LOG"))
				 .temperature(getDouble("TEMPERATURE"));
		

		if(format!=null)
			b.responseFormat(format);
		
		
	     return b.build();
	}
	
	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
			var map = super.getDefaultAttributes();
			map.put("MODEL", new MTGProperty("gemini-2.5-flash","choose langage model","gemini-2.5-flash","gemini-2.0-flash","gemini-1.5-flash","gemini-1.5-pro","gemini-1.0-pro"));
			return map;
	}


}
