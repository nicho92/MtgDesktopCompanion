package org.magic.api.notifiers.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.magic.api.beans.technical.MTGNotification;
import org.magic.api.beans.technical.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractMTGNotifier;
import org.magic.services.tools.POMReader;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class TelegramNotifier extends AbstractMTGNotifier {

	@Override
	public FORMAT_NOTIFICATION getFormat() {
		return FORMAT_NOTIFICATION.MARKDOWN;
	}

	@Override
	public boolean isExternal() {
		return true;
	}

	@Override
	public void send(MTGNotification notification) throws IOException {

		var message = SendMessage.builder().chatId(getString("CHANNEL")).text(notification.getMessage()).build();

		try {
			new OkHttpTelegramClient(getAuthenticator().get("TOKEN")).execute(message);
		} catch (TelegramApiException e) {
			logger.error(e);
		}

	}

	@Override
	public String getName() {
		return "Telegram";
	}

	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of("TOKEN");

	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("CHANNEL", new MTGProperty("",
				"ID of the channel where notification is send. See https://neliosoftware.com/content/help/how-do-i-get-the-channel-id-in-telegram/ to get this information"));
	}

	@Override
	public String getVersion() {
		return POMReader.readVersionFromPom(TelegramClient.class,
				"/META-INF/maven/org.telegram/telegrambots-meta/pom.properties");
	}

}
