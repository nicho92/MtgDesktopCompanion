package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.api.manapool.model.enums.EnumFinish;
import org.api.manapool.services.ManaPoolAPIService;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGPrice;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.MTGControler;

public class ManaPoolPricer extends AbstractPricesProvider {

	@Override
	public String getName() {
		return "ManaPool";
	}

	@Override
	protected List<MTGPrice> getLocalePrice(MTGCard card) throws IOException {
		
		var poolservice = new ManaPoolAPIService(getAuthenticator().get("EMAIL"),getAuthenticator().get("TOKEN"));
		var ret = new ArrayList<MTGPrice>();
		
		poolservice.listVariantsPrices().stream().filter(pv->pv.getScryfallId().equals(card.getScryfallId()) && pv.getAvailable()>0).forEach(pv->{
			var mp = new MTGPrice();
			mp.setCardData(card);
			mp.setCurrency("USD");
			mp.setFoil(pv.getFinishId()==EnumFinish.FO);
			mp.setSite(getName());
			mp.setUrl(pv.getUrl()+"?referrer_code=MP-unt564tzvR24HgRRfJk5EU");
			mp.setValue(pv.getPrice());
			mp.setLanguage(pv.getLanguageId().getLabel());
			mp.setCountry(Locale.US.getDisplayCountry(MTGControler.getInstance().getLocale()));
			mp.setQuality(aliases.getReversedConditionFor(this, pv.getConditionId().name(),EnumCondition.NEAR_MINT));
			mp.setQty(pv.getAvailable());
			ret.add(mp);
		});
		
		return ret;
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
