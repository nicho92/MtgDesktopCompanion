package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGPrice;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.MTGControler;
import org.magic.services.network.URLTools;

public class ParkagePricer extends AbstractPricesProvider {


	private static final String URL_BASE="https://www.parkage.com";
	
	@Override
	public String getName() {
		return "Parkage";
	}
	
	@Override
	public String getVersion() {
		return "2.0";
	}
	
	@Override
	protected List<MTGPrice> getLocalePrice(MTGCard card) throws IOException {

		ArrayList<MTGPrice> ret= new ArrayList<>();

		
		var url = "https://search.luciole.tech/api/search?text="+URLTools.encode(card.getName())+"&namespace=en&_ref=2&title=undefined&description=undefined&list=true&count=true&limit=25&offset=0&preorder=&isnew=&lang=&with_quantity=false&isDiscount=false&user_hash=";
		
		
		URLTools.extractAsJson(url).getAsJsonObject().get("list").getAsJsonArray().forEach(je->{
				var jo = je.getAsJsonObject();
				
				if(jo.get("image_url_1").getAsString().contains(card.getNumber()))
				{
				
					var mp = new MTGPrice();
					mp.setCountry(Locale.FRANCE.getDisplayCountry(MTGControler.getInstance().getLocale()));
					mp.setCardData(card);
					mp.setCurrency("EUR");
					mp.setSite(getName());
					mp.setLanguage(jo.get("lang").getAsString());
					mp.setFoil(jo.get("is_foil").getAsInt()==1);
					mp.setQty(jo.get("stock").getAsInt());
					mp.setValue(jo.get("price").getAsDouble());
					mp.setQuality(aliases.getReversedConditionFor(this, jo.get("state").getAsString(), EnumCondition.NEAR_MINT));
					mp.setUrl(URL_BASE+"/en/"+jo.get("id").getAsInt()+"-");
					mp.setSeller(getName());

					if(mp.getQty()>0)
						ret.add(mp);
				}
		});
		
		logger.info("{} found {} offers",getName(),ret.size());

		return ret;
	}
}
