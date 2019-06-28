package org.magic.api.notifiers.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;

import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.interfaces.abstracts.AbstractMTGNotifier;
import org.magic.tools.URLTools;


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
		String msg = URLTools.encode(notification.getMessage());
		
		if(msg.length()>4096)
		{
			logger.error("Message is too long : " + msg.length() + ">4096. Will truncate it");
			msg = msg.substring(0, 4096);
			
		}
		
		urlString = String.format(urlString, apiToken, chatId, msg);

		URLConnection conn = URLTools.openConnection(urlString);

		StringBuilder sb = new StringBuilder();
		
		InputStream is = new BufferedInputStream(conn.getInputStream());
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String inputLine = "";
		while ((inputLine = br.readLine()) != null) {
			sb.append(inputLine);
		}
		String response = sb.toString();
		logger.debug(response);
		br.close();

	}

	@Override
	public String getName() {
		return "Telegram";
	}


	@Override
	public void initDefault() {
		setProperty("TOKEN", "");
		setProperty("CHANNEL","");
	}

}
