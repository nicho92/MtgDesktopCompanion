package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGPrice;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.network.RequestBuilder;

public class CardSpherePricer extends AbstractPricesProvider {

	@Override
	public String getName() {
		return "CardSphere";
	}

	@Override
	protected List<MTGPrice> getLocalePrice(MTGCard card) throws IOException {
		var ret = new ArrayList<MTGPrice>();
		
		var arr = RequestBuilder.build()
						.newClient()
						.url("https://www.multiversebridge.com/api/v1/cards/scryfall/"+card.getScryfallId())
						.get()
						.toJson().getAsJsonArray();
		
		
			arr.forEach(je->{
				
				
				try {
					var obj = je.getAsJsonObject();
					var p = new MTGPrice();
						 p.setUrl("https://www.cardsphere.com"+obj.get("url").getAsString());
						 p.setFoil(obj.get("is_foil").getAsBoolean());
						 p.setValue(obj.get("prices").getAsJsonObject().get("price").getAsDouble());
						 p.setCurrency(Currency.getInstance("USD"));
						 p.setCardData(card);
						 p.setQty(1);
						 p.setSite(getName());
						ret.add(p);
				}
				catch(Exception e)
				{
					logger.error("error parsing json {}",e.getMessage());
				}
			});
		
		
		return ret;
	}

}
