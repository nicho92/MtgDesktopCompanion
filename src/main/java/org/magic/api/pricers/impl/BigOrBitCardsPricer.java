package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import javax.print.attribute.DocAttribute;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicPrice;
import org.magic.api.beans.enums.EnumMarketType;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.MTGControler;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.RequestBuilder.METHOD;
import org.magic.services.network.URLTools;
import org.magic.services.tools.UITools;

public class BigOrBitCardsPricer extends AbstractPricesProvider {

	@Override
	public String getName() {
		return "BigOrBitCards";
	}


	 @Override
	public EnumMarketType getMarket() {
		 return EnumMarketType.EU_MARKET;
	}

	@Override
	protected List<MagicPrice> getLocalePrice(MagicCard card) throws IOException {


		var extra="";
		var ret = new ArrayList<MagicPrice>();
		if(card.isExtendedArt())
			extra=" (Extended Art)";
		else if(card.isShowCase())
			extra=" (Showcase)";
		else if(card.isBorderLess())
			extra=" (Borderless Art)";
		else if(card.isTimeshifted())
			extra=" (Retro Frame)";
		
		logger.info("{} looking for {} with extra={}",getName(),card,extra);

		var doc = RequestBuilder.build()
											 .method(METHOD.GET)
											 .url("https://www.bigorbitcards.co.uk/magic-the-gathering/search/"+UITools.replaceSpecialCharacters(card.getName()+ extra, "+"))
											 .addContent("resultsPerPage","96")
											 .setClient(URLTools.newClient())
											 .toHtml()
											 .select("div.products article div.product-desc");

		doc.forEach(e->{
			
			var title = e.select("h2.product-title a");
			var rowDetail  = e.select("span.product-row").first();

			if(!(rowDetail.select("span.product-stock").text().equals("0 in Stock")) /*&& e.select("p").text().toLowerCase().indexOf(card.getCurrentSet().getSet().toLowerCase())>-1*/)
			{
			
			  var mp = new MagicPrice();
					mp.setSeller(getName());
					mp.setSite(getName());
					mp.setCurrency(Currency.getInstance(Locale.UK));
					mp.setCountry(Locale.UK.getDisplayCountry(MTGControler.getInstance().getLocale()));
					mp.setLanguage(title.text().contains("Japanese")?"Japanese":"English");
					mp.setMagicCard(card);
					mp.setFoil(title.text().contains("(Foil)"));
					mp.setUrl(title.attr("href"));
					mp.setSellerUrl(mp.getUrl());
					mp.setQuality(rowDetail.select("span.product-name").text());
					mp.setValue(UITools.parseDouble(rowDetail.select("span.product-price").text()));
					notify(mp);
					ret.add(mp);
			}
			
		});

		logger.info("{} found {} items ",getName(),ret.size());
		return ret;
	}

}
