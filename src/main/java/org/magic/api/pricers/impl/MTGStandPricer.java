package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.MTGControler;
import org.magic.services.network.URLTools;

import com.google.gson.JsonElement;

public class MTGStandPricer extends AbstractPricesProvider {


	private static final String BASE_URL="https://www.mtgstand.com/";

	@Override
	public String getName() {
		return "MTGStand";
	}

	@Override
	protected List<MagicPrice> getLocalePrice(MagicCard card) throws IOException {


		String cur=MTGControler.getInstance().getCurrencyService().getCurrentCurrency().getCurrencyCode();

		String url=BASE_URL+"/api/"+getString("TOKEN")+"/getseller/"+card.getScryfallId()+"/"+cur;
		logger.debug("{} looking for prices at {}",getName(),url);

		List<MagicPrice> ret = new ArrayList<>();

		var arr = URLTools.extractAsJson(url).getAsJsonArray();

		if(arr.size()<=0)
		{
			return ret;
		}

		for(JsonElement el : arr)
		{
			var p = new MagicPrice();
			p.setCurrency(cur);
			p.setMagicCard(card);
			p.setSeller(el.getAsJsonObject().get("username").getAsString());
			p.setSellerUrl(el.getAsJsonObject().get("user_market_stand_url").getAsString());
			p.setUrl(el.getAsJsonObject().get("user_market_stand_url").getAsString());
			p.setQuality(el.getAsJsonObject().get("condition").getAsString());
			p.setLanguage(el.getAsJsonObject().get("language").getAsString());
			p.setQty(el.getAsJsonObject().get("quantity").getAsInt());
			p.setCountry(el.getAsJsonObject().get("user_country").getAsString());
			p.setSite(getName());

			if(!el.getAsJsonObject().get("SellingPrice"+cur).isJsonNull())
			{
				p.setValue(el.getAsJsonObject().get("SellingPrice"+cur).getAsDouble());
				p.setFoil(false);
			}
			else if(!el.getAsJsonObject().get("SellingPriceFoil"+cur).isJsonNull())
			{
				p.setValue(el.getAsJsonObject().get("SellingPriceFoil"+cur).getAsDouble());
				p.setFoil(true);
			}
			ret.add(p);
		}
		logger.info("{} found {} items",getName(),ret.size());

		return ret;
	}

	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of("TOKEN", "");
	}


}
