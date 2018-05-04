package org.magic.api.notifiers.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.magic.api.beans.MTGNotification;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMTGNotifier;

public class TelegramNotifier extends AbstractMTGNotifier {

	@Override
	public void send(MTGNotification notification) throws IOException {
		String urlString = "https://api.telegram.org/bot%s/sendMessage?parse_mode=html&chat_id=%s&text=%s";

		String apiToken = getString("TOKEN");
		String chatId = getString("CHANNEL");
		urlString = String.format(urlString, apiToken, chatId, URLEncoder.encode(notification.getMessage(),"UTF-8"));

		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();

		StringBuilder sb = new StringBuilder();
		
		InputStream is = new BufferedInputStream(conn.getInputStream());
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String inputLine = "";
		while ((inputLine = br.readLine()) != null) {
			sb.append(inputLine);
		}
		String response = sb.toString();
		logger.debug(response);
		

	}

	@Override
	public String getName() {
		return "Telegram";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public void initDefault() {
		setProperty("TOKEN", "");
		setProperty("CHANNEL","");

	}

	@Override
	public String getVersion() {
		return "1.0";
	}

}
