package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGPrice;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.MTGControler;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.tools.UITools;

import com.google.gson.JsonElement;

public class MTGStandPricer extends AbstractPricesProvider {


	private static final String BASE_URL="https://www.mtgstand.com/";

	@Override
	public String getName() {
		return "MTGStand";
	}
	
	@Override
	protected List<MTGPrice> getLocalePrice(MTGCard card) throws IOException {
		var c = URLTools.newClient();
		var cur = MTGControler.getInstance().getCurrencyService().getCurrentCurrency().getCurrencyCode();
		var j = RequestBuilder.build().setClient(c).get().url(BASE_URL+"/data-singlecard.php")
																			.addContent("job","getsinglecard")
																			.addContent("sid",card.getScryfallId())
																			.addContent("userCurrencyBuyer",cur)
																			.addContent("useridbuyer","ZmxNd2tjSzFiNzB4bWN3V2M2TmVWUT09")
																			.toJson().getAsJsonObject();
		logger.debug("{} looking for prices",getName());
		List<MTGPrice> ret = new ArrayList<>();
		var arr = j.get("data").getAsJsonArray();

		if(arr.size()<=0)
		{
			logger.info("{} found nothing",getName());
			return ret;
		}

		for(JsonElement el : arr)
		{
			var obj = el.getAsJsonObject();
			var p = new MTGPrice();
			var htCountry = URLTools.toHtml(obj.get("seller").getAsString()).select("img").attr("src");
			
			p.setCurrency(cur);
			p.setScryfallId(card);
			p.setSeller(URLTools.toHtml(obj.get("seller").getAsString()).text());
			p.setSite(getName());
			p.setSellerUrl(BASE_URL+"/"+p.getSeller());
			p.setUrl(BASE_URL+"card-sid-"+card.getScryfallId());
			p.setLanguage(URLTools.toHtml(obj.get("cardlanguage").getAsString()).text().replace("l:", ""));
			p.setQuality(URLTools.toHtml(obj.get("cond").getAsString()).select("span").text().replace("c:", ""));
			p.setValue(UITools.parseDouble(obj.get("price").getAsString().substring(0,obj.get("price").getAsString().indexOf(" "))));
			p.setQty(obj.get("forsale").getAsInt());
			p.setFoil(obj.get("foilq").getAsInt()>0);
			p.setCountry(htCountry.replace("images/languages/usercountries/", "").replace(".png", ""));
			ret.add(p);
		}
		logger.info("{} found {} items",getName(),ret.size());

		return ret;
	}

}
