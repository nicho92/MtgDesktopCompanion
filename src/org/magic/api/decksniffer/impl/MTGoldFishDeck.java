package org.magic.api.decksniffer.impl;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.MTGDesktopCompanionControler;

public class MTGoldFishDeck extends AbstractDeckSniffer {

	
	public MTGoldFishDeck() {
		super();
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("SUPPORT", "paper");
			props.put("FORMAT", "modern");
			props.put("USER_AGENT", "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.10 (maverick) Firefox/3.6.13");
			props.put("URL", "http://www.mtggoldfish.com/");
			save();
	}
	}
	
	@Override
	public String[] listFilter() {
		return new String[] { "standard","modern","pauper","legacy","vintage","commander","tiny_leaders"};
	}

	@Override
	public MagicDeck getDeck(RetrievableDeck info) throws Exception {
		
		MagicDeck deck = new MagicDeck();
			deck.setName(info.getName());
			deck.setDescription(info.getUrl().toString());
		
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
				MagicCard mc = MTGDesktopCompanionControler.getInstance().getEnabledProviders().searchCardByCriteria("name", cardName, null).get(0);
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
		Document d = Jsoup.connect(props.getProperty("URL")+"metagame/"+props.getProperty("FORMAT")+"/full#"+props.getProperty("SUPPORT"))
    		 	.userAgent(props.getProperty("USER_AGENT"))
				.get();
		
		
		Elements e = d.select("div.archetype-tile" );
		
		List<RetrievableDeck> list = new ArrayList<RetrievableDeck>();
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
			deck.setAuthor("MtgGoldFish");
			deck.setColor(deckColor);
			
			for(Element mc : cont.getElementsByTag("li"))
			{
				deck.getKeycards().add(mc.text());
			}
			
			
			list.add(deck);
		}
		return list;
	}
	
	@Override
	public void connect() throws Exception {
		// Nothing todo

	}

	@Override
	public String toString() {
		return getName();
	}
	@Override
	public String getName() {
		return "MTGGoldFish Deck";
	}

}
