package org.magic.api.decksniffer.impl;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.interfaces.MagicCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.MTGControler;

public class MTGoldFishDeck extends AbstractDeckSniffer {

	
	@Override
	public STATUT getStatut() {
		return STATUT.STABLE;
	}
	
	
	private boolean metagames=false;

	public MTGoldFishDeck() {
		super();
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("SUPPORT", "paper");
			props.put("FORMAT", "modern");
			props.put("USER_AGENT", "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.10 (maverick) Firefox/3.6.13");
			props.put("URL", "http://www.mtggoldfish.com/");
			props.put("MAX_PAGE", "2");
			props.put("METAGAME", "false");
			save();
		}
	}
	
	@Override
	public String[] listFilter() {
		if(metagames)
			return new String[] { "standard","modern","pauper","legacy","vintage","commander","tiny_leaders"};
		else
			return new String[] { "standard","modern","pauper","legacy","vintage","block","commander","limited","frontier","canadian_highlander","penny_dreadful","tiny_Leaders","free_Form"};
	}

	@Override
	public MagicDeck getDeck(RetrievableDeck info) throws Exception {
		
		logger.debug("sniff url : " + info.getUrl());
		
		MagicDeck deck = new MagicDeck();
			deck.setName(info.getName());
			deck.setDescription(info.getUrl().toString());
			deck.setDateCreation(new Date());
		Document d = Jsoup.connect(info.getUrl().toString())
						  .userAgent(props.getProperty("USER_AGENT"))
						  .get();


		Elements e = d.select("table.deck-view-deck-table" ).get(0).select("tr");
		
		boolean sideboard = false;
		for(Element tr: e)
		{
			if(tr.select("td.deck-header").text().contains("Sideboard"))
				sideboard=true;

			if((tr.select("td.deck-col-qty").text() + " " + tr.select("td.deck-col-card").text()).length()>1)
			{
				
				int qte = Integer.parseInt(tr.select("td.deck-col-qty").text());
				String cardName = tr.select("td.deck-col-card").text();
				MagicEdition ed = null;
				if(cardName.trim().equalsIgnoreCase("Plains")||cardName.trim().equalsIgnoreCase("Island")||cardName.trim().equalsIgnoreCase("Swamp")||cardName.trim().equalsIgnoreCase("Mountain")||cardName.trim().equalsIgnoreCase("Forest"))
				{
					ed = new MagicEdition();
					ed.setId(MTGControler.getInstance().get("default-land-deck"));
				}
				
				if(cardName.contains("//"))
						cardName=cardName.substring(0, cardName.indexOf("//")).trim();
				
				
				MagicCard mc = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", cardName, ed,true).get(0);
				if(!sideboard)
				{
					deck.getMap().put(mc, qte);
				}
				else
				{
					deck.getMapSideBoard().put(mc, qte);
				}
			}
			
			
			  
		}
		return deck;
	}

	public List<RetrievableDeck> getDeckList() throws Exception {
		String url="";
		
		
		
		metagames=props.getProperty("METAGAME").equals("true");

		
		if(!metagames)
				url =props.getProperty("URL")+"/deck/custom/"+props.getProperty("FORMAT")+"?page=1#"+props.getProperty("SUPPORT");
		else
				url=props.getProperty("URL")+"metagame/"+props.getProperty("FORMAT")+"/full#"+props.getProperty("SUPPORT");
		
		List<RetrievableDeck> list = new ArrayList<RetrievableDeck>();
		
		int nbPage=1;
		int maxPage = Integer.parseInt(props.getProperty("MAX_PAGE"));
		
		
		if(metagames)
			maxPage=1;
		
		
		for(int i=1;i<=maxPage;i++)
		{
		
			if(!metagames)
				url =props.getProperty("URL")+"/deck/custom/"+props.getProperty("FORMAT")+"?page="+nbPage+"#"+props.getProperty("SUPPORT");
			else
				url=props.getProperty("URL")+"metagame/"+props.getProperty("FORMAT")+"/full#"+props.getProperty("SUPPORT");
	
			logger.debug("sniff url : " + url);
				
		Document d = Jsoup.connect(url)
    		 	.userAgent(props.getProperty("USER_AGENT"))
				.get();
		
		Elements e = null;
		
		if(!metagames)
			e = d.select("div.deck-tile" );
		else
			e = d.select("div.archetype-tile" );
			
		for(Element cont : e)
		{
			
			Elements desc = cont.select("span.deck-price-"+props.getProperty("SUPPORT") +"> a" );
			Elements colors = cont.select("span.manacost > img" );
			String deckColor="";
			for(Element c : colors)
				deckColor+="{"+c.attr("alt").toUpperCase()+"}";
			
			
			RetrievableDeck deck = new RetrievableDeck();
			deck.setName(desc.get(0).text());
			deck.setUrl(new URI(props.get("URL")+desc.get(0).attr("href")));
			
			if(metagames)
				deck.setAuthor("MtgGoldFish");
			else
				deck.setAuthor(cont.select("div.deck-author").text());
		
			deck.setColor(deckColor);
			
			for(Element mc : cont.getElementsByTag("li"))
			{
				deck.getKeycards().add(mc.text());
			}
			
			
			list.add(deck);
			
		}
		nbPage++;
		}
		return list;
	}
	
	@Override
	public void connect() throws Exception {
		// Nothing todo

	}
	
	@Override
	public String getName() {
		return "MTGGoldFish Deck";
	}

}
