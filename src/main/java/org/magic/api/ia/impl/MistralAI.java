package org.magic.api.ia.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractIA;
import org.magic.services.tools.POMReader;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.mistralai.MistralAiChatModel;
import dev.langchain4j.model.mistralai.MistralAiChatModelName;

public class MistralAI extends AbstractIA {

	@Override
	public String getName() {
		return "Mistral";
	}
	
	
	

	@Override
	public ChatModel getEngine(ResponseFormat format) {
		var b= MistralAiChatModel.builder() 
				 .apiKey(getAuthenticator().get("API_KEY"))
				 .modelName(MistralAiChatModelName.valueOf(getString("MODEL")))
				 .logRequests(getBoolean("LOG"))
				 .logResponses(getBoolean("LOG"))
				 .temperature(getDouble("TEMPERATURE"));
		

		if(format!=null)
			b.responseFormat(format);
		
				 
	        return b.build();
	}
		
	@Override
	public String getVersion() {
		return POMReader.readVersionFromPom(MistralAiChatModel.class, "/META-INF/maven/dev.langchain4j/langchain4j-mistral-ai/pom.properties");
	}
	
	
	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of("API_KEY");
	}
	

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
			var map = super.getDefaultAttributes();
			map.put("MODEL", new MTGProperty(MistralAiChatModelName.MISTRAL_SMALL_LATEST.name(),"choose langage model",Arrays.stream(MistralAiChatModelName.values()).map(Enum::name).toList().toArray(new String[0])));
			return map;
	}
	
}
