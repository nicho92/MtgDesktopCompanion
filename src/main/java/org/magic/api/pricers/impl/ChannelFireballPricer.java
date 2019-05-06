package org.magic.api.pricers.impl;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;
import org.magic.services.MTGConstants;
import org.magic.tools.URLTools;

public class ChannelFireballPricer extends AbstractMagicPricesProvider {

	private static String baseUrl="https://store.channelfireball.com";
	
	
	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}


	@Override
	public List<MagicPrice> getLocalePrice(MagicEdition me, MagicCard card) throws IOException {
		ArrayList<MagicPrice> list = new ArrayList<>();
	
		Document root = URLTools.extractHtml(baseUrl+"/products/search?query="+ URLEncoder.encode(card.getName(), MTGConstants.DEFAULT_ENCODING.displayName()));
		
		Elements lis = root.select("ul.products li div.meta");
		
		
		lis.forEach(li->{
			
			if(!li.getElementsByTag("form").text().contains("Wishlist") && li.getElementsByTag("a").first().text().toLowerCase().startsWith(card.getName().toLowerCase())) 
			{
			
			MagicPrice p = new MagicPrice();
					   p.setCountry("USA");
					   p.setCurrency("USD");
					   p.setSite(getName());
					   p.setUrl(baseUrl+li.getElementsByTag("a").first().attr("href"));
					   p.setSeller(li.getElementsByTag("a").get(1).text());
					   p.setValue(Double.parseDouble(li.select("span[itemprop].price").first().text().replaceAll("\\$","").trim()));
					   p.setFoil(li.getElementsByTag("a").first().text().contains("- Foil"));
					   list.add(p);
			}
		});

		logger.info(getName() + " found " + list.size() + " item(s)");

		return list;
	}

	@Override
	public String getName() {
		return "Channel Fireball";
	}

}
