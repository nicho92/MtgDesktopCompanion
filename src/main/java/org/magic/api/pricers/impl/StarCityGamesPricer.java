package org.magic.api.pricers.impl;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Level;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.MTGConstants;
import org.magic.services.logging.MTGLogger;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;

public class StarCityGamesPricer extends AbstractPricesProvider {

	NumberFormat format = NumberFormat.getCurrencyInstance();
	
	private final String BASE_URL="https://starcitygames.com";
	
	public static void main(String[] args) throws IOException {
		
		MTGLogger.changeLevel(Level.DEBUG);
		
		var mc = new MagicCard();
		mc.setName("Revive the Shire");
		
		new StarCityGamesPricer().getLocalePrice(mc);
	}
	
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
			
			var jo = je.getAsJsonObject().get("Document").getAsJsonObject().get("child_information").getAsJsonArray();
			
			logger.info(jo);
			
			var mp = new MagicPrice();
			mp.setMagicCard(card);
			mp.setCountry("US");
			mp.setSite(BASE_URL);
			mp.setCurrency("USD");
			
			mp.setQty(1);
			mp.setLanguage("");
			mp.setValue(1.0);
			mp.setSeller(getName());
			
			ret.add(mp);
			
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
