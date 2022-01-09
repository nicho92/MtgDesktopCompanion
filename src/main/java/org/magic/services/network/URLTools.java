package org.magic.services.network;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;
import org.magic.services.network.RequestBuilder.METHOD;
import org.magic.tools.ImageTools;
import org.magic.tools.XMLTools;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;


public class URLTools {

	

	private static Logger logger = MTGLogger.getLogger(URLTools.class);
	
	public static final String HEADER_JSON="application/json";
	public static final String HEADER_HTML="text/html";
	public static final String HEADER_CSS="text/css";
	
	public static final String REFERER = "Referer";
	public static final String HOST = "Host";
	public static final String X_REQUESTED_WITH = "X-Requested-With";
	public static final String ACCEPT_LANGUAGE = "Accept-Language";
	public static final String ACCEPT_ENCODING = "Accept-Encoding";
	public static final String ACCEPT = "Accept";
	public static final String ORIGIN = "Origin";
	public static final String UPGR_INSECURE_REQ= "Upgrade-Insecure-Requests";
	public static final String USER_AGENT = "User-Agent";
	public static final String CONTENT_TYPE="Content-Type";
	public static final String REFERER_POLICY = "Referrer Policy";
	
	private URLTools()	{
	}
	
	public static String getExternalIp()
	{
		try {
			return extractAsString("http://checkip.amazonaws.com");
		} catch (IOException e) {
			return "0.0.0.0";
		}
	}
	


	public static String decode(String s) {
		return URLDecoder.decode(s, MTGConstants.DEFAULT_ENCODING);
	}
	
	public static String encode(String s)
	{
		return URLEncoder.encode(s, MTGConstants.DEFAULT_ENCODING);
	}
	
	public static Document toHtml(String content)
	{
		return Jsoup.parse(content);
	}
	
	public static JsonElement toJson(String content)
	{
		return JsonParser.parseString(content);
	}
	
	public static org.w3c.dom.Document toXml(File f) throws IOException {
		try {
			return  XMLTools.createSecureXMLDocumentBuilder().parse(new FileInputStream(f));
		} catch (Exception e) {
			throw new IOException(e);
		} 
	}
	
	
	private static String toHtmlFromMarkdown(String c)
	{
		var parser = Parser.builder().build();
		Node document = parser.parse(c);
		return HtmlRenderer.builder().build().render(document);
	}
	
	public static org.w3c.dom.Document extractAsXml(String url) throws IOException {
		return RequestBuilder.build().setClient(newClient()).url(url).method(METHOD.GET).toXml();
	}

	public static JsonElement extractAsJson(String url) throws IOException	{
		return RequestBuilder.build().setClient(newClient()).url(url).method(METHOD.GET).toJson();
	}
	
	public static Document extractAsHtml(String url) throws IOException 	{
		return RequestBuilder.build().setClient(newClient()).url(url).method(METHOD.GET).toHtml();
	}
	
	public static InputStream extractAsInputStream(String url) throws IOException 	{
		return RequestBuilder.build().setClient(newClient()).url(url).method(METHOD.GET).execute().getEntity().getContent();
	}
	
	public static String extractAsString(String url) throws IOException	{
		return RequestBuilder.build().setClient(newClient()).url(url).method(METHOD.GET).toContentString(); 
	}

	public static void download(String url,File to) throws IOException {
		RequestBuilder.build().setClient(newClient()).url(url).method(METHOD.GET).download(to);
	}
	
	

	public static Document extractMarkdownAsHtml(String url) throws IOException
	{
		var ret = toHtmlFromMarkdown(extractAsString(url));
		ret=ret.replace("img/", MTGConstants.MTG_DESKTOP_WIKI_RAW_URL+"/img/");
		return toHtml(ret);
	}
	
	public static BufferedImage extractAsImage(String uri, int w, int h) throws IOException {
		return ImageTools.resize(extractAsImage(uri), h, w);
	}
	
	public static BufferedImage extractAsImage(String url) throws IOException	{
		if(url.startsWith("//"))
			url="https:"+url;
		
		return RequestBuilder.build().setClient(URLTools.newClient()).url(url).method(METHOD.GET).toImage(); 
	}
	
	
	public static boolean isCorrectConnection(String url) 
	{
		int resp;
		try {
			resp = RequestBuilder.build().setClient(URLTools.newClient()).url(url).method(METHOD.GET).execute().getStatusLine().getStatusCode();
			return resp >= 200 && resp < 300;
		} catch (IOException e) {
			logger.error(e);
			return false;
		}
		
		
	}
	
	public static MTGHttpClient newClient() {
		return new MTGHttpClient();
	}



	public static String getLocation(String url) {
		try {
			var c = URLTools.newClient();
			RequestBuilder.build().setClient(c).url(url).method(METHOD.GET).execute();
			return c.getHttpContext().getRedirectLocations().get(0).toASCIIString();
			
		} catch (Exception e) {
			return url;
		}
	}

	public static byte[] readAsBinary(String url) throws IOException {
			var is = RequestBuilder.build().setClient(URLTools.newClient()).url(url).method(METHOD.GET).execute().getEntity().getContent();
			return IOUtils.toByteArray(is);
		
			
	}


	
	
}
