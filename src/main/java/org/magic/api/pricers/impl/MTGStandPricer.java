package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.MTGControler;
import org.magic.tools.MTG;
import org.magic.tools.URLTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class MTGStandPricer extends AbstractPricesProvider {

	
	private static final String BASE_URL="https://www.mtgstand.com/";
	
	@Override
	public String getName() {
		return "MTGStand";
	}

	@Override
	protected List<MagicPrice> getLocalePrice(MagicCard card) throws IOException {
		String url=BASE_URL+"/api/"+getString("TOKEN")+"/getseller/"+card.getScryfallId()+"/"+MTGControler.getInstance().getCurrencyService().getCurrentCurrency().getCurrencyCode();
		logger.debug(getName() +" looking for prices at " + url);
		
		List<MagicPrice> ret = new ArrayList<>();
		
		JsonArray arr = URLTools.extractJson(url).getAsJsonArray();
		
		if(arr.size()<=0)
		{
			return ret;
		}
		
		for(JsonElement el : arr)
		{
			MagicPrice p = new MagicPrice();
			p.setCurrency(MTGControler.getInstance().getCurrencyService().getCurrentCurrency());
			p.setMagicCard(card);
			p.setSeller(el.getAsJsonObject().get("username").getAsString());
			p.setSellerUrl(el.getAsJsonObject().get("user_market_stand_url").getAsString());
			p.setUrl(el.getAsJsonObject().get("user_market_stand_url").getAsString());
			p.setValue(el.getAsJsonObject().get("SellingPrice"+MTGControler.getInstance().getCurrencyService().getCurrentCurrency().getCurrencyCode()).getAsDouble());
			p.setQuality(el.getAsJsonObject().get("condition").getAsString());
			p.setLanguage(el.getAsJsonObject().get("language").getAsString());
			p.setQty(el.getAsJsonObject().get("quantity").getAsInt());
			p.setSite(getName());
			
			ret.add(p);
		}
		
		return ret;
	}

	
	@Override
	public void initDefault() {
		setProperty("TOKEN", "");
	}
	

}
