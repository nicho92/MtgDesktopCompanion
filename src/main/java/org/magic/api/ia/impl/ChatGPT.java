package org.magic.api.ia.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractIA;
import org.magic.services.tools.POMReader;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;

public class ChatGPT extends AbstractIA {
	private static final String TOKEN = "TOKEN";
	
	@Override
	public String getName() {
		return "ChatGPT";
	}
	
	@Override
	public String getVersion() {
		return POMReader.readVersionFromPom(OpenAiChatModel.class, "/META-INF/maven/dev.langchain4j/langchain4j-open-ai/pom.properties");
	}
	
	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of(TOKEN);
	}
	
	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
			var map = super.getDefaultAttributes();
			
			map.put("MODEL", new MTGProperty(OpenAiChatModelName.GPT_3_5_TURBO.name(),"choose langage model",Arrays.stream(OpenAiChatModelName.values()).map(e->e.name()).toList().toArray(new String[0])));
			map.put("TEMPERATURE", MTGProperty.newIntegerProperty("0.7", "You can think of temperature like randomness, with low value is being least random (or most deterministic) and max being most random (least deterministic)", 0, 1));
			map.put("MAX_TOKEN", MTGProperty.newIntegerProperty("2000","Maximum size of the prompt",50,5000));
			map.put("LOG", MTGProperty.newBooleanProperty(FALSE, "enable chat model logger"));
			return map;
	}

	@Override
	public ChatModel getEngine(ResponseFormat format) {
		var b = OpenAiChatModel.builder() 
				 .apiKey(getAuthenticator().get(TOKEN))
				 .modelName(OpenAiChatModelName.valueOf(getString("MODEL")))
				 .maxTokens(getInt("MAX_TOKEN"))
				 .logRequests(getBoolean("LOG"))
				 .logResponses(getBoolean("LOG"))
				 .temperature(getDouble("TEMPERATURE"));
	
				if(format!=null)
					b.responseFormat(format.toString());
			
				 return b.build();
	}
	

}
