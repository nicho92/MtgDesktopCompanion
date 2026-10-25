package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGPrice;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.MTGControler;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.tools.UITools;

public class MagicCorporationPricer extends AbstractPricesProvider {

	private static final String BASE_URL = "https://boutique.magiccorporation.com";

	@Override
	public String getName() {
		return "MagicCorporation";
	}
	
	
	public static void main(String[] args) throws IOException {
	    
	    var mc = new MTGCard();
	    mc.setName("Jace, the mind sculptor");
	    
	    
	    new MagicCorporationPricer().getLocalePrice(mc);
	}
	

	@Override
	protected List<MTGPrice> getLocalePrice(MTGCard card) throws IOException {

		String url = BASE_URL + "/cartes/recherche?q="+URLTools.encode(card.getName());
		List<MTGPrice> ret = new ArrayList<>();

		Document content = RequestBuilder.build().url(url).newClient().get().addContent("q", card.getName()).toHtml();

		
	
		var trs = content.select("table.w-full tbody tr");
		
		if (!trs.isEmpty()) 
		{
			for (Element tr : trs) 
			{
				var entries = tr.getElementsByTag("td").get(8).select("select[name=variant] option");
				if(!entries.isEmpty())
				    createPriceEntry(ret, tr, card,entries);
			}
		} else {
			logger.debug("{} found nothing", getName());
		}
		logger.info("{} found {} offers", getName(), ret.size());

		return ret;
	}


	private void createPriceEntry(List<MTGPrice> ret,Element tr, MTGCard card, Elements entries) {
	    
	    
	    for(var e : entries)
	    {

		    var mp = new MTGPrice();
			mp.setCardData(card);
			mp.setCountry(Locale.FRANCE.getDisplayCountry(MTGControler.getInstance().getLocale()));
			mp.setCurrency("EUR");
			mp.setSite(getName());				
			mp.setSeller(getName());
			mp.setUrl(tr.getElementsByTag("td").first().getElementsByTag("a").attr("href"));
			mp.setValue(UITools.parseDouble(e.text().split("·")[1]));
			mp.setLanguage(e.attr("value").contains("vf")? "French":"English");
			mp.setFoil(e.attr("value").contains("foil"));
			mp.setQty(Integer.parseInt(e.attr("data-max")));
			ret.add(mp);
	    }
	    
	    
		    
	}

}
