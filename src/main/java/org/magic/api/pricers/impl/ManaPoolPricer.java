package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGPrice;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.providers.MTGJsonPricerProvider;
import org.magic.services.providers.MTGJsonPricerProvider.VENDOR;

public class ManaPoolPricer extends AbstractPricesProvider {

	@Override
	public String getName() {
		return "ManaPool";
	}

	@Override
	protected List<MTGPrice> getLocalePrice(MTGCard card) throws IOException {

		logger.debug("{} looking for prices for {} ",getName(),card);

		MTGJsonPricerProvider.getInstance().expirationDay(getInt("EXPIRE_FILE_DAYS"));
		
		
		return MTGJsonPricerProvider.getInstance().getPriceFor(card,VENDOR.MANAPOOL).stream().map(mp->{
			mp.setUrl("https://manapool.com/");
			return mp;
			
		}).toList();
	}

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("EXPIRE_FILE_DAYS",MTGProperty.newIntegerProperty("1","Number of day when the file will be updated",1,-1));
	}


}
