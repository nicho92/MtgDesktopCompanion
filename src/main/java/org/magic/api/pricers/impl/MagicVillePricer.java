package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;
import org.magic.services.MTGConstants;

public class MagicVillePricer extends AbstractMagicPricesProvider {
	
	private HttpClient httpclient;
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
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

	public MagicVillePricer() {
		super();
		httpclient = HttpClientBuilder.create().build();

	}

	public List<MagicPrice> getPrice(MagicEdition me, MagicCard card) throws IOException {
		List<MagicPrice> list = new ArrayList<>();
		HttpPost searchPost = new HttpPost(getString("WEBSITE")+"/fr/resultats.php?zbob=1");
		List<NameValuePair> nvps = new ArrayList<>();
							nvps.add(new BasicNameValuePair("recherche_titre", card.getName()));
	
		searchPost.setEntity(new UrlEncodedFormEntity(nvps));
		String res = httpclient.execute(searchPost,responseHandler);
		String key = "ref=";
		String code = res.substring(res.indexOf(key), res.indexOf("\";"));
		String url = getString("WEBSITE")+"/fr/register/show_card_sale?"+code;
		
		logger.info(getName() + " looking for prices " + url);

		
		Document doc = Jsoup.connect(url).userAgent(MTGConstants.USER_AGENT).get();
		
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

		logger.info(getName() + " found " + list.size() + " item(s) return " + getString("MAX") + " items");

		if (list.size() > Integer.parseInt(getString("MAX")) && Integer.parseInt(getString("MAX")) > -1)
			return list.subList(0, Integer.parseInt(getString("MAX")));

		return list;
	}

	@Override
	public String getName() {
		return "Magic-Ville";
	}

	@Override
	public void alertDetected(List<MagicPrice> p) {
		// do nothing

	}

	@Override
	public void initDefault() {
		setProperty("MAX", "5");
		setProperty("WEBSITE", "http://www.magic-ville.com/");
		

	}

	@Override
	public String getVersion() {
		return "2.0";
	}

}
