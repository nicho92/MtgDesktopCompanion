package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGPrice;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.MTGControler;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.tools.UITools;

public class BigOrBitCardsPricer extends AbstractPricesProvider {

	@Override
	public String getName() {
		return "BigOrBitCards";
	}


	@Override
	protected List<MTGPrice> getLocalePrice(MTGCard card) throws IOException {


		var extra="";
		var ret = new ArrayList<MTGPrice>();
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
											 .get()
											 .url("https://www.bigorbitcards.co.uk/magic-the-gathering/search/"+UITools.replaceSpecialCharacters(card.getName()+ extra, "+"))
											 .addContent("resultsPerPage","96")
											 .setClient(URLTools.newClient())
											 .toHtml()
											 .select("div.products article div.product-desc");

		doc.forEach(e->{
			
			var title = e.select("h2.product-title a");
			var rowDetail  = e.select("span.product-row").first();

			if( title.text().equalsIgnoreCase(card.getName()) &&  !rowDetail.select("span.product-stock").text().equals("0 in Stock") && e.select("p").text().toLowerCase().indexOf(card.getEdition().getSet().toLowerCase())>-1)
			{
			  var mp = new MTGPrice();
					mp.setSeller(getName());
					mp.setSite(getName());
					mp.setCurrency(Currency.getInstance(Locale.UK));
					mp.setCountry(Locale.UK.getDisplayCountry(MTGControler.getInstance().getLocale()));
					mp.setLanguage(title.text().contains("Japanese")?"Japanese":"English");
					mp.setCardData(card);
					mp.setFoil(title.text().contains("(Foil)"));
					mp.setUrl(title.attr("href"));
					mp.setSellerUrl(mp.getUrl());
					mp.setQuality(aliases.getReversedConditionFor(this, rowDetail.select("span.product-name").text(), EnumCondition.NEAR_MINT));
					mp.setValue(UITools.parseDouble(rowDetail.select("span.product-price").text()));
					notify(mp);
					ret.add(mp);
			}
			
		});

		logger.info("{} found {} items ",getName(),ret.size());
		return ret;
	}

}
