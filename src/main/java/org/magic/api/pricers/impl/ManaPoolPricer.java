package org.magic.api.pricers.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.api.manapool.model.PriceVariation;
import org.api.manapool.model.enums.EnumFinish;
import org.api.manapool.services.ManaPoolAPIService;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGPrice;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.tools.FileTools;

public class ManaPoolPricer extends AbstractPricesProvider {

	@Override
	public String getName() {
		return "ManaPool";
	}
	
	private List<PriceVariation> poolResults;

	
	public ManaPoolPricer() {
		poolResults = new ArrayList<>();
	}
	
	@Override
	protected List<MTGPrice> getLocalePrice(MTGCard card) throws IOException {
		
		var poolservice = new ManaPoolAPIService(getAuthenticator().get("EMAIL"),getAuthenticator().get("TOKEN"));
		var ret = new ArrayList<MTGPrice>();
		
		if(poolResults.isEmpty())
		{
			logger.info("cache  is empty or out of date. Will refresh it");
			poolResults.addAll(poolservice.listVariantsPrices());
		}
		poolResults.stream().filter(pv->pv.getScryfallId().equals(card.getScryfallId()) && pv.getAvailable()>0).forEach(pv->{
				var mp = new MTGPrice();
				mp.setCardData(card);
				mp.setCurrency("USD");
				mp.setFoil(pv.getFinishId()==EnumFinish.FO);
				mp.setSeller(getName());
				
				mp.setSite(getName());
				mp.setUrl(pv.getUrl()+"?referrer_code=MP-unt564tzvR24HgRRfJk5EU&conditions="+pv.getConditionId().name()+ (mp.isFoil()?"&finish=foil":""));
				mp.setSellerUrl(mp.getUrl());
				mp.setValue(pv.getPrice());
				mp.setLanguage(pv.getLanguageId().getLabel());
				mp.setCountry(Locale.US.getDisplayCountry(MTGControler.getInstance().getLocale()));
				mp.setQuality(aliases.getReversedConditionFor(this, pv.getConditionId().name(),org.magic.api.beans.enums.EnumCondition.NEAR_MINT));
				mp.setQty(pv.getAvailable());
				ret.add(mp);
			});
		return ret;
	}

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

}
