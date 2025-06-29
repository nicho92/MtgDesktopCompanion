package org.magic.api.ia.impl;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractIA;
import org.magic.services.network.URLTools;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.exceptions.ToolInvocationException;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder;
import io.github.ollama4j.models.chat.OllamaChatResult;

public class Ollama extends AbstractIA{

	 OllamaAPI ollamaAPI ;
	
	
	 private void init()
	 {
		 if(ollamaAPI==null)
		 {
			 	ollamaAPI = new OllamaAPI(getString("URL"));
				ollamaAPI.setVerbose(getBoolean("VERBOSE"));
				ollamaAPI.setRequestTimeoutSeconds(1000000);
		 }
	 }
	 
	 
	@Override
	public String ask(String prompt) throws IOException {
        init();
        				
        var builder = OllamaChatRequestBuilder.getInstance(getString("MODEL"));
        var requestModel = builder.withMessage(OllamaChatMessageRole.USER,prompt)
        										.withMessage(OllamaChatMessageRole.SYSTEM,getString("SYSTEM_MSG"))
        										.build();

        OllamaChatResult chatResult;
		try {
			chatResult = ollamaAPI.chat(requestModel);
		} catch (OllamaBaseException | IOException | InterruptedException | ToolInvocationException e) {
			throw new IOException(e);
		}

		var ret =  chatResult.getResponseModel().getMessage().getContent();
		
		
		logger.info("response : {}",ret);
		
		return ret;
	}

	@Override
	public MTGCard generateRandomCard(String description) throws IOException {
        init();
        
	var ret = ask(NEW_CARD_QUERY  +( (description==null || description.isEmpty())?"": " with this description  : "+description));
		
	if(ret==null)
		return null;
	
	ret = StringUtils.substringBetween(ret,"\u0060\u0060\u0060");
	
	var obj = URLTools.toJson(ret).getAsJsonObject();
	return parseIaCardSuggestion(obj);
		
		
		
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var m =  super.getDefaultAttributes();
			 m.put("URL",   new MTGProperty("http://localhost:11434","accessible url for ollama endpoint"));
			 m.put("VERBOSE",   MTGProperty.newBooleanProperty("false", "Verbose Ollama output"));
			 m.put("MODEL", new MTGProperty("llama3.1", "model used for generation by Oolama", new String[] { "gemma","gemma2","llama2","llama3","llama3.1","mistral","mixtral","deepseek-r1","llava","llava-phi3","neural-chat","codellama","dolphin-mixtral","mistral-openorca","llama2-uncensored","phi","phi3","orca-mini","deepseek-coder","dolphin-mistral","vicuna","wizard-vicuna-uncensored","zephyr","openhermes","qwen","qwen2","wizardcoder","llama2-chinese","tinyllama","phind-codellama","openchat","orca2","falcon","wizard-math","tinydolphin","nous-hermes","yi","dolphin-phi","starling-lm","starcoder","codeup","medllama2","stable-code","wizardlm-uncensored","bakllava","everythinglm","solar","stable-beluga","sqlcoder","yarn-mistral","nous-hermes2-mixtral","samantha-mistral","stablelm-zephyr","meditron","wizard-vicuna","stablelm2","magicoder","yarn-llama2","nous-hermes2","deepseek-llm","llama-pro","open-orca-platypus2","codebooga","mistrallite","nexusraven","goliath","nomic-bed-text","notux","alfred","megadolphin","wizardlm","xwinlm","notus","duckdb-nsql","all-minilm","codestral"}));
		return m;
	}
	
	
	@Override
	public String getName() {
		return "Ollama";
	}
}