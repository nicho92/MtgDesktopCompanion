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
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGPrice;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.beans.technical.audit.NetworkInfo;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.api.interfaces.abstracts.AbstractTechnicalServiceManager;
import org.magic.services.MTGControler;
import org.magic.services.tools.Chrono;

public class CardTraderPricer extends AbstractPricesProvider {

	private static final String COUNTRY_FILTER = "COUNTRY_FILTER";
	private CardTraderService service;

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var m = super.getDefaultAttributes();

		m.put("AUTOMATIC_ADD_CART", MTGProperty.newBooleanProperty("false","set to true if you want add finded item to your online cart"));
		m.put(COUNTRY_FILTER, new MTGProperty("", "Set seller's country filter in card search. Separated by comma","IT","FR","EN","SK","ES","BE","AT","GR","SE","CH"));

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
	public void alertDetected(List<MTGPrice> p) {
		init();
		if(getBoolean("AUTOMATIC_ADD_CART")) {

				for(var price : p)
				{
					var mk = (MarketProduct) price.getShopItem();
					var c = MTGControler.getInstance().getWebshopService().getWebConfig().getContact();

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
	protected List<MTGPrice> getLocalePrice(MTGCard card) throws IOException {

		init();

		var ret = new ArrayList<MTGPrice>();

		var set = service.getExpansionByCode(card.getEdition().getId());

		var bps = service.listBluePrints(card.getName(),set);

		if(bps.isEmpty())
		{
			logger.info("{} found nothing",getName());
			return ret;
		}


		var bp = service.getBluePrintById(bps.get(0).getId());
		Chrono c = new Chrono();
		c.start();
		logger.debug("Begin searching {}",bp);
		service.listMarketProductByBluePrint(bp).forEach(marketItem->{

			if(ArrayUtils.contains(getArray(COUNTRY_FILTER),marketItem.getSeller().getCountryCode()) || getString(COUNTRY_FILTER).isEmpty())
			{

				
					
					var mp = new MTGPrice();
					mp.setCountry(marketItem.getSeller().getCountryCode());
					mp.setCurrency(marketItem.getPrice().getCurrency());
					mp.setLanguage(marketItem.getLanguage());
					mp.setFoil(marketItem.isFoil());
					mp.setValue(marketItem.getPrice().getValue());
					mp.setCardData(card);
					mp.setSeller(marketItem.getSeller().getUsername());
					mp.setSite(getName());
					mp.setQuality(aliases.getReversedConditionFor(this, marketItem.getCondition().name(), EnumCondition.NEAR_MINT));
					mp.setSellerUrl(CardTraderConstants.CARDTRADER_WEBSITE_URI+"/users/"+marketItem.getSeller().getUsername());
					mp.setUrl(CardTraderConstants.CARDTRADER_WEBSITE_URI+"/cards/"+bp.getSlug()+"?share_code="+CardTraderConstants.SHARE_CODE);
					mp.setShopItem(marketItem);
					ret.add(mp);
			}
		});


		logger.info("{} found {} items in {}ms",getName(),ret.size(),c.stop());
		return ret;

	}

	private void init() {
		if(service==null)
			service = new CardTraderService(getAuthenticator().get("TOKEN"));

			service.setListener((URLCallInfo callInfo)-> {

				var ni = new NetworkInfo();
				ni.setStart(callInfo.getStart());
				ni.setEnd(callInfo.getEnd());
				ni.setReponse(callInfo.getResponse());
				ni.setRequest(callInfo.getRequest());

				AbstractTechnicalServiceManager.inst().store(ni);


			});

	}


}
