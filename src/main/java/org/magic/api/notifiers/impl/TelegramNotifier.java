package org.magic.api.notifiers.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.interfaces.abstracts.AbstractMTGNotifier;
import org.magic.services.network.URLTools;


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
		
		
		var urlString = "https://api.telegram.org/bot%s/sendMessage?parse_mode="+getFormat().name().toLowerCase()+"&chat_id=%s&text=%s";

		var apiToken = getAuthenticator().get("TOKEN");
		var chatId = getString("CHANNEL");
		var msg = URLTools.encode(notification.getMessage());
		
		if(msg.length()>4096)
		{
			logger.error("Message is too long : " + msg.length() + ">4096. Will truncate it");
			msg = msg.substring(0, 4096);
			
		}
		
		urlString = String.format(urlString, apiToken, chatId, msg);

		var conn = URLTools.openConnection(urlString);

		var sb = new StringBuilder();
		
		var is = new BufferedInputStream(conn.getInputStream());
		var br = new BufferedReader(new InputStreamReader(is));
		var inputLine = "";
		while ((inputLine = br.readLine()) != null) {
			sb.append(inputLine);
		}
		var response = sb.toString();
		logger.debug(response);
		br.close();

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
	public Map<String, String> getDefaultAttributes() {
		return Map.of("CHANNEL","");
	}

}
