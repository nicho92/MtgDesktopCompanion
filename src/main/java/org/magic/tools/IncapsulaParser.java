package org.magic.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map.Entry;

import org.magic.services.MTGConstants;

public class IncapsulaParser {

	static String incapsulaCookie = null;

	private IncapsulaParser() {
	}

	private static String getIncapsulaCookie(String url) throws IOException {

		if (incapsulaCookie != null)
			return incapsulaCookie;

		HttpURLConnection cookieConnection = URLTools.openConnection(url);
		cookieConnection.setRequestMethod("GET");
		cookieConnection.setRequestProperty("Accept", "text/html; charset="+MTGConstants.DEFAULT_ENCODING);
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

		StringBuilder response = new StringBuilder();
		BufferedReader in = null;

		HttpURLConnection connection = URLTools.openConnection(url);
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "text/html; charset="+MTGConstants.DEFAULT_ENCODING);
		connection.setRequestProperty("Cookie", getIncapsulaCookie(url));

		in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		String inputLine = "";
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}

		in.close();

		return response.toString();

	}

}
