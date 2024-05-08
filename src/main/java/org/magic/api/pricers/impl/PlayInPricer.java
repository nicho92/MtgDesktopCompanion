package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGPrice;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.MTGControler;
import org.magic.services.network.URLTools;

public class PlayInPricer extends AbstractPricesProvider {

	private static final String BASE_URL="https://en.play-in.com/";

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

	private String getPage(String name) throws IOException
	{
		String autocomplete = BASE_URL+"/api/autocompletion.php?search="+URLTools.encode(name);
		Document ret = URLTools.extractAsHtml(autocomplete);
		return BASE_URL+ret.select("a").first().attr("href");

	}

	@Override
	public List<MTGPrice> getLocalePrice(MTGCard card) throws IOException {

		List<MTGPrice> list = new ArrayList<>();

		String page = getPage(card.getName());
		logger.info("{} looking for prices {}",getName(),page);

		try {
			Document doc = URLTools.extractAsHtml(page);
			Elements els = doc.select("div.filterElement");
			var lang = "";
			var set = "";
			for (Element e : els) {
				var mp = new MTGPrice();

				if(!e.select("img.langue_big").first().attr("alt").isEmpty())
					lang=e.select("img.langue_big").first().attr("alt");


				if(!e.getElementsByClass("name_ext").text().isEmpty())
					set=e.getElementsByClass("name_ext").text();



				mp.setMagicCard(card);
				mp.setLanguage(lang);
				mp.setQuality(e.getElementsByClass("etat").html());
				mp.setValue(Double.parseDouble(clean(e.select("div.prix").text())));
				mp.setCurrency("EUR");
				mp.setCountry(Locale.FRANCE.getDisplayCountry(MTGControler.getInstance().getLocale()));
				mp.setSite(getName());
				mp.setSellerUrl(page);
				mp.setUrl(page);
				mp.setSeller(set);
				mp.setFoil(e.attr("data-foil").equals("O"));

				if(mp.getSeller().toLowerCase().startsWith(card.getEdition().getSet().toLowerCase()))
					list.add(mp);
			}
			logger.info("{} found {} offers",getName(),list.size());

			return list;
		} catch (Exception e) {
			logger.trace("Error loading price for {}",page, e);
			logger.info("{} found no item : {}",getName(),e.getMessage());
			return list;
		}
	}

	private String clean(String html) {
		return StringEscapeUtils.escapeHtml3(html).replace(",", ".").replace(" ", "").replace("€", "");
	}

	@Override
	public String getName() {
		return "PlayIn";
	}

	@Override
	public String getVersion() {
		return "1.5";
	}

}
