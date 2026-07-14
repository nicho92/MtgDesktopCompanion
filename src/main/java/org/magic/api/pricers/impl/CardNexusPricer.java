package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.api.cardnexus.configuration.NexusConfig;
import org.api.cardnexus.model.CardProduct;
import org.api.cardnexus.model.enums.EnumFinishes;
import org.api.cardnexus.model.requests.SearchProductRequest;
import org.api.cardnexus.services.ProductsService;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGPrice;
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
	
	var service = new ProductsService();
	var req = new SearchProductRequest().setCardmarketId(List.of(card.getMkmId())).setGame("mtg");
	var results = service.searchProduct(req);
	var ret = new ArrayList<MTGPrice>();
	
	results.forEach(p->{
		    var c = (CardProduct)p;
		    
		    for(var f : c.getFinishes())
		    {
				var mp = new MTGPrice();
				mp.setCardData(card);
				mp.setSite(getName());
				mp.setFoil(f==EnumFinishes.Foil);
				var n  = c.getPricesByFinish().get(f).getCardnexus();
				mp.setQty(n.getAvailableQuantity());
				mp.setCurrency(n.getLow().getCurrency());
				mp.setValue(n.getLow().getAmount());
				mp.setSeller(getName());
				
				ret.add(mp);
				
		    }
    		    
		    
		});
	
	return ret;
    }
    
    
    @Override
    public String getVersion() {
        return NexusConfig.API_VERSION;
    }

}
