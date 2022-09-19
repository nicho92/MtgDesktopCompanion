package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import org.apache.http.entity.StringEntity;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.URLTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class ChannelFireballPricer extends AbstractPricesProvider {

	private MTGHttpClient c = new MTGHttpClient();

	@Override
	public List<MagicPrice> getLocalePrice(MagicCard card) throws IOException {
		var list = new ArrayList<MagicPrice>();
		
		String idProduct=findIdProduct(card);
		
		
		if(idProduct!=null)
		{
			JsonArray arts = findArticles(idProduct);
			for(JsonElement el : arts)
			{
				var obj = el.getAsJsonObject();
				var price = new MagicPrice();
					price.setSite(getName());
					price.setCurrency(Currency.getInstance("USD"));
					price.setMagicCard(card);
					price.setShopItem(obj);
					price.setUrl("https://channelfireball.com/product/"+idProduct);
					
					price.setSellerUrl("https://channelfireball.com/store/"+obj.get("account").getAsJsonObject().get("sellerDetails").getAsJsonObject().get("slug").getAsString());
					price.setSeller(obj.get("account").getAsJsonObject().get("sellerDetails").getAsJsonObject().get("storeName").getAsString());
					price.setFoil(obj.get("sku").getAsJsonObject().get("printing").getAsString().equalsIgnoreCase("FO"));
					price.setLanguage(obj.get("sku").getAsJsonObject().get("language").getAsString());
					price.setQuality(obj.get("sku").getAsJsonObject().get("condition").getAsString());
					price.setValue(obj.get("price").getAsJsonObject().get("price").getAsDouble());
					price.setCountry(obj.get("shipping").getAsJsonObject().get("countries").getAsJsonArray().toString());
				notify(price);	
				list.add(price);
				
			}
			
		}
		
		logger.info("{} found {} offers",getName(),list.size());
		return list;
	}

	private JsonArray findArticles(String idProduct) throws IOException {
		var jsonFindId="{"
				+ "\"operationName\": \"getProductById\","
				+ "\"variables\": {"
				+ "\"id\": \""+idProduct+"\","
				+ "\"page\": 0,"
				+ "\"size\": "+getString("MAX_RESULTS")+","
				+ "\"filter\": {"
				+ "\"listing\": {"
				+ "\"terms\": {"
				+ "\"shipping.countries.keyword\": null"
				+ "}"
				+ "}"
				+ "},"
				+ "\"sort\": [\"price.price,asc\"]"
				+ "},"
				+ "\"query\": \"query getProductById($id: String!, $page: Int!, $size: Int!, $filter: FilterInput!, $sort: [String!]) {  getProductById(id: $id, page: $page, size: $size, filter: $filter, sort: $sort) {    id    title    listings {      totalHits      hits {        id        account {          id          sellerDetails {            storeName: store_name           slug            __typename          }          __typename        }        sku {          id          productId: product_id          condition          language          printing          __typename        }        price {          price          __typename        }        inventory {          quantity          __typename        }        shipping {          countries          __typename        }        __typename      }      __typename    }    __typename  }}\""
				+ "}";
		
		
		var ret = c.doPost("https://api.channelfireball.com/api/v1/graphql", new StringEntity(jsonFindId), new HashMap<>());
		var jresults = URLTools.toJson(ret.getEntity().getContent()).getAsJsonObject().get("data").getAsJsonObject().get("getProductById").getAsJsonObject().get("listings").getAsJsonObject();
		return jresults.get("hits").getAsJsonArray();
	}

	private String findIdProduct(MagicCard card) throws IOException {
		var jsonSearch ="{"
				+ "\"operationName\": \"ProductAndListingSearch\","
				+ "\"variables\": {"
				+ "\"filter\": {"
				+ "\"product\": {"
				+ "\"terms\": {"
				+ "\"tags.keyword\": [\"Magic: The Gathering\"]"
				+ "},"
				+ "\"title\": \""+card.getName()+(card.getFlavorName()!=null?" - "+card.getFlavorName():"") +"\""
				+ "}"
				+ "},"
				+ "\"page\": 0,"
				+ "\"size\": 20"
				+ "},"
				+ "\"query\": \"query ProductAndListingSearch($filter: FilterInput, $page: Int!, $size: Int!, $sort: [String!]) {  simpleSearch(filter: $filter, page: $page, size: $size, sort: $sort) {    totalHits    hits {      id      title      tags      attributes {        setName   setCode   imageSrc   attribute   cleanName   productId    __typename      }      listings {        aggs        __typename      }      __typename    }    __typename  }}\""
				+ "}";
		
		var ret = c.doPost("https://api.channelfireball.com/api/v1/graphql", new StringEntity(jsonSearch), new HashMap<>());
		var arrResults = URLTools.toJson(ret.getEntity().getContent()).getAsJsonObject().get("data").getAsJsonObject().get("simpleSearch").getAsJsonObject().get("hits").getAsJsonArray();
		
		
		var list = StreamSupport.stream(arrResults.spliterator(), true).filter(je->{
			var obj = je.getAsJsonObject();
			var testSet = true;
			try {
				testSet = obj.get("attributes").getAsJsonObject().get("setCode").getAsString().equalsIgnoreCase(card.getCurrentSet().getId());
			}
			catch(UnsupportedOperationException ex)
			{
				testSet=false;
			}
			var testQty = obj.get("listings").getAsJsonObject().get("aggs").getAsJsonObject().get("Inventory").getAsJsonObject().get("totalHits").getAsInt()>0;
			return testSet && testQty;
		}).toList();
		
		if(card.isShowCase() && card.getFlavorName()==null)
			list = list.stream().filter(je->je.getAsJsonObject().get("title").getAsString().contains("(Showcase")).toList();
		else if(card.isBorderLess() && card.getFlavorName()==null)
			list = list.stream().filter(je->je.getAsJsonObject().get("title").getAsString().contains("(Borderless)")).toList();
		
		if(list.isEmpty())
			return null;
	
		
		return list.get(0).getAsJsonObject().get("id").getAsString();
	}

	@Override
	public String getName() {
		return "Channel Fireball";
	}
	
	@Override
	public String getVersion() {
		return "3.0";
	}
	
	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of("MAX_RESULTS","10");
	}
	

}
