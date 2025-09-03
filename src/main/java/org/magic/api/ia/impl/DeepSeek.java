package org.magic.api.ia.impl;

import java.util.List;

import org.magic.api.interfaces.abstracts.AbstractIA;
import org.magic.services.tools.POMReader;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class DeepSeek extends AbstractIA {

	private static final String API_KEY = "API_KEY";

	@Override
	public String getVersion() {
		return POMReader.readVersionFromPom(OpenAiChatModel.class, "/META-INF/maven/dev.langchain4j/langchain4j-open-ai/pom.properties");
	}

	@Override
	public ChatModel getEngine(ResponseFormat format) {
		var b=  OpenAiChatModel.builder() 
				 .apiKey(getAuthenticator().get(API_KEY))
				 .baseUrl("https://api.deepseek.com")
				 .modelName("deepseek-chat")
				 .maxTokens(getInt("MAX_TOKEN"))
				 .logRequests(getBoolean("LOG"))
				 .logResponses(getBoolean("LOG"))
				 .temperature(getDouble("TEMPERATURE"));
		
				if(format!=null)
					b.responseFormat(format.toString());
			        
				 
				 
				 
				 return b.build();
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
