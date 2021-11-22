package org.magic.services.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map.Entry;

import org.magic.services.MTGConstants;

public class IncapsulaParser {

	static String incapsulaCookie = null;

	private IncapsulaParser() {
	}

	private static String getIncapsulaCookie(String url) throws IOException {

		HttpURLConnection cookieConnection = (HttpURLConnection) new URL(url).openConnection();
		cookieConnection.setRequestMethod("GET");
		cookieConnection.setRequestProperty("Accept", URLTools.HEADER_HTML+"; charset="+MTGConstants.DEFAULT_ENCODING);
		cookieConnection.setRequestProperty(URLTools.USER_AGENT, MTGConstants.USER_AGENT);
		cookieConnection.setRequestProperty("Connection", "close");

		String visid = null;
		String incap = null;

		cookieConnection.connect();

		for (Entry<String, List<String>> header : cookieConnection.getHeaderFields().entrySet()) {
			if (header.getKey() != null && header.getKey().equals("Set-Cookie")) {
				for (String cookieValue : header.getValue()) {
					if (cookieValue.contains("visid")) {
						visid = cookieValue.substring(0, cookieValue.indexOf(';') + 1);
					}
					if (cookieValue.contains("incap_ses")) {
						incap = cookieValue.substring(0, cookieValue.indexOf(';'));
					}
				}
			}
		}
		incapsulaCookie = visid + " " + incap;
		cookieConnection.disconnect();
		return incapsulaCookie;

	}

	public static String readUrl(String url) throws IOException {

		var response = new StringBuilder();
		BufferedReader in = null;

		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty(URLTools.ACCEPT, "text/html; charset="+MTGConstants.DEFAULT_ENCODING);
		connection.setRequestProperty(URLTools.USER_AGENT, MTGConstants.USER_AGENT);
		connection.setRequestProperty("Cookie", getIncapsulaCookie(url));

		in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		var inputLine = "";
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}

		in.close();

		return response.toString();

	}

}
