package org.magic.api.decksniffer.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class MTGDecksSniffer extends AbstractDeckSniffer {

	public static void main(String[] args) throws IOException {
		MTGDecksSniffer sniff = new MTGDecksSniffer();
		List<RetrievableDeck> list = sniff.getDeckList();
		
		sniff.getDeck(list.get(0));	
		
		
	}
	
	
	@Override
	public String[] listFilter() {
		return new String[] {"Standard","Modern","Legacy","Vintage","Commander","MTGO","Pauper","Frontier","Peasant","Highlander"};
	}

	@Override
	public MagicDeck getDeck(RetrievableDeck info) throws IOException {
		
		MagicDeck deck = new MagicDeck();
				  deck.setDateCreation(new Date());
				  deck.setName(info.getName());
				  deck.setDescription("from " + info.getUrl());
				  
		logger.debug("get deck at " + info.getUrl());
		
		Document d = Jsoup.connect(info.getUrl().toString())
    		 	.userAgent(getString("USER_AGENT"))
				.get();
		
		for(Element e : d.select("table.subtitle a"))
			deck.getTags().add(e.text());
		
		
		Elements tables = d.select("div.wholeDeck table");
		
		boolean isSideboard=false;
		
		for(Element table : tables)
		{
			isSideboard=table.select("th").first().hasClass("Sideboard");
			
			for(Element tr : table.select("tr.cardItem"))
			{
				Element td=tr.select("td.number").first();
				String qte = td.html().substring(0,td.html().indexOf(' '));
				String name=td.select("a").text();
				if(name.contains("/"))
					name=name.substring(0, name.indexOf('/')).trim();
			
				MagicCard mc = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", name, null, true).get(0);
				
				if(!isSideboard)
					deck.getMap().put(mc, Integer.parseInt(qte));
				else
					deck.getMapSideBoard().put(mc, Integer.parseInt(qte));
				
			}
			
			
			
		}
		
		
		
		return deck;
	}

	@Override
	public List<RetrievableDeck> getDeckList() throws IOException {
		String url =getString("URL")+"/"+getString("FORMAT")+"/decklists/page:1";
		logger.debug("get List deck at " + url);
		List<RetrievableDeck> list = new ArrayList<>();
		int nbPage=1;
		int maxPage = getInt("MAX_PAGE");
		
		for(int i=1;i<=maxPage;i++)
		{
			 url =getString("URL")+"/"+getString("FORMAT")+"/decklists/page:"+nbPage;
			 Document d = Jsoup.connect(url)
					  .userAgent(getString("USER_AGENT"))
					  .get();

			 Elements trs = d.select("table.table tr");
			 
			 for(int j=1;j<trs.size();j++)
			 {
				Element tr = trs.get(j);
				RetrievableDeck deck = new RetrievableDeck();
				
				deck.setName(tr.select("td a").first().text());
				try {
					deck.setUrl(new URI(getString("URL")+'/'+tr.select("td a").first().attr("href")));
				} catch (URISyntaxException e) {
					deck.setUrl(null);
				}
				deck.setAuthor(tr.select("td").get(1).select("strong").get(1).text());
				
				String manas=tr.select("td").get(3).html();
				
				StringBuilder build = new StringBuilder();
				
				
				if(manas.contains("ms-w"))
					build.append("{W}");
				if(manas.contains("ms-u"))
					build.append("{U}");
				if(manas.contains("ms-b"))
					build.append("{B}");
				if(manas.contains("ms-r"))
					build.append("{R}");
				if(manas.contains("ms-g"))
					build.append("{G}");
				
				deck.setColor(build.toString());
				list.add(deck);
			 }
			 
			 
			 
			 
		}
		
		return list;
	}

	@Override
	public void connect() throws IOException {
		//do nothing

	}

	@Override
	public String getName() {
		return "MTGDecks";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public void initDefault() {
		setProperty("USER_AGENT", MTGConstants.USER_AGENT);
		setProperty("URL", "https://mtgdecks.net");
		setProperty("FORMAT", "Standard");
		setProperty("MAX_PAGE","2");
	}

	@Override
	public String getVersion() {
		return "0.1";
	}

}

