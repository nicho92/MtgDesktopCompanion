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
import org.magic.api.beans.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMTGNotifier;
import org.magic.services.MTGConstants;

public class TelegramNotifier extends AbstractMTGNotifier {

	@Override
	public FORMAT_NOTIFICATION getFormat() {
		return FORMAT_NOTIFICATION.MARKDOWN;
	}
	
	@Override
	public void send(MTGNotification notification) throws IOException {
		
		
		String urlString = "https://api.telegram.org/bot%s/sendMessage?parse_mode="+getFormat().name().toLowerCase()+"&chat_id=%s&text=%s";

		String apiToken = getString("TOKEN");
		String chatId = getString("CHANNEL");
		String msg = URLEncoder.encode(notification.getMessage(),MTGConstants.DEFAULT_ENCODING);
		
		if(msg.length()>4096)
		{
			msg = msg.substring(0, 4096);
			logger.error("Message is too long : " + msg.length() + ">4096. Will truncate it");
		}
		
		urlString = String.format(urlString, apiToken, chatId, msg);

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


}
