package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;
import org.magic.tools.UITools;
import org.magic.tools.URLTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MiniatureMarketPricer extends AbstractMagicPricesProvider {

	@Override
	public String getName() {
		return "MiniatureMarket";
	}

	@Override
	protected List<MagicPrice> getLocalePrice(MagicEdition me, MagicCard card) throws IOException {
		
		List<MagicPrice> prices = new ArrayList<>();
		MagicEdition ed = me;
		
		if(ed==null)
			ed=card.getCurrentSet();
		
		
		
		String search = (card.getName() + " " + ed.getSet()).replace(" ", "%20");
		String url = "https://search.unbxd.io/fb500edbf5c28edfa74cc90561fe33c3/prod-miniaturemarket-com811741582229555/autosuggest?q="+search+"&popularProducts.boost=if(gt(query($dd),0),1,0.00001)&dd=stock_status:%22In%20Stock%22%20AND%20product_tag:%22New%20Items%22&inFields.count=10&topQueries.count=10&keywordSuggestions.count=10&popularProducts.count=6&promotedSuggestion.count=10&indent=off&popularProducts.fields=doctype,title,imageUrl,productUrl,price,stock_status,regular_price,quantity,cached_thumbnail_url,entity_type_id%20,autosuggest";

		logger.debug(getName() + " looking for prices at " + url);
		
		JsonElement el = URLTools.extractJson(url);
		JsonArray arr = el.getAsJsonObject().get("response").getAsJsonObject().get("products").getAsJsonArray();
		
		JsonObject je = null;
		
		
		if(arr.size()>0)
			je = arr.get(0).getAsJsonObject();
		
		
		if(je!=null && je.getAsJsonObject().get("quantity").getAsInt()>0)
		{
				
			Document d = URLTools.extractHtml(je.get("productUrl").getAsString());
			Elements divs = d.select("div.grouped-row");
			
			for(Element div : divs)
			{
				MagicPrice mp = new MagicPrice();
						mp.setCountry("USA");
						mp.setMagicCard(card);
						mp.setCurrency("USD");
						mp.setUrl(je.get("productUrl").getAsString());
						mp.setSite(getName());
						mp.setQuality(div.selectFirst("div.item-name").text().replace("Foil", "").trim());
						mp.setValue(UITools.parseDouble(div.selectFirst("div.item-price").text().replace("$", "")));
						mp.setSeller(ed.getSet());
						mp.setFoil(div.selectFirst("div.item-name").text().toLowerCase().contains("foil"));
						mp.setLanguage("English");
				prices.add(mp);
			}
		}
		return prices;
	}
}
