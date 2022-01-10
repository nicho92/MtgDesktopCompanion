package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.api.cardtrader.modele.Address;
import org.api.cardtrader.modele.MarketProduct;
import org.api.cardtrader.services.CardTraderConstants;
import org.api.cardtrader.services.CardTraderService;
import org.api.cardtrader.tools.URLCallInfo;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicPrice;
import org.magic.api.beans.audit.NetworkInfo;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.MTGControler;
import org.magic.services.TechnicalServiceManager;
import org.magic.tools.Chrono;

public class CardTraderPricer extends AbstractPricesProvider {

	private static final String COUNTRY_FILTER = "COUNTRY_FILTER";
	private CardTraderService service;
	
	@Override
	public Map<String, String> getDefaultAttributes() {
		var m = super.getDefaultAttributes();
		
		m.put("AUTOMATIC_ADD_CART", "false");
		m.put(COUNTRY_FILTER, "IT,FR");
		
		return m;
	}
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
		
	@Override
	public String getVersion() {
		return CardTraderConstants.CARDTRADER_JAVA_API_VERSION;
	}

	@Override
	public String getName() {
		return "CardTrader";
	}
	
	@Override
	public void alertDetected(List<MagicPrice> p) {
		init();
		if(getBoolean("AUTOMATIC_ADD_CART")) {
		
				for(var price : p)
				{
					var mk = (MarketProduct) price.getShopItem();
					var c = MTGControler.getInstance().getWebConfig().getContact();
					
					var addr = new Address();
					addr.setName(c.getName() + " "+ c.getLastName());
					addr.setCity(c.getCity());
					addr.setCountry(c.getCountry());
					addr.setStreet(c.getAddress());
					addr.setZip(c.getZipCode());
					
					try {
						service.addProductToCart(mk, true, 1, addr, addr);
					} catch (Exception e) {
						logger.error("Error adding product to cart",e);
					}
					
				}
		}
	}
	

	@Override
	protected List<MagicPrice> getLocalePrice(MagicCard card) throws IOException {
		
		init();
		
		var ret = new ArrayList<MagicPrice>();
		
		var set = service.getExpansionByCode(card.getCurrentSet().getId());
		
		var bps = service.listBluePrints(service.getCategoryById(1), card.getName(),set);
		
		if(bps.isEmpty())
		{
			logger.info(getName() + " found nothing");
			return ret;
		}
		
		
		var bp = bps.get(0);
		
		Chrono c = new Chrono();
		c.start();
		logger.debug("Begin searching " + bp);
		service.listMarketProductByBluePrint(bp).forEach(marketItem->{
			
			if(ArrayUtils.contains(getArray(COUNTRY_FILTER),marketItem.getSeller().getCountryCode()) || getString(COUNTRY_FILTER).isEmpty())
			{
			
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
			}
		});
		
		
		logger.info(getName() + " found " + ret.size() + " items in " + c.stop() +" ms");
		return ret;
		
	}

	private void init() {
		if(service==null)
			service = new CardTraderService(getAuthenticator().get("TOKEN"));
		
			service.setListener((URLCallInfo callInfo)-> {
				
				var ni = new NetworkInfo();
				ni.setStart(callInfo.getStart());
				ni.setEnd(callInfo.getEnd());
				ni.setDuration(callInfo.getDuration());
				ni.setReponse(callInfo.getResponse());
				ni.setRequest(callInfo.getRequest());
				
				TechnicalServiceManager.inst().store(ni);
					
				
			});
		
	}
	
	
}
