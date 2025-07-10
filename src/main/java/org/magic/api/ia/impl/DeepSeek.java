package org.magic.api.ia.impl;

import java.util.List;
import java.util.Map;

import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractIA;
import org.magic.services.tools.POMReader;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class DeepSeek extends AbstractIA {

	private static final String API_KEY = "API_KEY";

	@Override
	public String getVersion() {
		return POMReader.readVersionFromPom(OpenAiChatModel.class, "/META-INF/maven/dev.langchain4j/langchain4j-open-ai/pom.properties");
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
			var map = super.getDefaultAttributes();
			map.put("TEMPERATURE", MTGProperty.newIntegerProperty("0.7", "You can think of temperature like randomness, with low value is being least random (or most deterministic) and max being most random (least deterministic)", 0, 1));
			map.put("MAX_TOKEN", MTGProperty.newIntegerProperty("2000","Maximum size of the prompt",50,5000));
			map.put("LOG", MTGProperty.newBooleanProperty(FALSE, "enable chat model logger"));
			return map;
	}

	@Override
	public ChatModel getCardGeneratorEngine() {
		return OpenAiChatModel.builder() 
				 .apiKey(getAuthenticator().get(API_KEY))
				 .baseUrl("https://api.deepseek.com")
				 .modelName("deepseek-chat")
				 .responseFormat(getFormat().toString())
				 .maxTokens(getInt("MAX_TOKEN"))
				 .logRequests(getBoolean("LOG"))
				 .logResponses(getBoolean("LOG"))
				 .temperature(getDouble("TEMPERATURE"))
	        .build();
	}
	
	@Override
	public ChatModel getStandardEngine() {
		return OpenAiChatModel.builder() 
				 .apiKey(getAuthenticator().get(API_KEY))
				 .baseUrl("https://api.deepseek.com")
				 .modelName("deepseek-chat")
				 .maxTokens(getInt("MAX_TOKEN"))
				 .logRequests(getBoolean("LOG"))
				 .logResponses(getBoolean("LOG"))
				 .temperature(getDouble("TEMPERATURE"))
	        .build();
	}

	@Override
	public String getName() {
		return "DeepSeek";
	}

	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of(API_KEY);
	}
	

	
}
