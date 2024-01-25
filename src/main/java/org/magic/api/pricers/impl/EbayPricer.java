package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGPrice;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;

import com.google.gson.JsonElement;

public class EbayPricer extends AbstractPricesProvider {


	private static final String URL_BASE ="https://svcs.ebay.com/services/search/FindingService/v1";
	@Override
	public List<MTGPrice> getLocalePrice(MTGCard card) throws IOException {
		List<MTGPrice> prices = new ArrayList<>();
		String keyword = card.getName();
			   keyword += " " + card.getCurrentSet().getSet();


			   var b = RequestBuilder.build().setClient(URLTools.newClient()).get()
				.url(URL_BASE)
				.addContent("SECURITY-APPNAME", getAuthenticator().get("API_KEY"))
				.addContent("OPERATION-NAME", "findItemsByKeywords")
				.addContent("RESPONSE-DATA-FORMAT", "JSON")
				.addContent("GLOBAL-ID", getAuthenticator().get("COUNTRY","EBAY-FR"))
				.addContent("paginationInput.entriesPerPage", getString("MAX"))
				.addContent("keywords", keyword);

		if(getBoolean("FIXEDPRICE_ONLY"))
		{
			b.addContent("itemFilter(0).name", "ListingType");
			b.addContent("itemFilter(0).value(1)", "FixedPrice");
		}

		logger.info("{} looking for {}",getName(),keyword);

		JsonElement root = b.toJson();

		JsonElement articles = root.getAsJsonObject().entrySet().iterator().next().getValue().getAsJsonArray().get(0).getAsJsonObject().get("searchResult");

		if (articles.getAsJsonArray().get(0).getAsJsonObject().get("item") == null) {
			logger.info("{} find nothing",getName());
			return prices;
		}

		var items = articles.getAsJsonArray().get(0).getAsJsonObject().get("item").getAsJsonArray();

		logger.trace(items);

		for (JsonElement el : items) {
			var mp = new MTGPrice();

			var etat = "";
			var title = el.getAsJsonObject().get("title").getAsString();
			var consultURL = el.getAsJsonObject().get("viewItemURL").getAsString();
			var country = el.getAsJsonObject().get("location").getAsJsonArray().toString();
			var price = el.getAsJsonObject().get("sellingStatus").getAsJsonArray().get(0).getAsJsonObject()
					.get("currentPrice").getAsJsonArray().get(0).getAsJsonObject().get("__value__").getAsDouble();
			var currency = el.getAsJsonObject().get("sellingStatus").getAsJsonArray().get(0).getAsJsonObject()
					.get("currentPrice").getAsJsonArray().get(0).getAsJsonObject().get("@currencyId").getAsString();
			try {
				etat = el.getAsJsonObject().get("condition").getAsJsonArray().get(0).getAsJsonObject()
						.get("conditionDisplayName").getAsString();
			} catch (NullPointerException e) {
				etat = "";
			}



			mp.setMagicCard(card);
			mp.setCountry(country);
			mp.setSeller(title);
			mp.setUrl(consultURL);
			mp.setCurrency(currency);
			mp.setValue(price);
			mp.setSite(getName());
			mp.setQuality(etat);
			mp.setFoil(mp.getSeller().toLowerCase().contains("foil"));
			prices.add(mp);
		}

		logger.info("{} found {} offers",getName(),prices.size());

		return prices;
	}

	@Override
	public String getName() {
		return "Ebay";
	}


	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of("MAX", "10",
								"WEBSITE", "https://www.ebay.com/",
								"FIXEDPRICE_ONLY","false");

	}

	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of("COUNTRY","API_KEY");

	}


	@Override
	public String getVersion() {
		return "1.13.0";
	}

}
