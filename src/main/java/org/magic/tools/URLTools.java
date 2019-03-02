package org.magic.tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLHandshakeException;

import org.apache.commons.io.FileUtils;
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
	{
			
	}
	
	public static boolean isConnected()
	{
		try {
			return InetAddress.getByName(MTGConstants.URLTOOL_CHECK_URI).isReachable(3000);
		}  catch (Exception e) {
			return false;
		}
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
	
	public static void download(String url,File to) throws IOException
	{
		FileUtils.copyInputStreamToFile(openConnection(url).getInputStream(),to);
	}
	
	public static Document extractHtml(URL url) throws IOException
	{
		return toHtml(extractAsString(url));
	}
	
	public static org.w3c.dom.Document extractXML(URI url)  throws IOException
	{
		return extractXML(url.toURL());
	}
	
	
	public static org.w3c.dom.Document extractXML(String url)  throws IOException
	{
		try {
			return extractXML(new URI(url));
		} catch (URISyntaxException e) {
			throw new IOException(e);
		}
	}
	
	public static org.w3c.dom.Document extractXML(File f) throws IOException {
		try {
			return XMLTools.createSecureXMLFactory().newDocumentBuilder().parse(new FileInputStream(f));
		} catch (Exception e) {
			throw new IOException(e);
		} 
	}

	
	
	
	public static org.w3c.dom.Document extractXML(URL url) throws IOException {
		try {
			return XMLTools.createSecureXMLFactory().newDocumentBuilder().parse(openConnection(url).getInputStream());
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
	
	public static String extractAsString(String url,Charset enc) throws IOException
	{
		return extractAsString(new URL(url),enc); 
	}
	
	public static String extractAsString(URL url) throws IOException
	{
		return extractAsString(url,MTGConstants.DEFAULT_ENCODING); 
	}
	
	public static String extractAsString(String url) throws IOException
	{
		return extractAsString(new URL(url),MTGConstants.DEFAULT_ENCODING); 
	}
	
	public static String extractAsString(URL url,Charset enc) throws IOException
	{
		return IOUtils.toString(openConnection(url).getInputStream(), enc); 
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

	public static URLToolsClient newClient() {
		return new URLToolsClient();
	}
	
	
}
