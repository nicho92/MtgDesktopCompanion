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
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.interfaces.MagicCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.MTGControler;

public class TCGPlayerDeckSniffer extends AbstractDeckSniffer {
  

	
	@Override
	public STATUT getStatut() {
		return STATUT.STABLE;
	}
	
	
	public TCGPlayerDeckSniffer() {
		super();
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("FORMAT", "standard");
			props.put("USER_AGENT", "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.10 (maverick) Firefox/3.6.13");
			props.put("URL", "http://decks.tcgplayer.com");
			props.put("MAX_PAGE", "1");
			save();
		}
	}
	
	@Override
	public String[] listFilter() {
		return new String[] { "standard","modern","legacy","vintage","commander",};
	}

	@Override
	public MagicDeck getDeck(RetrievableDeck info) throws Exception {
		logger.debug("get deck at " + info.getUrl());
		MagicDeck deck = new MagicDeck();
		deck.setName(info.getName());
		deck.setDescription(info.getUrl().toString());
	
		Document d = Jsoup.connect(info.getUrl().toString())
					  .userAgent(props.getProperty("USER_AGENT"))
					  .get();
		
		
		Elements main = d.getElementsByClass("subdeck");
		
		int taille = main.get(0).getElementsByClass("subdeck-group__card-qty").size();
		for(int i=0;i<taille;i++)
		{
			int qte = Integer.parseInt(main.get(0).getElementsByClass("subdeck-group__card-qty").get(i).text());
			String cardName =main.get(0).getElementsByClass("subdeck-group__card-name").get(i).text();
			
			MagicEdition ed = null;
			if(cardName.trim().equalsIgnoreCase("Plains")||cardName.trim().equalsIgnoreCase("Island")||cardName.trim().equalsIgnoreCase("Swamp")||cardName.trim().equalsIgnoreCase("Mountain")||cardName.trim().equalsIgnoreCase("Forest"))
			{
				ed = new MagicEdition();
				ed.setId(MTGControler.getInstance().get("default-land-deck"));
			}
			
			if(cardName.contains("//"))
					cardName=cardName.substring(0, cardName.indexOf("//")).trim();
			
			MagicCard mc = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", cardName, ed).get(0);
			
			deck.getMap().put(mc, qte);
			
		}
		
		if(main.size()>1)
		{
			int tailleSide = main.get(1).getElementsByClass("subdeck-group__card-qty").size();
			for(int i=0;i<tailleSide;i++)
			{
				int qte = Integer.parseInt(main.get(1).getElementsByClass("subdeck-group__card-qty").get(i).text());
				String cardName =main.get(1).getElementsByClass("subdeck-group__card-name").get(i).text();
				
				MagicEdition ed = null;
				if(cardName.trim().equalsIgnoreCase("Plains")||cardName.trim().equalsIgnoreCase("Island")||cardName.trim().equalsIgnoreCase("Swamp")||cardName.trim().equalsIgnoreCase("Mountain")||cardName.trim().equalsIgnoreCase("Forest"))
				{
					ed = new MagicEdition();
					ed.setId(MTGControler.getInstance().get("default-land-deck"));
				}
				
				if(cardName.contains("//"))
						cardName=cardName.substring(0, cardName.indexOf("//")).trim();
				
				MagicCard mc = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", cardName, ed).get(0);
				
				deck.getMapSideBoard().put(mc, qte);
			}
		}
		
		return deck;
	
	
	}

	@Override
	public List<RetrievableDeck> getDeckList() throws Exception {
		String url =props.getProperty("URL")+"magic/deck/search?format="+props.getProperty("FORMAT")+"&page=1";
		logger.debug("get List deck at " + url);
		List<RetrievableDeck> list = new ArrayList<RetrievableDeck>();
		int nbPage=1;
		int maxPage = Integer.parseInt(props.getProperty("MAX_PAGE"));
	
		for(int i=1;i<=maxPage;i++)
		{
			
			 url =props.getProperty("URL")+"/magic/deck/search?format="+props.getProperty("FORMAT")+"&page="+nbPage;
			 Document d = Jsoup.connect(url)
		    		 	.userAgent(props.getProperty("USER_AGENT"))
						.get();
				
			Elements table = d.getElementsByClass("dataTable");
			
			table.get(0).getElementsByTag("tr").remove(0);
			for(Element tr : table.get(0).getElementsByClass("gradeA"))
			{
				RetrievableDeck deck = new RetrievableDeck();
				
				String mana="";
				
				Element manaEl = tr.getElementsByTag("td").get(0);
				if(manaEl.toString().contains("white-mana"))
					mana+="{W}";
				if(manaEl.toString().contains("blue-mana"))
					mana+="{U}";
				if(manaEl.toString().contains("black-mana"))
					mana+="{B}";
				if(manaEl.toString().contains("red-mana"))
					mana+="{R}";
				if(manaEl.toString().contains("green-mana"))
					mana+="{G}";
				
				String deckName= tr.getElementsByTag("td").get(1).text();
				String link = props.getProperty("URL")+ tr.getElementsByTag("td").get(1).getElementsByTag("a").attr("href");
				String deckPlayer= tr.getElementsByTag("td").get(2).text();
						
				deck.setColor(mana);
				deck.setAuthor(deckPlayer);
				deck.setName(deckName);
				deck.setUrl(new URI(link));
				
				list.add(deck);
				
			}
			nbPage++;		
			
			
		}
		
		
		
		return list;
		
		
	}

	@Override
	public void connect() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return "TCG Player Decks";
	}

	public static void main(String[] args) throws Exception {
		TCGPlayerDeckSniffer sniff = new TCGPlayerDeckSniffer();
		RetrievableDeck d = sniff.getDeckList().get(11);
		sniff.getDeck(d);
		
		

	}

}
