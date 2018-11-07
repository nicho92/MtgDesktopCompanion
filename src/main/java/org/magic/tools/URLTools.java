package org.magic.tools;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.net.ssl.SSLHandshakeException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;
import org.xml.sax.SAXException;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class URLTools {

	private static Logger logger = MTGLogger.getLogger(URLTools.class);
	
	public static final String HEADER_JSON="application/json";
	
	private URLTools() 
	{
			
	}
	
	public static HttpURLConnection openConnection(String url) throws IOException {
		return openConnection(new URL(url));
	}
	
	public static HttpURLConnection getConnection(String url) throws IOException {
		return getConnection(new URL(url),MTGConstants.USER_AGENT);
	}
	
	public static HttpURLConnection getConnection(URL url,String userAgent) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		try{
			
			connection.setRequestProperty("User-Agent", userAgent);
			connection.setAllowUserInteraction(true);
			connection.setInstanceFollowRedirects(true);
			int status = connection.getResponseCode();
			
			if (!isCorrectConnection(connection) && (status == HttpURLConnection.HTTP_MOVED_TEMP|| status == HttpURLConnection.HTTP_MOVED_PERM|| status == HttpURLConnection.HTTP_SEE_OTHER)) {
				return getConnection(connection.getHeaderField("Location"));
			}
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
		HttpURLConnection con = getConnection(url,MTGConstants.USER_AGENT);
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
	
	public static org.w3c.dom.Document extractXML(String url)  throws IOException
	{
		return extractXML(new URL(url));
	}
	
	private static org.w3c.dom.Document extractXML(URL url) throws IOException {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(openConnection(url).getInputStream());
		} catch (Exception e) {
			throw new IOException(e);
		} 
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
	}

	public static boolean isCorrectConnection(HttpURLConnection connection) {
			try {
				
				connection.getHeaderFields().entrySet().forEach(e->logger.trace(e.getKey() +" " + e.getValue()));
				
				int resp=connection.getResponseCode();
				if(resp >= 200 && resp < 300)
				{
					return true;
				}
				else
				{
					if(connection.getErrorStream()!=null)
						logger.trace(IOUtils.toString(connection.getErrorStream(),MTGConstants.DEFAULT_ENCODING));

					return false;
				}
			} catch (IOException e) {
				logger.error(e);
				return false;
			}
	}
	
	
}
