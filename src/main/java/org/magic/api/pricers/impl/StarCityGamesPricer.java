package org.magic.api.pricers.impl;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;

public class StarCityGamesPricer extends AbstractPricesProvider {

	NumberFormat format = NumberFormat.getCurrencyInstance();

	@Override
	public List<MagicPrice> getLocalePrice(MagicCard card) throws IOException {
		List<MagicPrice> ret = new ArrayList<>();

		String cardName = card.getName();
		RequestBuilder build = RequestBuilder.build().setClient(URLTools.newClient()).get().url("https://starcitygames.com/search/?card_name="+cardName);
		Document page = build.toHtml();
		Elements divs = page.select("div.hawk-results-item");
		for(Element div : divs)
		{

			try {
					logger.debug(div);
			}
			catch(Exception e)
			{
				logger.error("{} has error : ",getName(),e);
			}

		}
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
