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
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.MTGDesktopCompanionControler;

public class DeckstatsDeckSniffer extends AbstractDeckSniffer {

	
	public DeckstatsDeckSniffer() {
		super();
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("USER_AGENT", "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.10 (maverick) Firefox/3.6.13");
			props.put("URL", "https://deckstats.net/decks/f/");
			props.put("TIMEOUT", "0");
			props.put("FORMAT", "standard");
			props.put("MAX_PAGE", "2");
			save();
		}
	}
	
	@Override
	public String[] listFilter() {
		return new String[]{"casual","standard","modern","legacy","edh-commander","highlander","frontier","pauper","vintage","extended","vube","tiny-leaders","peasant","other"};
	}

	@Override
	public MagicDeck getDeck(RetrievableDeck info) throws Exception {
		// 
		
		MagicDeck deck = new MagicDeck();
		Document d = Jsoup.connect(info.getUrl().toString())
    		 	.userAgent(props.getProperty("USER_AGENT"))
    		 	.timeout(Integer.parseInt(props.getProperty("TIMEOUT")))
				.get();
		
		deck.setDescription(info.getUrl().toString());
		deck.setName(info.getName());
		
		Elements e = d.select("tr.deck_card");
		
		for(Element cont : e)
		{
				Integer qte = Integer.parseInt(cont.getElementsByClass("card_amount").get(0).text());
				String cardName = cont.getElementsByClass("deck_card_name").get(0).text().trim();
				
				if(cardName.contains("//"))
					cardName=cardName.substring(0, cardName.indexOf("//")).trim();
				
				
				String set = cont.getElementsByClass("deck_col_set").get(0).getElementsByTag("a").text().trim();
				MagicCard mc = null;
				if(set==null)
				{
					mc = MTGDesktopCompanionControler.getInstance().getEnabledProviders().searchCardByCriteria("name", cardName, null).get(0);
				}
				else
				{
					MagicEdition me = new MagicEdition();
								 me.setId(set);
					mc = MTGDesktopCompanionControler.getInstance().getEnabledProviders().searchCardByCriteria("name", cardName, me).get(0);
				}
				deck.getMap().put(mc, qte);
		}

		Elements s = d.select("table#cards_sideboard").select("tr");
		s.remove(0);
		s.remove(0);
	//	s.remove(s.size()-1);
		for(Element cont : s)
		{
			try{
				Integer qte = Integer.parseInt(cont.getElementsByClass("card_amount").get(0).text());
				String cardName = cont.getElementsByClass("deck_card_name").get(0).text().trim();
				MagicCard mc = MTGDesktopCompanionControler.getInstance().getEnabledProviders().searchCardByCriteria("name", cardName, null).get(0);
				deck.getMapSideBoard().put(mc, qte);
			}
			catch(IndexOutOfBoundsException ex)
			{
				
			}
		}
		
		return deck;
	}

	@Override
	public List<RetrievableDeck> getDeckList() throws Exception {
		
		Document d = Jsoup.connect(props.getProperty("URL")+"/"+props.getProperty("FORMAT")+"/?lng=fr&page="+props.getProperty("MAX_PAGE"))
    		 	.userAgent(props.getProperty("USER_AGENT"))
    		 	.timeout(Integer.parseInt(props.getProperty("TIMEOUT")))
				.get();
		
		
		
		int nbPage = Integer.parseInt(props.getProperty("MAX_PAGE"));
		List<RetrievableDeck> list = new ArrayList<RetrievableDeck>();
		
		
		for(int i=1;i<=nbPage;i++)
		{
			d = Jsoup.connect(props.getProperty("URL")+"/"+props.getProperty("FORMAT")+"/?lng=fr&page="+i)
	    		 	.userAgent(props.getProperty("USER_AGENT"))
	    		 	.timeout(Integer.parseInt(props.getProperty("TIMEOUT")))
					.get();
			
			Elements e = d.select("tr.touch_row" );
	
			for(Element cont : e)
			{
				RetrievableDeck deck = new RetrievableDeck();
				Element info = cont.select("a").get(0);
				
				
				String name = info.text();
				String url = info.attr("href");
				String auteur = cont.select("a").get(1).text();
				
				deck.setName(name);
				deck.setUrl(new URI(url));
				deck.setAuthor(auteur);
				deck.setColor("");
				
				list.add(deck);
			}
		}
		return list;
	}

	@Override
	public void connect() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return "DeckStats.net";
	}

}
