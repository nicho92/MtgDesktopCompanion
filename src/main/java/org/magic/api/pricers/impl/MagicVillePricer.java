package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;
import org.magic.services.MTGConstants;
import org.magic.tools.URLTools;
import org.magic.tools.URLToolsClient;

public class MagicVillePricer extends AbstractMagicPricesProvider {
	
	private static final String MAX = "MAX";
	private static final String WEBSITE = "WEBSITE";
	private URLToolsClient httpclient;
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	
	public MagicVillePricer() {
		super();
		httpclient = URLTools.newClient();

	}

	public List<MagicPrice> getLocalePrice(MagicEdition me, MagicCard card) throws IOException {
		List<MagicPrice> list = new ArrayList<>();
		
		List<NameValuePair> nvps = new ArrayList<>();
							nvps.add(new BasicNameValuePair("recherche_titre", card.getName()));

		
		
		String res = httpclient.doPost(getString(WEBSITE)+"/fr/resultats.php?zbob=1", nvps, null);
		if(res.length()>100)
		{
			logger.error("too much result");
			return list;
		}
		
		String key = "ref=";
		String code = res.substring(res.indexOf(key), res.indexOf("\";"));
		String url = getString(WEBSITE)+"/fr/register/show_card_sale?"+code;
		
		logger.info(getName() + " looking for prices " + url);

		
		Document doc =URLTools.extractHtml(url);
		
		Element table = null;
		try {
			table = doc.select("table[width=98%]").get(2); // select the first table.
		} catch (IndexOutOfBoundsException e) {
			logger.info(getName() + " no sellers");
			return list;
		}

		Elements rows = table.select(MTGConstants.HTML_TAG_TR);

		for (int i = 3; i < rows.size(); i = i + 2) {
			Element ligne = rows.get(i);
			Elements cols = ligne.getElementsByTag(MTGConstants.HTML_TAG_TD);
			MagicPrice mp = new MagicPrice();

			String price = cols.get(4).text();
			price = price.substring(0, price.length() - 1);
			mp.setValue(Double.parseDouble(price));
			mp.setCurrency("EUR");
			mp.setSeller(cols.get(0).text());
			mp.setSite(getName());
			mp.setUrl(url);
			mp.setQuality(cols.get(2).text());
			mp.setLanguage(cols.get(1).getElementsByTag("span").text());
			mp.setCountry("France");

			list.add(mp);

		}

		logger.info(getName() + " found " + list.size() + " item(s) return " + getString(MAX) + " items");

		if (list.size() > getInt(MAX) && getInt(MAX) > -1)
			return list.subList(0, getInt(MAX));

		return list;
	}

	@Override
	public String getName() {
		return "Magic-Ville";
	}


	@Override
	public void initDefault() {
		setProperty(MAX, "5");
		setProperty(WEBSITE, "https://www.magic-ville.com/");
		

	}

	@Override
	public String getVersion() {
		return "2.0";
	}

}
