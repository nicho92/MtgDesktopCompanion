package org.magic.api.pricers.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class EbayPricer extends AbstractMagicPricesProvider {

	@Override
	public STATUT getStatut() {
		return STATUT.STABLE;
	}

	String keyword = "";

	public EbayPricer() {
		super();

	}

	public List<MagicPrice> getPrice(MagicEdition me, MagicCard card) throws IOException {
		List<MagicPrice> prices = new ArrayList<>();

		String url = getString("URL");
		url = url.replaceAll("%API_KEY%", getString("API_KEY"));
		url = url.replaceAll("%COUNTRY%", getString("COUNTRY"));
		url = url.replaceAll("%MAX%", getString("MAX"));
		keyword = card.getName();

		if (me != null)
			keyword += " " + me.getSet();

		setProperty("KEYWORD", keyword);

		keyword = URLEncoder.encode(keyword, getString("ENCODING"));

		String link = url.replaceAll("%KEYWORD%", keyword);

		logger.info(getName() + " looking for " + keyword);

		JsonReader reader = new JsonReader(new InputStreamReader(new URL(link).openStream(), "UTF-8"));
		JsonElement root = new JsonParser().parse(reader);

		JsonElement articles = root.getAsJsonObject().entrySet().iterator().next().getValue().getAsJsonArray().get(0)
				.getAsJsonObject().get("searchResult");

		if (articles.getAsJsonArray().get(0).getAsJsonObject().get("item") == null) {
			logger.info(getName() + " find nothing");
			return prices;
		}

		JsonArray items = articles.getAsJsonArray().get(0).getAsJsonObject().get("item").getAsJsonArray();

		logger.trace(items);

		for (JsonElement el : items) {
			MagicPrice mp = new MagicPrice();
			String etat = "";
			String title = el.getAsJsonObject().get("title").getAsString();
			String consultURL = el.getAsJsonObject().get("viewItemURL").getAsString();
			double price = el.getAsJsonObject().get("sellingStatus").getAsJsonArray().get(0).getAsJsonObject()
					.get("currentPrice").getAsJsonArray().get(0).getAsJsonObject().get("__value__").getAsDouble();
			String currency = el.getAsJsonObject().get("sellingStatus").getAsJsonArray().get(0).getAsJsonObject()
					.get("currentPrice").getAsJsonArray().get(0).getAsJsonObject().get("@currencyId").getAsString();
			try {
				etat = el.getAsJsonObject().get("condition").getAsJsonArray().get(0).getAsJsonObject()
						.get("conditionDisplayName").getAsString();
			} catch (NullPointerException e) {
				etat = "";
			}

			mp.setSeller(title);
			mp.setUrl(consultURL);
			mp.setCurrency(currency);
			mp.setValue(price);
			mp.setSite(getName());
			mp.setQuality(etat);
			prices.add(mp);
		}

		logger.info(getName() + " find " + prices.size() + " item(s)");

		java.util.Collections.sort(prices);
		return prices;
	}

	@Override
	public String getName() {
		return "Ebay";
	}

	@Override
	public void alertDetected(List<MagicPrice> p) {
		logger.error("not implemented");

	}

	@Override
	public void initDefault() {
		setProperty("MAX", "10");
		setProperty("COUNTRY", "EBAY-FR");
		setProperty("API_KEY", "none04674-8d13-4421-af9e-ec641c7ee59");
		setProperty("URL","http://svcs.ebay.fr/services/search/FindingService/v1?SECURITY-APPNAME=%API_KEY%&OPERATION-NAME=findItemsByKeywords&RESPONSE-DATA-FORMAT=JSON&GLOBAL-ID=%COUNTRY%&keywords=%KEYWORD%&paginationInput.entriesPerPage=%MAX%");
		setProperty("WEBSITE", "http://www.ebay.com/");
		setProperty("ENCODING", "UTF-8");
		setProperty("KEYWORD", "");

	}

	@Override
	public String getVersion() {
		return "1.0";
	}

}
