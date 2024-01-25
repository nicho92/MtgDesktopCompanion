package org.magic.api.pricers.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGPrice;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.network.URLTools;
import org.magic.services.tools.UITools;

import com.google.gson.stream.JsonReader;

public class MTGPricePricer extends AbstractPricesProvider {

	private static final String API_KEY = "API_KEY";

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

	@Override
	public List<MTGPrice> getLocalePrice(MTGCard card) throws IOException {
	
		if(getAuthenticator().get(API_KEY).isEmpty())
		{
			logger.error("No authentication information found for {}",getName());
			return new ArrayList<>();
		}
		
		
		
		String set = card.getCurrentSet().getSet().replace(" ", "_");

		String url = getString("WS_URL") + "?apiKey=" + getAuthenticator().get(API_KEY) + "&s=" + set;

		List<MTGPrice> ret = new ArrayList<>();

		logger.info("{} looking for price at {}",getName(),url);

		try {
			var reader = new JsonReader(new InputStreamReader(URLTools.extractAsInputStream(url)));
			reader.setLenient(true);
			reader.beginObject();
			reader.nextName();
			reader.beginArray();

			var name = "";
			var fairPrice = "";
			var mtgpriceID = "";

			while (reader.hasNext()) {
				reader.beginObject();
				reader.nextName();
				mtgpriceID = (reader.nextString());
				reader.nextName();
				name = (reader.nextString());
				reader.nextName();
				fairPrice = (reader.nextString());
				reader.endObject();

				if (name.equalsIgnoreCase(card.getName())) {
					var price = new MTGPrice();
					price.setCurrency("USD");
					price.setMagicCard(card);
					price.setSeller(getName());
					price.setUrl(getString("WEBSITE") + "/sets/" + set + "/"+ mtgpriceID.substring(0, mtgpriceID.indexOf(set)));
					price.setValue(UITools.parseDouble(fairPrice));
					price.setQuality("NM");
					var start=mtgpriceID.indexOf(set) + set.length();
					price.setFoil(mtgpriceID.indexOf("true", start)>-1);
					price.setSite(getName());
					ret.add(price);
					reader.close();
					logger.info("{} found {} items",getName(),ret.size());

					return ret;
				}
			}
			reader.close();
		} catch (Exception e) {

			return ret;
		}


		return ret;

	}

	@Override
	public String getName() {
		return "MTGPrice";
	}


	
	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of(API_KEY);
	}
	
	@Override
	public Map<String, String> getDefaultAttributes() {

		return Map.of("MAX", "5",
							   "WS_URL", "https://www.mtgprice.com/api",
								"WEBSITE", "https://www.mtgprice.com/");

	}


}
