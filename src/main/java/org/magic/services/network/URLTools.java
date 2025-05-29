package org.magic.services.network;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.logging.log4j.Logger;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jspecify.annotations.Nullable;
import org.magic.services.MTGConstants;
import org.magic.services.logging.MTGLogger;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.XMLTools;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;


public class URLTools {



	private static Logger logger = MTGLogger.getLogger(URLTools.class);

	public static final String HEADER_JSON="application/json";
	public static final String HEADER_HTML="text/html";
	public static final String HEADER_CSS="text/css";
	public static final String HEADER_TEXT="text/plain";

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
	public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
	public static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
	public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

	public static final String AUTHORIZATION = "Authorization";
	
	
	private URLTools()	{
	}

	public static String getExternalIp()
	{
		try {
			return extractAsString("http://checkip.amazonaws.com");
		} catch (IOException _) {
			return "0.0.0.0";
		}
	}



	public static Map<String,String> parseLinksHeader(Header header)
	{
		var map = new HashMap<String,String>();

		if(header==null)
			return map;

		var p = Pattern.compile("<(.*?)>;\\srel=\"(.*?)\"");

		var m = p.matcher(header.getValue());
		while(m.find())
			map.put(m.group(2), m.group(1));

		return map;

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

	public static JsonElement toJson(InputStream content)
	{
		try(var reader = new InputStreamReader(content))
		{
			return JsonParser.parseReader(reader);
		} catch (IOException _) {
			return null;
		}
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
		
		List<Extension> extensions = List.of(TablesExtension.create());
		
		var parser = Parser.builder().extensions(extensions).build();
		Node document = parser.parse(c);
		return HtmlRenderer.builder().extensions(extensions).build().render(document);
	}

	public static org.w3c.dom.Document extractAsXml(String url) throws IOException {
		return RequestBuilder.build().setClient(newClient()).url(url).get().toXml();
	}

	public static JsonElement extractAsJson(String url) 	{
		return RequestBuilder.build().setClient(newClient()).url(url).get().toJson();
	}

	public static Document extractAsHtml(String url) throws IOException 	{
		return RequestBuilder.build().setClient(newClient()).url(url).get().toHtml();
	}

	public static InputStream extractAsInputStream(String url) throws IOException 	{
		return RequestBuilder.build().setClient(newClient()).url(url).get().execute().getEntity().getContent();
	}

	public static String extractAsString(String url) throws IOException	{
		return RequestBuilder.build().setClient(newClient()).url(url).get().toContentString();
	}

	public static void download(String url,File to) throws IOException {
		RequestBuilder.build().setClient(newClient()).url(url).get().download(to);
	}



	public static Document extractMarkdownAsHtml(String url) throws IOException
	{
		var ret = toHtmlFromMarkdown(extractAsString(url));
		return toHtml(ret);
	}

	public static BufferedImage extractAsImage(String uri, int w, int h) throws IOException {
		return ImageTools.resize(extractAsImage(uri), h, w);
	}

	public static BufferedImage extractAsImage(String url) throws IOException	{
		if(url.startsWith("//"))
			url="https:"+url;

		return RequestBuilder.build().setClient(URLTools.newClient()).url(url).get().toImage();
	}


	public static boolean isCorrectConnection(String url)
	{
		int resp;
		try {
			resp = RequestBuilder.build().setClient(URLTools.newClient()).url(url).get().execute().getStatusLine().getStatusCode();
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
			RequestBuilder.build().setClient(c).url(url).get().execute();
			return c.getHttpContext().getRedirectLocations().get(0).toASCIIString();

		} catch (Exception _) {
			return url;
		}
	}

	public static byte[] readAsBinary(String url) throws IOException {
			var is = RequestBuilder.build().setClient(URLTools.newClient()).url(url).get().execute().getEntity().getContent();
			return IOUtils.toByteArray(is);


	}


	public static String getInternalIP() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		}
		catch(Exception _)
		{
			return "127.0.0.1";
		}
	}

	public static String extractElementText(@Nullable Element element) {
		
		if(element==null)
			return "";
		
		return element.text();
	}


}
