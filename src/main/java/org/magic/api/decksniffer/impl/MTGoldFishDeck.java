package org.magic.api.decksniffer.impl;

import java.io.File;
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
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.MTGConstants;
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
			props.put("USER_AGENT", MTGConstants.USER_AGENT);
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
	public MagicDeck getDeck(RetrievableDeck info) throws IOException {
		
		logger.debug("sniff url : " + info.getUrl());
		
		MagicDeck deck = new MagicDeck();
			deck.setName(info.getName());
			deck.setDescription(info.getUrl().toString());
			deck.setDateCreation(new Date());
		Document d = Jsoup.connect(info.getUrl().toString())
						  .userAgent(props.getProperty("USER_AGENT"))
						  .get();


		Elements e = d.select("table.deck-view-deck-table" ).get(0).select(MTGConstants.HTML_TAG_TR);
		
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

	public List<RetrievableDeck> getDeckList() throws IOException {
		String url="";
		metagames=props.getProperty("METAGAME").equals("true");
		
		List<RetrievableDeck> list = new ArrayList<>();
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
			StringBuilder deckColor=new StringBuilder();
			
			for(Element c : colors)
				deckColor.append("{").append(c.attr("alt").toUpperCase()).append("}");
			
			
			RetrievableDeck deck = new RetrievableDeck();
			deck.setName(desc.get(0).text());
			try {
				deck.setUrl(new URI(props.get("URL")+desc.get(0).attr("href")));
			} catch (URISyntaxException e1) {
				deck.setUrl(null);
			}
			
			if(metagames)
				deck.setAuthor("MtgGoldFish");
			else
				deck.setAuthor(cont.select("div.deck-author").text());
		
			deck.setColor(deckColor.toString());
			
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
	public void connect() throws IOException {
		// do nothing

	}
	
	@Override
	public String getName() {
		return "MTGGoldFish Deck";
	}

}
