package org.magic.api.ia.impl;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ResponseFormat;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractIA;
import org.magic.services.tools.POMReader;

public class ClaudeAI extends AbstractIA {

	@Override
	public String getName() {
		return "ClaudeIA";
	}

	@Override
	public ChatModel getEngine(ResponseFormat format) {
		var b = AnthropicChatModel.builder().apiKey(getAuthenticator().get("API_KEY"))
				.modelName(AnthropicChatModelName.valueOf(getString("MODEL"))).logRequests(getBoolean("LOG"))
				.logResponses(getBoolean("LOG")).temperature(getDouble("TEMPERATURE"));

		if (format != null)
			b.responseFormat(format);

		return b.build();
	}

	@Override
	public String getVersion() {
		return POMReader.readVersionFromPom(AnthropicChatModel.class,
				"/META-INF/maven/dev.langchain4j/langchain4j-anthropic/pom.properties");
	}

	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of("API_KEY");
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var map = super.getDefaultAttributes();
		map.put("MODEL", new MTGProperty(AnthropicChatModelName.CLAUDE_SONNET_4_5_20250929.name(),
				"choose langage model",
				Arrays.stream(AnthropicChatModelName.values()).map(Enum::name).toList().toArray(new String[0])));
		return map;
	}

	@Override
	public Color getChatColor() {
		return new Color(128, 55, 10);
	}

}
