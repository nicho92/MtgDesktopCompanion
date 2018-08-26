package org.magic.api.decksniffer.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RegExUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.interfaces.MTGDeckSniffer;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.InstallCert;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TappedOutDeckSniffer extends AbstractDeckSniffer {

	private static final String CERT_SERV = "CERT_SERV";
	private static final String URL_JSON = "URL_JSON";
	private static final String FORMAT = "FORMAT";
	private static final String PASS = "PASS";
	private static final String LOGIN2 = "LOGIN";
	private String uriBase="https://tappedout.net";
	private CookieStore cookieStore;
	private HttpClient httpclient;
	private HttpContext httpContext;

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

	public static void main(String[] args) {
		MTGDeckSniffer snif = new TappedOutDeckSniffer();
		RetrievableDeck d;
		try {
			d = snif.getDeckList().get(0);
			snif.getDeck(d);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public TappedOutDeckSniffer() {
		super();

		if(getBoolean("LOAD_CERTIFICATE"))
		{
			try {
				InstallCert.installCert("tappedout.net");
				setProperty("LOAD_CERTIFICATE", "false");
			} catch (Exception e1) {
				logger.error(e1);
			}
		}
	}

	@Override
	public String[] listFilter() {
		return new String[] { "latest", "standard", "modern", "legacy", "vintage", "edh", "tops", "pauper", "aggro","budget", "control" };
	}

	@Override
	public String getName() {
		return "TappedOut";
	}

	private void initConnexion() throws IOException {
		cookieStore = new BasicCookieStore();
		httpContext = new BasicHttpContext();
		httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
		httpclient = HttpClients.custom().setUserAgent(MTGConstants.USER_AGENT).setRedirectStrategy(new LaxRedirectStrategy()).build();
		
		httpclient.execute(new HttpGet(uriBase+"/accounts/login/?next=/"), httpContext);
		
		HttpPost login = new HttpPost(uriBase+"/accounts/login/");
		List<NameValuePair> nvps = new ArrayList<>();
							nvps.add(new BasicNameValuePair("next", "/"));
							nvps.add(new BasicNameValuePair("username", getString(LOGIN2)));
							nvps.add(new BasicNameValuePair("password", getString(PASS)));
							nvps.add(new BasicNameValuePair("csrfmiddlewaretoken", getCookieValue("csrftoken")));
		login.setEntity(new UrlEncodedFormEntity(nvps));
		login.addHeader("Referer", uriBase+"/accounts/login/?next=/");
		login.addHeader("Upgrade-Insecure-Requests", "1");
		login.addHeader("Origin", uriBase);
		HttpResponse resp = httpclient.execute(login, httpContext);
		EntityUtils.consume(resp.getEntity());
		logger.debug("Connection : " + getString(LOGIN2) + " " + resp.getStatusLine().getReasonPhrase());
		
	}

	@Override
	public MagicDeck getDeck(RetrievableDeck info) throws IOException {
		if(cookieStore==null)
			initConnexion();
		
		logger.debug("sniff deck at " + info.getUrl());
		
		
		HttpGet get = new HttpGet(info.getUrl());
		HttpResponse resp = httpclient.execute(get, httpContext);
		String responseBody = EntityUtils.toString(resp.getEntity());
		logger.debug("sniff deck : "+ resp.getStatusLine().getReasonPhrase());
		
		MagicDeck deck = new MagicDeck();
		JsonElement root = new JsonParser().parse(responseBody);
		deck.setName(root.getAsJsonObject().get("name").getAsString());
		deck.setDescription(root.getAsJsonObject().get("url").getAsString());
		for (int i = 0; i < root.getAsJsonObject().get("inventory").getAsJsonArray().size(); i++) {
			JsonArray inv = root.getAsJsonObject().get("inventory").getAsJsonArray().get(i).getAsJsonArray();
			String cardName = inv.get(0).getAsString();
			String position = inv.get(1).getAsJsonObject().get("b").getAsString();
			int qte = inv.get(1).getAsJsonObject().get("qty").getAsInt();

			// remove foil if present
			cardName = RegExUtils.replaceAll(cardName, "\\*.+?\\*", "").trim();

			// ged ed if present
			String idSet = null;
			Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(cardName);
			while (m.find()) {
				idSet = (m.group(1));
			}
			cardName = RegExUtils.replaceAll(cardName, "\\(([^)]+)\\)", "").trim();

			// remove behavior if present
			if (cardName.contains("#"))
				cardName = cardName.substring(0, cardName.indexOf('#')).trim();

			if (cardName.contains("//"))
				cardName = cardName.substring(0, cardName.indexOf("//")).trim();

			List<MagicCard> ret;
			if (idSet == null) {
				if (MagicCard.isBasicLand(cardName)) {
					MagicEdition ed = new MagicEdition();
					ed.setId(MTGControler.getInstance().get("default-land-deck"));
					ret = MTGControler.getInstance().getEnabledCardsProviders().searchCardByName( cardName, ed,
							true);
				} else {
					ret = MTGControler.getInstance().getEnabledCardsProviders().searchCardByName( cardName, null,
							true);
				}

			} else {
				MagicEdition ed = new MagicEdition();
				ed.setId(idSet);
				ret = MTGControler.getInstance().getEnabledCardsProviders().searchCardByName( cardName, ed, true);
			}

			if (!ret.isEmpty()) {
				setChanged();
				notifyObservers(deck.getMap());

				if (position.equalsIgnoreCase("main"))
					deck.getMap().put(ret.get(0), qte);
				else
					deck.getMapSideBoard().put(ret.get(0), qte);
			}
		}
		return deck;

	}

	public List<RetrievableDeck> getDeckList() throws IOException {

		if(cookieStore==null)
			initConnexion();
		
		String tappedJson = RegExUtils.replaceAll(getString(URL_JSON), "%FORMAT%", getString(FORMAT));
		logger.debug("sniff url : " + tappedJson);

		HttpResponse resp = httpclient.execute(new HttpGet(tappedJson), httpContext);
		String responseBody = EntityUtils.toString(resp.getEntity());
		
		JsonElement root = new JsonParser().parse(responseBody);
		List<RetrievableDeck> list = new ArrayList<>();

		for (int i = 0; i < root.getAsJsonArray().size(); i++) {
			JsonObject obj = root.getAsJsonArray().get(i).getAsJsonObject();
			RetrievableDeck deck = new RetrievableDeck();
			deck.setName(obj.get("name").getAsString());
			try {
				URI u = new URI(obj.get("resource_uri").getAsString());
				
				deck.setUrl(u);
			} catch (URISyntaxException e) {
				deck.setUrl(null);
			}
			deck.setAuthor(obj.get("user").getAsString());
			deck.setColor("");
			list.add(deck);
		}
		EntityUtils.consume(resp.getEntity());
		
		return list;
	}

	private String getCookieValue(String cookieName) {
		String value = null;
		for (Cookie cookie : cookieStore.getCookies()) {
			if (cookie.getName().equals(cookieName)) {
				value = cookie.getValue();
				break;
			}
		}
		return value;
	}

	@Override
	public void initDefault() {
		setProperty(LOGIN2, "login@mail.com");
		setProperty(PASS, "changeme");
		setProperty(FORMAT, "standard");
		setProperty(URL_JSON, uriBase+"/api/deck/latest/%FORMAT%");
		setProperty(CERT_SERV, "www.tappedout.net");

	}


}
