package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGPrice;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.tools.CryptoUtils;

public class EbayPricer extends AbstractPricesProvider {

	private static final String CCG_CATEG_ID = "CCG_CATEG_ID";


	@Override
	public List<MTGPrice> getLocalePrice(MTGCard card) throws IOException {
		var prices = new ArrayList<MTGPrice>();
		var clientId=getAuthenticator().get("CLIENT_ID");
		var clientSecret=getAuthenticator().get("CLIENT_SECRET");
		var domain ="https://api.ebay.com";

		var token = RequestBuilder.build().setClient(URLTools.newClient())
				.url(domain+"/identity/v1/oauth2/token")
				.addHeader(URLTools.CONTENT_TYPE,"application/x-www-form-urlencoded")
				.addHeader(URLTools.AUTHORIZATION, "Basic " + CryptoUtils.toBase64( (clientId+":"+clientSecret).getBytes()))
				.addContent("grant_type", "client_credentials")
				.addContent("scope",domain+"/oauth/api_scope")
				.post()
				.toJson().getAsJsonObject().get("access_token").getAsString();
		
		var query = RequestBuilder.build().setClient(URLTools.newClient())
						.url(domain+"/buy/browse/v1/item_summary/search")
						.addHeader("X-EBAY-C-MARKETPLACE-ID",getString("EBAY_MARKETPLACE"))
						.addHeader(URLTools.AUTHORIZATION, "bearer " + token)
						.addContent("q", card.getName() + (getBoolean("FILTER_SET")?" " +card.getEdition().getSet():""));
						
						if(!getString(CCG_CATEG_ID).isEmpty())
							query = query.addContent("category_ids",getString(CCG_CATEG_ID));
				
			var result = query.get().toJson().getAsJsonObject();
		
			
		logger.debug("result : {}",result);
		
		if(result.get("total").getAsInt()>0) {
			result.get("itemSummaries").getAsJsonArray().forEach(je->{
					var obj = je.getAsJsonObject();	
					
					var price = new MTGPrice();
						 price.setValue(obj.get("price").getAsJsonObject().get("value").getAsDouble());
						 price.setCurrency(obj.get("price").getAsJsonObject().get("currency").getAsString());
						 price.setCardData(card);
						 price.setUrl(obj.get("itemWebUrl").getAsString());
						 price.setSeller(obj.get("seller").getAsJsonObject().get("username").getAsString());
						 price.setSite(getName());
						 price.setCountry(obj.get("itemLocation").getAsJsonObject().get("country").getAsString());
						 price.setSellerUrl("https://www.ebay.com/usr/"+price.getSeller());
						 price.setFoil(obj.get("title").toString().toLowerCase().contains("foil"));
						 prices.add(price);
			});
		}
		
		
		logger.info("{} found {} offers",getName(),prices.size());

		return prices;
	}

	@Override
	public String getName() {
		return "Ebay";
	}


	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("MAX", MTGProperty.newIntegerProperty("10","max results to return",5,-1),
							"EBAY_MARKETPLACE", new MTGProperty("EBAY_US","choose country market place. Type EBAY_XXX where is is the country code","EBAY_AT","EBAY_AU","EBAY_BE","EBAY_CA","EBAY_CH","EBAY_DE","EBAY_ES","EBAY_FR","EBAY_GB","EBAY_HK","EBAY_IE","EBAY_IT","EBAY_NL","EBAY_PL","EBAY_SG","EBAY_US"),
							"FIXEDPRICE_ONLY",MTGProperty.newBooleanProperty("false","chose if you want to filter to fixed price items"),
							CCG_CATEG_ID,MTGProperty.newIntegerProperty("183454","The id of TCG ebay category",-1,-1),
							"FILTER_SET", MTGProperty.newBooleanProperty("true", "append the set name to the query")  );

	}

	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of("CLIENT_ID","CLIENT_SECRET");

	}


	@Override
	public String getVersion() {
		return "1.19.7";
	}

}
