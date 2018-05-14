package org.magic.api.shopping.impl;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.magic.api.beans.ShopItem;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;
import org.magic.api.pricers.impl.EbayPricer;
import org.magic.services.MTGConstants;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class EbayShopper extends AbstractMagicShopper {


	EbayPricer pricer;

	public EbayShopper() {
		super();
		pricer = new EbayPricer();
		setProps(pricer.getProperties());
	}

	public List<ShopItem> search(String search) {
		List<ShopItem> prices = new ArrayList<>();

		try {
			String url = getString("URL");
			url = url.replaceAll("%API_KEY%", getString("API_KEY"));
			url = url.replaceAll("%COUNTRY%", getString("COUNTRY"));
			url = url.replaceAll("%MAX%", getString("MAX"));

			String keyword = URLEncoder.encode(search, MTGConstants.DEFAULT_ENCODING);

			String link = url.replaceAll("%KEYWORD%", keyword);

			logger.info(getName() + " looking for " + link);

			try (JsonReader reader = new JsonReader(new InputStreamReader(new URL(link).openStream(), MTGConstants.DEFAULT_ENCODING))) {
				JsonElement root = new JsonParser().parse(reader);
				JsonElement articles = root.getAsJsonObject().entrySet().iterator().next().getValue().getAsJsonArray()
						.get(0).getAsJsonObject().get("searchResult");
				logger.debug(articles);
				if (articles.getAsJsonArray().get(0).getAsJsonObject().get("item") == null)
					return prices;

				JsonArray items = articles.getAsJsonArray().get(0).getAsJsonObject().get("item").getAsJsonArray();
				for (JsonElement el : items) {
					ShopItem mp = new ShopItem();

					String title = el.getAsJsonObject().get("title").getAsString();
					String type = el.getAsJsonObject().get("primaryCategory").getAsJsonArray().get(0).getAsJsonObject()
							.get("categoryName").getAsString();
					String consultURL = el.getAsJsonObject().get("viewItemURL").getAsString();
					double price = el.getAsJsonObject().get("sellingStatus").getAsJsonArray().get(0).getAsJsonObject()
							.get("currentPrice").getAsJsonArray().get(0).getAsJsonObject().get("__value__")
							.getAsDouble();
					URL image = new URL(el.getAsJsonObject().get("galleryURL").getAsJsonArray().get(0).getAsString());
					Date d = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
							.parse(el.getAsJsonObject().get("listingInfo").getAsJsonArray().get(0).getAsJsonObject()
									.get("startTime").getAsString());
					String id = el.getAsJsonObject().get("itemId").getAsString();
					mp.setName(title);
					mp.setUrl(new URL(consultURL));
					mp.setPrice(price);
					mp.setType(type);
					mp.setShopName(getName());
					mp.setImage(image);
					mp.setDate(d);
					mp.setId(id);
					prices.add(mp);
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return prices;
	}

	@Override
	public String getName() {
		return "Ebay";
	}

	@Override
	public void initDefault() {
		//do nothing

	}

	@Override
	public String getVersion() {
		return pricer.getVersion();
	}

}
