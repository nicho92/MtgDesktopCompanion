package org.magic.tools;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;

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
	
}
