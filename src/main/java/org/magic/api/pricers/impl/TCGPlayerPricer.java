package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.groovy.util.Maps;
import org.apache.http.entity.StringEntity;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGPrice;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.MTGControler;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.URLTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class TCGPlayerPricer extends AbstractPricesProvider {

	private static final String RESULTS = "results";
	private MTGHttpClient c = URLTools.newClient();

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

	@Override
	public List<MTGPrice> getLocalePrice(MTGCard card) throws IOException {
		var list = new ArrayList<MTGPrice>();

		var idResults = card.getTcgPlayerId();
		if(idResults==null)
				idResults = parseIdFor(card);

		if(idResults==null)
		{
			logger.warn("{} found nothing",getName());
			return list;
		}

		JsonArray arr = parseResultsFor(idResults);
		logger.info("{} found {} results",getName(),arr.size());

		for(JsonElement e: arr)
		{
			var mp = new MTGPrice();
				mp.setUrl("https://www.tcgplayer.com/product/"+idResults+"?partner=MTGCompanion&utm_campaign=affiliate&utm_medium=MTGCompanion&utm_source=MTGCompanion");
				mp.setCurrency(Currency.getInstance("USD"));
				mp.setCountry(Locale.US.getDisplayCountry(MTGControler.getInstance().getLocale()));
				mp.setSite(getName());
				mp.setMagicCard(card);
				mp.setSeller(e.getAsJsonObject().get("sellerName").getAsString());
				mp.setLanguage(e.getAsJsonObject().get("language").getAsString());
				mp.setQuality(e.getAsJsonObject().get("condition").getAsString());
				mp.setValue(e.getAsJsonObject().get("price").getAsDouble());
				mp.setSellerUrl("https://www.tcgplayer.com/search/product/all?seller="+e.getAsJsonObject().get("sellerKey").getAsString()+"&view=grid&partner=MTGCompanion&utm_campaign=affiliate&utm_medium=MTGCompanion&utm_source=MTGCompanion");
				mp.setFoil(e.getAsJsonObject().get("printing").getAsString().equalsIgnoreCase("Foil"));

				list.add(mp);

		}
		return list;

	}

	@Override
	public boolean isPartner() {
		return true;
	}

	private JsonArray parseResultsFor(int idResults) throws  IOException {
		var json ="""
				{
				    "filters": {
				        "term": {
				            "sellerStatus": "Live",
				            "channelId": 0,
				            "language": ["English"]
				        },
				        "range": {
				            "quantity": {
				                "gte": 1
				            }
				        },
				        "exclude": {
				            "channelExclusion": 0
				        }
				    },
				    "from": 0,
				    "size": $MAX,
				    "sort": {
				        "field": "price+shipping",
				        "order": "asc"
				    },
				    "context": {
				        "shippingCountry": "FR",
				        "cart": {}
				    },
				    "aggregations": ["listingType"]
				}
					""".replace("$MAX", getString("MAX"));


		var res = c.doPost("https://mp-search-api.tcgplayer.com/v1/product/"+idResults+"/listings", new StringEntity(json), Maps.of("content-type", URLTools.HEADER_JSON));
		var jsonResult  =URLTools.toJson(res.getEntity().getContent());
		if(jsonResult==null)
			return new JsonArray();
		
		return jsonResult.getAsJsonObject().get(RESULTS).getAsJsonArray().get(0).getAsJsonObject().get(RESULTS).getAsJsonArray();
	}


	private Integer parseIdFor(MTGCard card) throws IOException {

		var setName = card.getEdition().getSet().replace(":", "").replace(" ", "-");

		var extra ="";
		if(card.isShowCase())
			extra=" (Showcase)";
		else if(card.isBorderLess())
			extra=" (Borderless)";
		else if(card.isExtendedArt())
			extra=" (Extended Art)";

		var json ="""
				{
					    "algorithm": "",
					    "from": 0,
					    "size": 24,
					    "filters": {
					        "term": {
					            "productLineName": ["magic"],
					            "productName": ["$cardName"],
					            "setName": ["$setName"],
								"productTypeName": ["Cards"],
					            "language": ["English"]
					        },
					        "range": {},
					        "match": {}
					    },
					    "listingSearch": {
					        "filters": {
					            "term": {},
					            "range": {
					                "quantity": {
					                    "gte": 1
					                }
					            },
					            "exclude": {
					                "channelExclusion": 0
					            }
					        },
					        "context": {
					            "cart": {}
					        }
					    },
					    "context": {
					        "cart": {}

					    },
					    "sort": {
					        "field": "product-name",
					        "order": "asc"
					    }
					}
					""".replace("$cardName", card.getName() + extra)
						.replace("$setName", setName);

		var res = c.doPost("https://mpapi.tcgplayer.com/v2/search/request?q=&isList=false", new StringEntity(json), Maps.of("content-type", URLTools.HEADER_JSON));
		var arr =URLTools.toJson(res.getEntity().getContent()).getAsJsonObject().get(RESULTS).getAsJsonArray().get(0).getAsJsonObject().get(RESULTS).getAsJsonArray();

		if(arr.isEmpty())
			return null;


		return arr.get(0).getAsJsonObject().get("productId").getAsInt();
	}


	@Override
	public String getName() {
		return "TCGPlayer";
	}


	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of("MAX", "10");

	}


}
