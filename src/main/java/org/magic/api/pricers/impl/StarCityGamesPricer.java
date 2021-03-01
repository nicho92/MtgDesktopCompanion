package org.magic.api.pricers.impl;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;
import org.magic.tools.RequestBuilder;
import org.magic.tools.RequestBuilder.METHOD;
import org.magic.tools.URLTools;

public class StarCityGamesPricer extends AbstractMagicPricesProvider {

	NumberFormat format = NumberFormat.getCurrencyInstance();
	
	@Override
	public List<MagicPrice> getLocalePrice(MagicCard card) throws IOException {
		List<MagicPrice> ret = new ArrayList<>();
		
		String cardName = URLTools.encode("\""+card.getName()+"\"");
		RequestBuilder build = RequestBuilder.build().setClient(URLTools.newClient()).method(METHOD.GET).url("https://starcitygames.com/search/?card_name="+cardName);
		Document page = build.toHtml();
		Elements divs = page.select("div.hawk-results-item");

		System.out.println(page);
		
		
		for(Element div : divs)
		{
			
			try {
			
					System.out.println(div);
				
				
			}
			catch(Exception e)
			{
				logger.error(getName() +" has error : " + e);
			}
				
		}
		logger.debug(getName() + " found " + ret.size() + " items");
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
	public void initDefault() {
		setProperty("NB_PAGE", "1");

	}

	@Override
	public String getVersion() {
		return "0.5";
	}
	

}
