package org.magic.services.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.logging.log4j.Logger;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jspecify.annotations.Nullable;
import org.magic.services.MTGConstants;
import org.magic.services.logging.MTGLogger;
import org.magic.services.tools.ImageTools;

public class URLTools {

	private static Logger logger = MTGLogger.getLogger(URLTools.class);
	private static final List<Extension> MARKDOWN_EXTENSIONS = List.of(TablesExtension.create());
	private static final Parser MARKDOWN_PARSER = Parser.builder().extensions(MARKDOWN_EXTENSIONS).build();
	private static final HtmlRenderer MARKDOWN_RENDERER = HtmlRenderer.builder().extensions(MARKDOWN_EXTENSIONS)
			.build();

	public static final String HEADER_JSON = "application/json";
	public static final String HEADER_HTML = "text/html";
	public static final String HEADER_CSS = "text/css";
	public static final String HEADER_TEXT = "text/plain";

	public static final String REFERER = "Referer";
	public static final String HOST = "Host";
	public static final String X_REQUESTED_WITH = "X-Requested-With";
	public static final String ACCEPT_LANGUAGE = "Accept-Language";
	public static final String ACCEPT_ENCODING = "Accept-Encoding";
	public static final String ACCEPT = "Accept";
	public static final String ORIGIN = "Origin";
	public static final String UPGR_INSECURE_REQ = "Upgrade-Insecure-Requests";
	public static final String USER_AGENT = "User-Agent";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String REFERER_POLICY = "Referrer Policy";
	public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
	public static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
	public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

	public static final String AUTHORIZATION = "Authorization";

	private URLTools() {
	}

	public static String getExternalIp() {
		try {
			return extractAsString("http://checkip.amazonaws.com");
		} catch (IOException _) {
			return "0.0.0.0";
		}
	}

	public static Map<String, String> parseLinksHeader(Header header) {
		var map = new HashMap<String, String>();

		if (header == null)
			return map;

		var p = Pattern.compile("<(.*?)>;\\srel=\"(.*?)\"");

		var m = p.matcher(header.getValue());
		while (m.find())
			map.put(m.group(2), m.group(1));

		return map;

	}

	public static String decode(String s) {
		return URLDecoder.decode(s, MTGConstants.DEFAULT_ENCODING);
	}

	public static String encode(String s) {
		return URLEncoder.encode(s, MTGConstants.DEFAULT_ENCODING);
	}

	public static Document toHtml(String content) {
		return Jsoup.parse(content);
	}

	public static JsonElement toJson(String content) {
		return JsonParser.parseString(content);
	}

	public static JsonElement toJson(InputStream content) {
		try (var reader = new InputStreamReader(content)) {
			return JsonParser.parseReader(reader);
		} catch (IOException _) {
			return null;
		}
	}

	public static String toHtmlFromMarkdown(String c) {
		var document = MARKDOWN_PARSER.parse(c);
		return MARKDOWN_RENDERER.render(document);
	}

	public static org.w3c.dom.Document extractAsXml(String url) throws IOException {
		return RequestBuilder.build().newClient().url(url).get().toXml();

	}

	public static JsonElement extractAsJson(String url) {
		try {
			return RequestBuilder.build().newClient().url(url).get()
					.addHeader(URLTools.ACCEPT, "application/json;q=0.9,*/*;q=0.8").toJson();
		} catch (Exception e) {
			logger.error("error while extracting json from {}", url, e);
			var ret = new JsonObject();
			ret.addProperty("error", e.getMessage());
			return ret;
		}
	}

	public static Document extractAsHtml(String url) throws IOException {
		return RequestBuilder.build().setClient(URLTools.newClient()).url(url).get().toHtml();

	}

	public static InputStream extractAsInputStream(String url) throws IOException {
		var content = RequestBuilder.build().newClient().url(url).get().execute().getEntity().getContent()
				.readAllBytes();
		return new ByteArrayInputStream(content);

	}

	public static String extractAsString(String url) throws IOException {
		return RequestBuilder.build().newClient().url(url).get().toContentString();
	}

	public static void download(String url, File to) throws IOException {

		RequestBuilder.build().newClient().url(url).get().download(to);

	}

	public static Document extractMarkdownAsHtml(String url) throws IOException {
		var ret = toHtmlFromMarkdown(extractAsString(url));
		return toHtml(ret);
	}

	public static BufferedImage extractAsImage(String url) throws IOException {

		if (url.startsWith("file:"))
			return ImageTools.readLocal(URI.create(url).toURL());

		if (url.startsWith("//"))
			url = "https:" + url;

		var client = URLTools.newClient();
		return RequestBuilder.build().setClient(client).url(url).get().toImage();

	}

	public static boolean isCorrectConnection(String url) {
		int resp;
		try {
			var client = URLTools.newClient();
			resp = RequestBuilder.build().setClient(client).url(url).get().execute().getStatusLine().getStatusCode();
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
		var c = URLTools.newClient();
		try {
			RequestBuilder.build().setClient(c).url(url).get().execute();
			return c.getHttpContext().getRedirectLocations().get(0).toASCIIString();
		} catch (IOException e) {
			logger.error(e);
			return url;
		}

	}

	public static byte[] readAsBinary(String url) throws IOException {
		var is = RequestBuilder.build().newClient().url(url).get().execute().getEntity().getContent();
		return IOUtils.toByteArray(is);
	}

	public static String getInternalIP() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (Exception _) {
			return "127.0.0.1";
		}
	}

	public static String extractElementText(@Nullable Element element) {

		if (element == null)
			return "";

		return element.text();
	}

	public static String toText(InputStream content) throws IOException {
		return new String(content.readAllBytes(), StandardCharsets.UTF_8);
	}

	public static Map<String, String> createSecHeaders() {

		return Map.of("sec-ch-ua", "\"Google Chrome\";v=\"147\", \"Not.A/Brand\";v=\"8\", \"Chromium\";v=\"147\"",
				"sec-ch-ua-mobile", "?0", "sec-ch-ua-platform", "\"Windows\"", "sec-ch-viewport-height", "368",
				"sec-ch-viewport-width", "1740", "sec-fetch-dest", "document", "sec-fetch-mode", "navigate",
				"sec-fetch-site", "same-origin", "sec-fetch-user", "?1", "upgrade-insecure-requests", "1");

	}

}
