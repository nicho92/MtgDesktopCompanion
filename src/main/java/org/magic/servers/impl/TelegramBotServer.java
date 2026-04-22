package org.magic.servers.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.services.MTGConstants;
import org.magic.services.tools.MTG;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class TelegramBotServer extends AbstractMTGServer implements LongPollingSingleThreadUpdateConsumer {

	private TelegramClient telegramClient;
	private TelegramBotsLongPollingApplication pool;
	private static final String REGEX = "\\{(.*?)\\}";
	private Pattern p = Pattern.compile(REGEX);

	@Override
	public void start() throws IOException {
		init();

		try {
			if (pool == null) {
				pool = new TelegramBotsLongPollingApplication();
				pool.registerBot(getAuthenticator().get("TOKEN"), this);
			} else
				pool.start();

			logger.info("{} is started", getName());

		} catch (TelegramApiException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void consume(Update update) {

		logger.debug("read {}", update);

		var message = update.getMessage();

		if (!update.hasMessage())
			message = update.getChannelPost();

		if (message.hasText())
			response(message);

	}

	private void response(Message message) {
		var m = p.matcher(message.getText());
		if (m.find()) {

			try {
				var ret = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(m.group(1), null, true);

				if (!ret.isEmpty()) {

					var card = ret.get(0);

					var msgResponse = SendPhoto.builder().chatId(message.getChatId())
							.photo(new InputFile(card.getUrl())).caption(card.getName() + " " + card.getEdition())
							.build();

					telegramClient.execute(msgResponse);

				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	private void init() {
		if (telegramClient == null)
			telegramClient = new OkHttpTelegramClient(getAuthenticator().get("TOKEN"));
	}

	@Override
	public void stop() throws IOException {
		try {
			if (pool != null)
				pool.stop();

		} catch (TelegramApiException e) {
			throw new IOException(e);
		}

	}

	@Override
	public boolean isAutostart() {
		return getBoolean("AUTOSTART");
	}

	@Override
	public String description() {
		return "Query your  " + MTGConstants.MTG_APP_NAME + "  via Telegram Bot ";
	}

	@Override
	public String getName() {
		return "Telegram";
	}

	@Override
	public boolean isAlive() {

		if (pool != null)
			return pool.isRunning();
		else
			return false;
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var m = new HashMap<String, MTGProperty>();
		m.put("AUTOSTART", MTGProperty.newBooleanProperty(FALSE, "Run bot at startup"));
		return m;
	}

}
