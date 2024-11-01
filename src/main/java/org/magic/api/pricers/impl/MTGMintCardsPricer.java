package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.select.Elements;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGPrice;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.tools.UITools;

public class MTGMintCardsPricer  extends AbstractPricesProvider{

	
	private static final String URL_BASE="https://www.mtgmintcard.com/";
	
	@Override
	public String getName() {
		return "MTGMintCard";
	}

	@Override
	protected List<MTGPrice> getLocalePrice(MTGCard card) throws IOException {
		var ret = new ArrayList<MTGPrice>();
		var client = URLTools.newClient();
		
		
		var category = (card.isBorderLess()||card.isExtendedArt()||card.isShowCase()||card.isRetro())?"15":"11";
		
		logger.info("{} looking for price for {}", getName(), card);
		
		var trs = RequestBuilder.build().url(URL_BASE+"mtg/singles/search")
										.addContent("action", "normal_search")
										.addContent("keywords",card.getName())
										.addContent("fil_ml",category)
										.setClient(client)
										.get()
										.toHtml().select("tr#product_list_content");
		
			parse(card,ret,trs,false);
			
			
			
			
			if(card.getFinishes().size()>1)
			{
				trs = RequestBuilder.build().url(URL_BASE+"mtg/singles/search")
					.addContent("action", "normal_search")
					.addContent("keywords",card.getName())
					.addContent("fil_ml",category.equals("11")?"12":"16")
					.setClient(client)
					.get()
					.toHtml().select("tr#product_list_content");

				parse(card,ret,trs,true);
			}
	
			
			
			logger.info("{} found {} items", getName(), ret.size());
			
		
		
		return ret;
	}
	
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	private void parse(MTGCard card,ArrayList<MTGPrice> ret, Elements trs,boolean foil) {

		for(var tr : trs)
		{
			 tr.select("td").remove(0);
			 if(tr.select("td a.opacityit").attr("href").contains(card.getEdition().getId().toLowerCase()))
			 {
					
					  var elementsQuality = tr.getElementsByTag("TD").get(3).select("div:not(:empty)");
					  var elementsPrice = tr.getElementsByTag("TD").get(4).select("td strong");
					  var stockAvailable = tr.getElementsByTag("TD").get(6).select("div");
					  
						  for(int i = 0; i<elementsQuality.size();i++)
						  {
								var p = new MTGPrice();
									  p.setCardData(card);
									  p.setCurrency("USD");
									  p.setCountry("Hong-Kong");
									  p.setSite(getName());
									  p.setSeller(getName());
									  p.setUrl(tr.select("td a.opacityit").attr("href"));
									  p.setSellerUrl(p.getUrl());
									  p.setQuality(elementsQuality.get(i).text());
									  p.setValue(UITools.parseDouble(elementsPrice.get(i).text()));
									  p.setLanguage("English");
									  p.setFoil(foil);
									  if(!stockAvailable.get(i).text().contains("Out of Stock"))
										  ret.add(p);
						  }
						  
			 }
	}
		
	}

}
