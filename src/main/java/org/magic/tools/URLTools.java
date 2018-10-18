package org.magic.tools;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLHandshakeException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class URLTools {

	private static Logger logger = MTGLogger.getLogger(URLTools.class);
	
	public static final String HEADER_JSON="application/json";
	
	

	private URLTools() 
	{}
	
	public static HttpURLConnection openConnection(String url) throws IOException {
		return openConnection(new URL(url));
	}
	
	public static HttpURLConnection getConnection(String url) throws IOException {
		return getConnection(new URL(url));
	}
	
	public static HttpURLConnection getConnection(URL url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		try{
			
			connection.setRequestProperty("User-Agent", MTGConstants.USER_AGENT);
			connection.setInstanceFollowRedirects(true);
			logger.trace("get stream from " + url + " " + connection.getResponseCode());
			return connection;
		}
		catch(SSLHandshakeException e)
		{
			logger.error(url,e);
			return connection;
		}
	}
	
	
	public static HttpURLConnection openConnection(URL url) throws IOException {
		HttpURLConnection con = getConnection(url);
		con.connect();
		return con;
	}
	
	public static Document toHtml(String s)
	{
		return Jsoup.parse(s);
	}
	
	public static JsonElement toJson(String s)
	{
		return new JsonParser().parse(s);
	}
	
	
	public static Document extractHtml(URL url) throws IOException
	{
		return toHtml(extractAsString(url));
	}
	
	
	public static Document extractHtml(String url) throws IOException
	{
		return extractHtml(new URL(url));
	}
	
	public static JsonElement extractJson(String url) throws IOException
	{
		JsonReader reader = new JsonReader(new InputStreamReader(openConnection(url).getInputStream()));
		return new JsonParser().parse(reader);
	}
	
	public static String extractAsString(String url) throws IOException
	{
		return extractAsString(new URL(url)); 
	}
	
	public static String extractAsString(URL url) throws IOException
	{
		return IOUtils.toString(openConnection(url).getInputStream(), MTGConstants.DEFAULT_ENCODING); 
	}

	public static BufferedImage extractImage(String url) throws IOException
	{
		return extractImage(new URL(url));
	}
	
	public static BufferedImage extractImage(URL url) throws IOException
	{
		return ImageIO.read(openConnection(url).getInputStream()); 
		//return ImageIO.read(url);
	}

	public static boolean isCorrectConnection(HttpURLConnection connection) {
			try {
				return (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300);
			} catch (IOException e) {
				logger.error(e);
				return false;
			}
	}
	
	
}
