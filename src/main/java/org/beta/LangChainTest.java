package org.beta;

import java.util.List;

import org.magic.api.beans.enums.EnumRarity;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.ResponseFormatType;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchema;
import dev.langchain4j.model.mistralai.MistralAiChatModel;
import dev.langchain4j.model.mistralai.MistralAiChatModelName;

public class LangChainTest {
	
	private ResponseFormat getFormat()
	{
		return ResponseFormat.builder()
					        .type(ResponseFormatType.JSON)
					        .jsonSchema(JsonSchema.builder()
					                .name("MTG Card")
					                .rootElement(JsonObjectSchema.builder()
					                        .addStringProperty("name")
					                        .addStringProperty("types")
					                        .addStringProperty("cost")
					                        .addIntegerProperty("cmc")
					                        .addEnumProperty("rarity",List.of(EnumRarity.values()).stream().map(en->en.getName()).toList())
					                        .addStringProperty("flavor")
					                        .addStringProperty("text")
					                        .addIntegerProperty("cmc")
					                        .addIntegerProperty("power")
					                        .addIntegerProperty("toughtness")
					                        .required("name", "types", "cost", "text","flavor","rarity")
					                        .build())
					                .build())
					        .build();
	}
	
	public ChatModel getChatEngine()
	{
		var chat = MistralAiChatModel.builder() 
				 .apiKey("")
				 .modelName(MistralAiChatModelName.MISTRAL_SMALL_LATEST)
				 .responseFormat(getFormat())
				 .logRequests(true)
				 .logResponses(true)
				 .temperature(0.7)
				 
	        .build();
		
		
				
		return chat;
	}
	
	public static void main(String[] args) {
	    //var chat = GoogleAiGeminiChatModel.builder()  
       // .apiKey("")
       // .modelName("gemini-1.5-flash")
	
		var test = new LangChainTest();
	
		System.out.println(test.getChatEngine().chat("a creature from lovecraft universe"));
	}

}
