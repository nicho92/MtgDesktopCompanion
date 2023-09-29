package org.magic.api.pricers.impl;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;

public class StarCityGamesPricer extends AbstractPricesProvider {

	NumberFormat format = NumberFormat.getCurrencyInstance();
	
	private static final String BASE_URL="https://starcitygames.com";
	
	@Override
	public List<MagicPrice> getLocalePrice(MagicCard card) throws IOException {
		List<MagicPrice> ret = new ArrayList<>();
		var c = URLTools.newClient();
		
		RequestBuilder.build().setClient(c).get().url(BASE_URL).execute();
		
		String cardName = card.getName();
		
		var payload = "{\"Keyword\":\"\",\"Variant\":{\"MaxPerPage\":32},\"FacetSelections\":{\"card_name\":[\""+cardName+"\"]},\"MaxPerPage\":24,\"clientguid\":\"cc3be22005ef47d3969c3de28f09571b\"}";

		
		var hds = c.buildMap()
		 .put("Accept-Encoding","gzip, deflate, br")
		 .put("Accept","*/*")
		 .put("Accept-Language","fr-FR,fr;q=0.9,en;q=0.8")
		 .put("Sec-Ch-Ua", "\"Google Chrome\";v=\"117\", \"Not;A=Brand\";v=\"8\", \"Chromium\";v=\"117\"")
		 .put("Sec-Ch-Ua-Mobile", "?0")
		 .put("Sec-Ch-Ua-Platform", "Window")
		 .put("Sec-Fetch-Dest", "empty")
		 .put("Sec-Fetch-Mode", "cors")
		 .put("Sec-Fetch-Site", "cross-site")
		 .put("Referer", BASE_URL)
		 .put("Origin", BASE_URL)
		 .put("Content-Type", "application/json")
		 .put("User-Agent", MTGConstants.USER_AGENT).build();
		
		
		
		var resp =c.doPost("https://essearchapi-na.hawksearch.com/api/v2/search", new StringEntity(payload), hds);
		var json = URLTools.toJson(resp.getEntity().getContent()).getAsJsonObject().get("Results").getAsJsonArray();
		
		
		json.forEach(je->{
			
			var arr = je.getAsJsonObject().get("Document").getAsJsonObject().get("child_information").getAsJsonArray();
			
			arr.forEach(str->{
				var jinfo = URLTools.toJson(str.getAsString()).getAsJsonObject();
				
				if(jinfo.get("qty").getAsInt()>0 && jinfo.get("sku").getAsString().toUpperCase().contains(card.getCurrentSet().getId().toUpperCase())) {
					var mp = new MagicPrice();
					mp.setMagicCard(card);
					mp.setCurrency(Currency.getInstance("USD"));
					mp.setCountry(Locale.US.getDisplayCountry(MTGControler.getInstance().getLocale()));
					mp.setSite(getName());
					mp.setSeller(getName());
					mp.setQty(jinfo.get("qty").getAsInt());
					mp.setLanguage(jinfo.get("language").getAsString());
					mp.setUrl(BASE_URL+jinfo.get("url").getAsString());
					mp.setValue(jinfo.get("price").getAsDouble());
					mp.setFoil(jinfo.get("p_cat_url").getAsString().endsWith("finish=Foil"));
					mp.setQuality(jinfo.get("condition").getAsString());
					
					ret.add(mp);
				}
			});
		});
		
		logger.info("{} found {} items",getName(),ret.size());

		return ret;

	}


	@Override
	public String getName() {
		return "StarCityGame";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.BUGGED;
	}

	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of("NB_PAGE", "1");

	}

	@Override
	public String getVersion() {
		return "0.5";
	}


}
