package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGPrice;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.MTGConstants;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.URLTools;

public class MagicVillePricer extends AbstractPricesProvider {

	private static final String MAX = "MAX";
	private static final String WEBSITE = "https://www.magic-ville.com/";
	private MTGHttpClient httpclient;

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}


	public MagicVillePricer() {
		super();
		httpclient = URLTools.newClient();

	}

	@Override
	public List<MTGPrice> getLocalePrice(MTGCard card) throws IOException {
		List<MTGPrice> list = new ArrayList<>();

		String res = httpclient.toString(httpclient.doPost(WEBSITE+"/fr/resultats.php?zbob=1", httpclient.buildMap().put("recherche_titre", card.getName()).build(), null));
		if(res.length()>100)
		{
			logger.error("too much result");
			return list;
		}

		var key = "ref=";
		var code = res.substring(res.indexOf(key), res.indexOf("\";"));
		String url = WEBSITE+"/fr/register/show_card_sale?"+code;

		logger.info("{} looking for prices {}",getName(),card);


		Document doc =URLTools.extractAsHtml(url);

		Element table = null;
		try {
			table = doc.select("table[width=98%]").get(2); // select the first table.
		} catch (IndexOutOfBoundsException e) {
			logger.info("{} no sellers",getName());
			return list;
		}

		Elements rows = table.select(MTGConstants.HTML_TAG_TR);

		for (var i = 3; i < rows.size(); i = i + 2) {
			var ligne = rows.get(i);
			var cols = ligne.getElementsByTag(MTGConstants.HTML_TAG_TD);
			var mp = new MTGPrice();

			String price = cols.get(4).text();
			price = price.substring(0, price.length() - 1);
			mp.setValue(Double.parseDouble(price));
			mp.setScryfallId(card);
			mp.setCurrency("EUR");
			mp.setSeller(cols.get(0).text());
			mp.setSellerUrl(WEBSITE+"/fr/register/cards_to_sell?user="+mp.getSeller());
			mp.setSite(getName());
			mp.setUrl(url);
			mp.setQuality(cols.get(2).text());
			mp.setLanguage(cols.get(1).getElementsByTag("span").text());
			mp.setCountry("France");
			mp.setFoil(mp.getLanguage().toLowerCase().contains("foil"));

			list.add(mp);

		}


		if (list.size() > getInt(MAX) && getInt(MAX) > -1)
			return list.subList(0, getInt(MAX));

		logger.info("{} found {} offers",getName(),list.size());

		return list;
	}

	@Override
	public String getName() {
		return "Magic-Ville";
	}


	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of(MAX, "5");


	}

	@Override
	public String getVersion() {
		return "2.0";
	}


}
