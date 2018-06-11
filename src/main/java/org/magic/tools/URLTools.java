package org.magic.tools;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class URLTools {

	static Logger logger = MTGLogger.getLogger(URLTools.class);

	private URLTools() 
	{}
	
	public static HttpURLConnection openConnection(String url) throws IOException {
		return openConnection(new URL(url));
	}
	
	public static HttpURLConnection openConnection(URL url) throws IOException {
		logger.trace("get stream from " + url);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("User-Agent", MTGConstants.USER_AGENT);
		connection.setInstanceFollowRedirects(true);
		connection.connect();
		return connection;
	}
	
	public static Document extractHtml(String url) throws IOException
	{
		logger.trace("get html from " + url);
		return Jsoup.connect(url)
			 .userAgent(MTGConstants.USER_AGENT)
			 .timeout(0)
			 .get();
	}
	
	public static JsonElement extractJson(String url) throws IOException
	{
		logger.trace("get json from " + url);
		JsonReader reader = new JsonReader(new InputStreamReader(openConnection(url).getInputStream()));
		return new JsonParser().parse(reader);
	}
	
	
	
	
	
}
