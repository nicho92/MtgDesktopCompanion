package org.magic.api.pricers.impl;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;
import org.magic.services.MTGConstants;
import org.magic.tools.InstallCert;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DeckTutorPricer extends AbstractMagicPricesProvider {

	private static final String MAX_RESULT = "MAX_RESULT";
	private static final String LOAD_CERTIFICATE = "LOAD_CERTIFICATE";
	private String dsite = "www.decktutor.com";

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	private BasicCookieStore cookieStore;
	private BasicHttpContext httpContext;
	private static int sequence = 1;
	private JsonParser parser;
	private ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

		public String handleResponse(final HttpResponse response) throws IOException {
			int status = response.getStatusLine().getStatusCode();
			HttpEntity entity = response.getEntity();

			if (status >= 200 && status < 300) {
				return entity != null ? EntityUtils.toString(entity) : null;
			} else {
				throw new ClientProtocolException(
						"Unexpected response status: " + status + ":" + EntityUtils.toString(entity));
			}
		}
	};

	public DeckTutorPricer() {
		super();
		cookieStore = new BasicCookieStore();
		httpContext = new BasicHttpContext();
		parser = new JsonParser();
		if(getBoolean(LOAD_CERTIFICATE))
		{
			try {
				InstallCert.installCert("decktutor.com");
				setProperty(LOAD_CERTIFICATE, "false");
			} catch (Exception e1) {
				logger.error(e1);
			}
		}
	}

	private String getMD5(String chaine) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] messageDigest = md.digest(chaine.getBytes());
		BigInteger number = new BigInteger(1, messageDigest);
		return number.toString(16);
	}

	public static void increment() {
		sequence++;
	}

	@Override
	public List<MagicPrice> getPrice(MagicEdition me, MagicCard card) throws IOException {
		HttpClient httpClient = HttpClientBuilder.create().build();
		httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
		JsonObject jsonparams = new JsonObject();
		jsonparams.addProperty("login", getString("LOGIN"));
		jsonparams.addProperty("password", getString("PASS"));

		HttpPost reqCredential = new HttpPost(getString("URL") + "/account/login");
		reqCredential.addHeader("content-type", "application/json");
		reqCredential.setEntity(new StringEntity(jsonparams.toString()));

		String response = httpClient.execute(reqCredential, responseHandler, httpContext);
		logger.debug(getName() + " connected with " + response);

		JsonElement root = new JsonParser().parse(response);

		String authToken = root.getAsJsonObject().get("auth_token").getAsString();
		String authSecrectToken = root.getAsJsonObject().get("auth_token_secret").getAsString();

		logger.info(getName() + " Looking for price " + getString("URL") + "/search/serp");

		HttpPost reqSearch = new HttpPost(getString("URL") + "/search/serp");
		reqSearch.addHeader("x-dt-Auth-Token", authToken);
		reqSearch.addHeader("x-dt-Sequence", String.valueOf(sequence));
		try {
			reqSearch.addHeader("x-dt-Signature", getMD5(sequence + ":" + authSecrectToken));
		} catch (NoSuchAlgorithmException e) {
			throw new IOException(e);
		}
		reqSearch.addHeader("Content-type", "application/json");
		reqSearch.addHeader("Accept", "application/json");
		reqSearch.addHeader("x-dt-cdb-Language", "en");
		reqSearch.addHeader("User-Agent",MTGConstants.USER_AGENT);

		jsonparams = new JsonObject();
		jsonparams.addProperty("name", card.getName());
		jsonparams.addProperty("game", "mtg");
		if (me != null)
			jsonparams.addProperty("set", me.getId().toUpperCase());
		else
			jsonparams.addProperty("set", card.getCurrentSet().getId().toUpperCase());

		JsonObject obj = new JsonObject();
		obj.add("search", jsonparams);

		if (getString(MAX_RESULT) != null)
			obj.addProperty("limit", getString(MAX_RESULT));

		logger.trace(getName() + " request :" + obj);
		reqSearch.setEntity(new StringEntity(obj.toString()));
		response = httpClient.execute(reqSearch, responseHandler, httpContext);
		logger.trace(getName() + " response :" + response);
		increment();

		return parseResult(response);
	}

	private List<MagicPrice> parseResult(String response) {
		List<MagicPrice> list = new ArrayList<>();

		JsonElement e = parser.parse(response);

		JsonArray arr = e.getAsJsonObject().get("results").getAsJsonObject().get("items").getAsJsonArray();

		for (int i = 0; i < arr.size(); i++) {
			JsonObject item = arr.get(i).getAsJsonObject();
			MagicPrice price = new MagicPrice();
			price.setFoil(false);
			price.setSeller(item.get("seller").getAsJsonObject().get("nick").getAsString());
			price.setSite(getName());
			price.setLanguage(item.get("language").getAsString());
			price.setQuality(item.get("condition").getAsString());
			price.setCurrency(Currency.getInstance("EUR"));
			price.setValue(
					Double.parseDouble(item.get("price").getAsString().replaceAll(price.getCurrency().getCurrencyCode(), "").trim()));
			price.setUrl("https://mtg.decktutor.com/insertions/" + item.get("code").getAsString() + "/"
					+ item.get("title").getAsString().replaceAll(" - ", "-").replaceAll(" ", "-") + ".html");
			price.setShopItem(item);
			JsonArray attrs = item.get("attrs").getAsJsonArray();
			if (attrs.size() < 0) {
				price.setFoil(false);
			} else {
				price.setFoil(attrs.toString().contains("foil"));
			}
			// https://mtg.decktutor.com/insertions/EU1BCUS32554473N/lotus-petal-tmp-english-good.html

			list.add(price);
		}

		logger.info(getName() + " found " + list.size() + " items");
		return list;
	}

	@Override
	public String getName() {
		return "Deck Tutor";
	}

	@Override
	public void alertDetected(List<MagicPrice> p) {
		// nothing to do
	}

	@Override
	public void initDefault() {
		setProperty("URL", "https://ws.decktutor.com/app/v2");

		setProperty("WEBSITE", "https://" + dsite);
		setProperty("LANG", "en");
		setProperty("LOGIN", "login");
		setProperty(MAX_RESULT, "");
		setProperty("PASS", "PASS");
		setProperty("AUTOMATIC_ADD_CARD_ALERT", "false");
		setProperty(LOAD_CERTIFICATE, "true");

	}

	@Override
	public String getVersion() {
		return "0.5";
	}

}
