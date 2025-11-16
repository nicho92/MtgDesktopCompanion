package org.magic.api.pricers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGPrice;
import org.magic.api.interfaces.abstracts.AbstractPricesProvider;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.URLTools;

public class PlayInPricer extends AbstractPricesProvider {

	private static final String BASE_URL="https://www.play-in.com";
	private MTGHttpClient client;

	@Override
	public STATUT getStatut() {
		return STATUT.BUGGED;
	}
	
	public static void main(String[] args) throws IOException {
		var pricer = new PlayInPricer();
		
		var c = new MTGCard();
		c.setName("Lightning Bolt");
		
		var u = pricer.getLocalePrice(c);
		
	}
	
	
	public PlayInPricer() {
		client = URLTools.newClient();
	}
	
	

	private String getPage(String name) throws IOException
	{
		var nextRouter="[\"\",{\"children\":[[\"lang\",\"en\",\"d\"],{\"children\":[\"(website)\",{\"children\":[\"__PAGE__\",{},null,null]},null,null]},null,null]},null,null,true]";
		var heads = Map.of(URLTools.ACCEPT,"text/x-component",URLTools.ORIGIN,BASE_URL,URLTools.REFERER,BASE_URL,URLTools.CONTENT_TYPE,"text/plain;charset=UTF-8","next-router-state-tree",nextRouter,"next-action","7f7617c8e2a0143fb7835dd63bee1989331461053f");
		var res = client.doPost(BASE_URL, new StringEntity("[{\"lang\":\"en\",\"search\":\""+name+"\",\"searchCategory\":\"unitCards\"}]"), heads);
		var ret =EntityUtils.toString(res.getEntity());
		var result = URLTools.toJson(ret.split("\\n")[1].substring(2)).getAsJsonArray();
		return result.get(0).getAsJsonObject().get("href").getAsString();
	}

	@Override
	public List<MTGPrice> getLocalePrice(MTGCard card) throws IOException {
		var nextRouter ="[\"\",{\"children\":[[\"lang\",\"en\",\"d\"],{\"children\":[\"(website)\",{\"children\":[\"carte\",{\"children\":[[\"cardId\",\"2337\",\"d\"],{\"children\":[[\"cardSlug\",\"foudre\",\"d\"],{\"children\":[\"__PAGE__\",{},null,null]},null,null]},null,null]},null,null]},null,null]},null,null]},null,\"refetch\"]";
		var heads = Map.of(URLTools.ACCEPT,"text/x-component",URLTools.ORIGIN,BASE_URL,URLTools.REFERER,BASE_URL,URLTools.CONTENT_TYPE,"text/plain;charset=UTF-8","next-router-state-tree",nextRouter,"next-action","7f7617c8e2a0143fb7835dd63bee1989331461053f");
		var list = new ArrayList<MTGPrice>();
		var page = BASE_URL+getPage(card.getName());
				
		logger.info("{} looking for prices {}",getName(),page);

		try {
			var res = client.doGet(page);
					
			logger.info("{} found {} offers",getName(),list.size());

			return list;
		} catch (Exception e) {
			logger.trace("Error loading price for {}",page, e);
			logger.info("{} found no item : {}",getName(),e.getMessage());
			return list;
		}
	}

	@Override
	public String getName() {
		return "PlayIn";
	}

	@Override
	public String getVersion() {
		return "1.5";
	}

}
