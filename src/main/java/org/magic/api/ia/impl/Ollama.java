package org.magic.api.ia.impl;

import java.util.Map;

import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractIA;
import org.magic.services.tools.POMReader;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.ollama.OllamaChatModel;

public class Ollama extends AbstractIA{

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var m =  super.getDefaultAttributes();
			 m.put("URL",   new MTGProperty("http://localhost:11434","accessible url for ollama endpoint"));
			 m.put("MODEL", new MTGProperty("llama3.2", "model used for generation by Oolama", new String[] { "gemma","gemma2","llama2","llama3","llama3.1","mistral","mixtral","deepseek-r1","llava","llava-phi3","neural-chat","codellama","dolphin-mixtral","mistral-openorca","llama2-uncensored","phi","phi3","orca-mini","deepseek-coder","dolphin-mistral","vicuna","wizard-vicuna-uncensored","zephyr","openhermes","qwen","qwen2","wizardcoder","llama2-chinese","tinyllama","phind-codellama","openchat","orca2","falcon","wizard-math","tinydolphin","nous-hermes","yi","dolphin-phi","starling-lm","starcoder","codeup","medllama2","stable-code","wizardlm-uncensored","bakllava","everythinglm","solar","stable-beluga","sqlcoder","yarn-mistral","nous-hermes2-mixtral","samantha-mistral","stablelm-zephyr","meditron","wizard-vicuna","stablelm2","magicoder","yarn-llama2","nous-hermes2","deepseek-llm","llama-pro","open-orca-platypus2","codebooga","mistrallite","nexusraven","goliath","nomic-bed-text","notux","alfred","megadolphin","wizardlm","xwinlm","notus","duckdb-nsql","all-minilm","codestral"}));
			 m.put("LOG", MTGProperty.newBooleanProperty(FALSE, "enable chat model logger"));
		return m;
	}

	@Override
	public String getVersion() {
		return POMReader.readVersionFromPom(OllamaChatModel.class, "/META-INF/maven/dev.langchain4j/langchain4j-ollama/pom.properties");
	}
	
	@Override
	public String getName() {
		return "Ollama";
	}

	@Override
	public ChatModel getEngine(ResponseFormat format) {
		var b= OllamaChatModel.builder()
				.baseUrl(getString("URL"))
				 .modelName(getString("MODEL"))
				 .logRequests(getBoolean("LOG"))
				 .logResponses(getBoolean("LOG"))
				 .temperature(getDouble("TEMPERATURE"));
	        

		if(format!=null)
			b.responseFormat(format);
		
		return b.build();
	}
	
}