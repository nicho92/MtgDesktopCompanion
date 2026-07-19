package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.api.cardnexus.configuration.NexusConfig;
import org.api.cardnexus.model.enums.EnumFinishes;
import org.api.cardnexus.model.enums.EnumMarketPlace;
import org.api.cardnexus.model.requests.MarketListRequest;
import org.api.cardnexus.services.ProductsService;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGPrice;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;

public class CardNexusPricer extends AbstractPricesProvider{

    @Override
    public List<String> listAuthenticationAttributes() {
       return List.of("CARDNEXUS_API_KEY");
    }
    
    
    @Override
    public String getName() {
	return "CardNexus";
    }

    @Override
    protected List<MTGPrice> getLocalePrice(MTGCard card) throws IOException {
	NexusConfig.setToken(getAuthenticator().get("CARDNEXUS_API_KEY"));
	var ret = new ArrayList<MTGPrice>();
	var service = new ProductsService();
	var ids = service.resolveProductsId(EnumMarketPlace.cardmarket, List.of(card.getMkmId()));
	var id = ids.get(card.getMkmId());
	var product = service.getProductById(id);
	
	var req = MarketListRequest.create().setProductId(id);
	
	service.listMarketListing(req).forEach(p->{
	    		  var mp = new MTGPrice();
				mp.setCardData(card);
				mp.setSite(getName());
				mp.setFoil(p.finish()==EnumFinishes.Foil);
				mp.setQty(p.quantity());
				mp.setCurrency(p.price().currency());
				mp.setValue(p.price().amount());
				mp.setSeller(p.seller().username());
				mp.setLanguage(p.language());
				mp.setQuality(aliases.getReversedConditionFor(this, p.condition().getLabel(), EnumCondition.NEAR_MINT));
				mp.setUrl(product.urlProduct());
				mp.setCountry(p.seller().country());
				mp.setUrl(p.seller().urlProfilePage());
				ret.add(mp);
		
		});
	
	return ret;
    }
    
    
    @Override
    public String getVersion() {
        return NexusConfig.API_VERSION;
    }

}
