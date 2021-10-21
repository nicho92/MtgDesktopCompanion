package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.api.cardtrader.services.CardTraderConstants;
import org.api.cardtrader.services.CardTraderService;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;

public class CardTraderPricer extends AbstractPricesProvider {

	private CardTraderService service;
	
	
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
		
	@Override
	public String getVersion() {
		return CardTraderConstants.CARDTRADER_API_VERSION;
	}

	@Override
	public String getName() {
		return "CardTrader";
	}
	
	@Override
	public void alertDetected(List<MagicPrice> p) {
		
		
	}
	

	@Override
	protected List<MagicPrice> getLocalePrice(MagicCard card) throws IOException {
		if(service==null)
			service = new CardTraderService(getAuthenticator().get("TOKEN"));
		
		
		var ret = new ArrayList<MagicPrice>();
		
		var set = service.getExpansionByCode(card.getCurrentSet().getId());
		
		var bps = service.listBluePrints(service.getCategoryById(1), card.getName(),set);
		
		if(bps.isEmpty())
		{
			logger.info(getName() + " found nothing");
			return ret;
		}
		
		
		var bp = bps.get(0);
		service.listMarketProduct(bp).forEach(marketItem->{
			var mp = new MagicPrice();
			mp.setCountry(marketItem.getSeller().getCountryCode());
			mp.setCurrency(marketItem.getPrice().getCurrency());
			mp.setLanguage(marketItem.getLanguage());
			mp.setFoil(marketItem.isFoil());
			mp.setValue(marketItem.getPrice().getValue());
			mp.setMagicCard(card);
			mp.setSeller(marketItem.getSeller().getUsername());
			mp.setSite(getName());
			mp.setQuality(marketItem.getCondition().getValue());
			mp.setSellerUrl(CardTraderConstants.CARDTRADER_WEBSITE_URI+"/users/"+marketItem.getSeller().getUsername());
			mp.setUrl(CardTraderConstants.CARDTRADER_WEBSITE_URI+"/cards/"+bp.getSlug()+"?share_code="+CardTraderConstants.SHARE_CODE);
			mp.setShopItem(marketItem);
			ret.add(mp);
		});
		
		
		logger.info(getName() + " found " + ret.size() + " items");
		return ret;
		
	}
	
	
}
