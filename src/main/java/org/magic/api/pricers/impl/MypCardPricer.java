package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;
import org.magic.tools.RequestBuilder;
import org.magic.tools.RequestBuilder.METHOD;
import org.magic.tools.URLTools;
import org.magic.tools.URLToolsClient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MypCardPricer extends AbstractMagicPricesProvider {

	
	private static final String BASE_URL="https://mypcards.com";
	private URLToolsClient client;
	
	
	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}
	
	@Override
	public String getName() {
		return "Mypcards";
	}

	@Override
	protected List<MagicPrice> getLocalePrice(MagicEdition me, MagicCard card) throws IOException {

		List<MagicPrice> list = new ArrayList<>();
		
		String set = card.getCurrentSet().getId();
		
		if(me!=null)
			set=me.getId();
		
		
		if(client==null)
			client=URLTools.newClient();
		
		
		String url=BASE_URL+"/produto/search";
		JsonElement e = RequestBuilder.build().url(url).setClient(client).method(METHOD.GET).addContent("term",card.getName()).toJson();
		JsonObject o = e.getAsJsonArray().get(0).getAsJsonObject();
		
		int qtyVariation = o.get("qtd").getAsInt();
		
		if(qtyVariation==1)
		{
			parsingOffers(BASE_URL + "/produto/"+o.get("idproduto").getAsInt()+"/"+o.get("slugnomeptproduto").getAsString(),list);
		}
		else
		{
			Elements divs = RequestBuilder.build().method(METHOD.GET).url(BASE_URL + "/magic").setClient(client).addContent("ProdutoSearch[query]", card.getName()).toHtml().select("div.card");
			
			for(Element div : divs)
			{
				if(div.select("a div").toString().contains("magic_"+set.toLowerCase()+"_"))
				{
					String urlC = BASE_URL +div.select("a").attr("href");
					parsingOffers(urlC, list);
					return list;
				}
			}
		}
		return list;
	}
	
	private void parsingOffers(String urlC, List<MagicPrice> list) throws IOException {
		Elements trs = URLTools.extractHtml(urlC).select("table.table tr[data-key]");
		for(Element tr : trs)
		{
			Elements tds = tr.select("td");
			if(tds.isEmpty())
			{
				logger.debug(getName() + " found no offer");
				return;
			}
			
			MagicPrice mp = new MagicPrice();
				mp.setCountry("Brazil");
				mp.setCurrency(Currency.getInstance("BRL"));
				mp.setSite(getName());
				mp.setSeller(tds.get(1).text());
				mp.setFoil(tds.get(2).html().equalsIgnoreCase("foil"));
				mp.setQuality(tds.get(3).html());
				mp.setValue(Double.parseDouble(tds.get(5).html()));
				mp.setUrl(urlC);
				list.add(mp);
		}
		logger.debug(getName() + " found " + list.size() + " offers");
	}
	

}
