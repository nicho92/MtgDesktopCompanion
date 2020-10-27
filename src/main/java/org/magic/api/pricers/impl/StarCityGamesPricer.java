package org.magic.api.pricers.impl;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;
import org.magic.tools.RequestBuilder;
import org.magic.tools.RequestBuilder.METHOD;
import org.magic.tools.URLTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class StarCityGamesPricer extends AbstractMagicPricesProvider {

	
	NumberFormat format = NumberFormat.getCurrencyInstance();
	
	@Override
	public List<MagicPrice> getLocalePrice(MagicEdition me, MagicCard card) throws IOException {
		
		String cardName = URLTools.encode("\""+card.getName()+"\"");
		RequestBuilder build = RequestBuilder.build().setClient(URLTools.newClient()).method(METHOD.GET).url("https://starcitygames.com/search.php?search_query="+cardName+"&page=1&section=product");
		
		Document d = build.toHtml();
		List<MagicPrice> ret = new ArrayList<>();
		Elements trs = d.select("article.productList table tbody tr");

		for(Element tr : trs)
		{
			
			try {
			
				String idProduct = tr.attr("data-id");
				String urlDetails = "https://newstarcityconnector.herokuapp.com/eyApi/products/"+idProduct+"/variants";
				String dataName = tr.attr("data-name");				
				logger.debug(getName() + " looking for prices at " + urlDetails);
				if(dataName.toLowerCase().startsWith(card.getName().toLowerCase())) 
				{
					JsonElement el = build.clean().url(urlDetails).method(METHOD.GET).toJson();
					JsonArray data = el.getAsJsonObject().get("response").getAsJsonObject().get("data").getAsJsonArray();
					
					for(JsonElement obj : data)
					{
						JsonObject item = obj.getAsJsonObject();
						if(item.get("inventory_level").getAsInt()>0)
						{
							MagicPrice mp = new MagicPrice();
									mp.setSite(getName());
									mp.setCountry("USA");
									mp.setCurrency("USD");
									mp.setUrl(tr.select("h4.listItem-title a").attr("href"));
									mp.setValue(item.get("price").getAsDouble());
									mp.setQuality(item.get("option_values").getAsJsonArray().get(0).getAsJsonObject().get("label").getAsString());
									mp.setLanguage(tr.select("p.category-language").text());
									mp.setFoil(tr.select("span.category-row-name-search").html().contains("(Foil)"));
									mp.setSeller(tr.select("span.category-row-name-search").html());
							ret.add(mp);
							notify(mp);
							
						}
					}
				}
				else
				{
					//No Bracket... not a card product
				}
				
			}
			catch(Exception e)
			{
				logger.error(getName() +" has error : " + e);
			}
				
		}
		logger.debug(getName() + " found " + ret.size() + " items");
		return ret;
		
	}


	@Override
	public String getName() {
		return "StarCityGame";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.BUGGED;
	}

	@Override
	public void initDefault() {
		setProperty("NB_PAGE", "1");

	}

	@Override
	public String getVersion() {
		return "0.5";
	}
	

}
