package org.magic.api.ia.impl;

import java.io.IOException;
import java.util.Map;

import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractIA;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.exceptions.ToolInvocationException;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder;
import io.github.ollama4j.models.chat.OllamaChatResult;

public class Ollama extends AbstractIA{

	 private OllamaAPI ollamaAPI ;
	 private OllamaChatRequestBuilder builder;
	
	
	 private void init()
	 {
		 if(ollamaAPI==null)
		 {
			 	ollamaAPI = new OllamaAPI(getString("URL"));
				ollamaAPI.setVerbose(getBoolean("VERBOSE"));
				ollamaAPI.setRequestTimeoutSeconds(180);
		 }
		 builder = OllamaChatRequestBuilder.getInstance(getString("MODEL"));
	 }
	 
	 
	 public static void main(String[] args) throws IOException {
		
		 var ollama = new Ollama();
		 
		 ollama.ask("Peux tu me generer une carte Magic the gathering au format Json avec la Princesse Zelda ?");
		 
		 
	}
	 
	@Override
	public String ask(String prompt) throws IOException {
        init();
        				
        
        var requestModel = builder.withMessage(OllamaChatMessageRole.USER,prompt)
        						//				.withMessage(OllamaChatMessageRole.SYSTEM,getString("SYSTEM_MSG"))
        										.build();

        OllamaChatResult chatResult;
		try {
			chatResult = ollamaAPI.chat(requestModel);
		} catch (OllamaBaseException | IOException |  ToolInvocationException e) {
			throw new IOException(e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return "";
		}

		var ret =  chatResult.getResponseModel().getMessage().getContent();
		
		
		logger.info("response : {}",ret);
		
		return ret;
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
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	@Override
	public String getName() {
		return "Ollama";
	}
}