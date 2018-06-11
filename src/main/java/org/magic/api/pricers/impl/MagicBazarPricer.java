package org.magic.api.pricers.impl;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;
import org.magic.services.MTGConstants;
import org.magic.tools.URLTools;

public class MagicBazarPricer extends AbstractMagicPricesProvider {

	Document doc;
	private ArrayList<MagicPrice> list;

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

	public MagicBazarPricer() {
		super();
		list = new ArrayList<>();
	}

	@Override
	public List<MagicPrice> getPrice(MagicEdition me, MagicCard card) throws IOException {
		list.clear();
		String url = getString("URL") + URLEncoder.encode(card.getName(), MTGConstants.DEFAULT_ENCODING);
		logger.info(getName() + " looking for prices " + url);

		try {
			doc = URLTools.extractHtml(url);
			Elements els = doc.select("div.filterElement");
			for (int i = 0; i < els.size(); i++) {
				Element e = els.get(i);
				MagicPrice mp = new MagicPrice();
				mp.setLanguage(e.getElementsByClass("langue").get(0).getElementsByTag("img").get(0).attr("alt"));
				mp.setQuality(e.getElementsByClass("etat").html());
				mp.setValue(Double.parseDouble(clean(e.select("div.prix").text())));
				mp.setCurrency("EUR");
				mp.setCountry("France");
				mp.setSite(getName());
				mp.setUrl(url);
				mp.setSeller(e.getElementsByClass("edition").get(0).getElementsByIndexEquals(0).get(0).text());
				mp.setFoil(!e.getElementsByClass("logo").isEmpty());
				list.add(mp);

			}
			return list;
		} catch (Exception e) {
			logger.trace("Error loading price for " + url, e);
			logger.info(getName() + " no item : " + e.getMessage());
			return list;
		}
	}

	private String clean(String html) {
		return StringEscapeUtils.escapeHtml3(html).replaceAll(",", ".").replaceAll(" ", "").replaceAll("€", "");
	}

	@Override
	public String getName() {
		return "MagicBazar";
	}

	@Override
	public void alertDetected(List<MagicPrice> okz) {
		// do nothing

	}

	@Override
	public void initDefault() {
		setProperty("URL", "https://www.magicbazar.fr/recherche/result.php?s=");
		

	}

	@Override
	public String getVersion() {
		return "1.4";
	}

}
