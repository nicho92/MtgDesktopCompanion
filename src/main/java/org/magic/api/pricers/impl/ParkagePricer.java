package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicPrice;
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
	
	public static void main(String[] args) throws IOException {
		
		var mc = new MagicCard();
		mc.setName("Liliana of the veil");
		
		new ParkagePricer().getLocalePrice(mc);
	}

	@Override
	protected List<MagicPrice> getLocalePrice(MagicCard card) throws IOException {

		ArrayList<MagicPrice> ret= new ArrayList<>();

		
		var url = "https://search.luciole.tech/api/search?text="+URLTools.encode(card.getName())+"&namespace=en&_ref=2&title=undefined&description=undefined&list=true&count=true&limit=25&offset=0&preorder=&isnew=&lang=&with_quantity=false&isDiscount=false&user_hash=";
		
		
		URLTools.extractAsJson(url).getAsJsonObject().get("list").getAsJsonArray().forEach(je->{
				var jo = je.getAsJsonObject();
			
			var mp = new MagicPrice();
					mp.setCountry(Locale.FRANCE.getDisplayCountry(MTGControler.getInstance().getLocale()));
					mp.setMagicCard(card);
					mp.setCurrency("EUR");
					mp.setSite(getName());
					mp.setLanguage(jo.get("lang").getAsString());
					mp.setFoil(jo.get("is_foil").getAsBoolean());
					mp.setQty(jo.get("stock").getAsInt());
					mp.setValue(jo.get("price").getAsDouble());
					mp.setQuality(jo.get("state").getAsString());
					mp.setUrl(URL_BASE+"/en/"+jo.get("id").getAsInt()+"-");
					mp.setSeller(getName());

					if(mp.getQty()>0)
						ret.add(mp);
	
		});
		
		logger.info("{} found {} offers",getName(),ret.size());

		return ret;
	}
}
