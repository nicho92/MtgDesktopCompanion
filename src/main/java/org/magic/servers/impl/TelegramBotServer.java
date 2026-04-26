package org.magic.servers.impl;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.beans.technical.audit.MessageInfo;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.api.interfaces.abstracts.AbstractTechnicalServiceManager;
import org.magic.services.MTGConstants;
import org.magic.services.tools.MTG;
import org.magic.services.tools.POMReader;
import org.telegram.telegrambots.abilitybots.api.util.AbilityExtension;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class TelegramBotServer extends AbstractMTGServer
		implements
			LongPollingSingleThreadUpdateConsumer,
			AbilityExtension {

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
			} else {
				pool.start();
			}

			logger.info("{}Bot is started", getName());

		} catch (TelegramApiException e) {
			throw new IOException(e);
		}
	}

	public static JsonObject parse(User author) {
		var user = new JsonObject();

		if (author != null) {
			user.addProperty("id", author.getId());
			user.addProperty("name", author.getUserName());
		} else {
			user.addProperty("id", "");
			user.addProperty("name", "");

		}

		user.addProperty("avatar", "");
		return user;
	}

	private JsonObject parse(Chat c) {
		var channel = new JsonObject();

		channel.addProperty("name", c.getTitle() == null ? c.getUserName() : c.getTitle());
		channel.addProperty("id", c.getId());
		channel.addProperty("type", c.getType());
		return channel;
	}

	@Override
	public void consume(Update update) {

		logger.debug("read {}", update);
		var message = update.getMessage(); // return null if message is send to a Channel.

		if (message == null)
			message = update.getChannelPost();

		if (message != null && message.hasText()) {

			var info = new MessageInfo();
			info.setSource(getName());
			info.setUser(parse(message.getFrom()));
			info.setChannel(parse(message.getChat()));
			info.setMessage(message.getText());

			response(message, info);

		}

	}

	private void response(Message message, MessageInfo info) {
		var m = p.matcher(message.getText());

		if (m.find())
			sendCard(message, m.group(1));

		info.setEnd(Instant.now());
		AbstractTechnicalServiceManager.inst().store(info);

	}

	private void sendCard(Message message, String cardName) {
		try {
			var ret = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(cardName, null, true);
			if (!ret.isEmpty()) {
				var card = ret.get(0);
				var msgResponse = SendPhoto.builder().chatId(message.getChatId()).photo(new InputFile(card.getUrl()))
						.caption(card.getName() + "/" + card.getEdition()).build();
				telegramClient.execute(msgResponse);
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Override
	public String getVersion() {
		return POMReader.readVersionFromPom(TelegramClient.class,
				"/META-INF/maven/org.telegram/telegrambots-meta/pom.properties");
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
