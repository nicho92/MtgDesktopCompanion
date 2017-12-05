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
import org.magic.api.interfaces.DeckSniffer;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.MTGControler;

public class LotusNoirDecks extends AbstractDeckSniffer {

	public LotusNoirDecks() {
		super();
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("USER_AGENT", "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.10 (maverick) Firefox/3.6.13");
			props.put("URL", "http://www.lotusnoir.info/magic/decks/");
			props.put("FORMAT", "decks-populaires");
			props.put("MAX_PAGE", "2");
			props.put("TIMEOUT", "0");
			save();
		}
		
	}
	
	
	@Override
	public String[] listFilter() {
		return new String[]{"derniers-decks","decks-du-moment","decks-populaires"};
	}

	@Override
	public MagicDeck getDeck(RetrievableDeck info) throws Exception {
		MagicDeck deck = new MagicDeck();
		
		logger.debug("get deck at " + info.getUrl());
		
		Document d = Jsoup.connect(info.getUrl().toString())
    		 	.userAgent(props.getProperty("USER_AGENT"))
    		 	.timeout(Integer.parseInt(props.getProperty("TIMEOUT")))
				.get();
		
		
		deck.setDescription(info.getUrl().toString());
		deck.setName(info.getName());
		
		Elements e = d.select("div.demi_page>table").select("tr");
		boolean sideboard = false;
		for(Element cont : e)
		{
			Elements cont2= cont.select("span.card_title_us" );
			
			if(cont.text().startsWith("Réserve"))
				sideboard=true;
			
			if(cont2.text().length()>0)
			{
				Integer qte = Integer.parseInt(cont2.text().substring(0,cont2.text().indexOf(" ")));
				String cardName = cont2.text().substring(cont2.text().indexOf(" "),cont2.text().length()).trim();
				
				if(cardName.contains("//")) // for transformatble cards
					cardName=cardName.substring(0, cardName.indexOf("//")).trim();
				
				MagicCard mc = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", cardName, null).get(0);
				if(!sideboard)
					deck.getMap().put(mc, qte);
				else
					deck.getMapSideBoard().put(mc, qte);
			}
		}
		return deck;
	}
	
	
	public static void main(String[] args) throws Exception {
		MTGControler.getInstance().getEnabledProviders().init();
		DeckSniffer snif = new LotusNoirDecks();
		RetrievableDeck d = snif.getDeckList().get(0);
		snif.getDeck(d);
		
	}

	@Override
	public List<RetrievableDeck> getDeckList() throws Exception {
	
		String decksUrl = props.getProperty("URL")+"?dpage="+props.getProperty("MAX_PAGE")+"&action="+props.getProperty("FORMAT");
		
		logger.debug("snif decks : " + decksUrl);

		Document d = Jsoup.connect(decksUrl)
    		 	.userAgent(props.getProperty("USER_AGENT"))
    		 	.timeout(Integer.parseInt(props.getProperty("TIMEOUT")))
				.get();
		
		int nbPage = Integer.parseInt(props.getProperty("MAX_PAGE"));
		List<RetrievableDeck> list = new ArrayList<RetrievableDeck>();
		
		
		for(int i=1;i<=nbPage;i++)
		{
			d = Jsoup.connect(props.getProperty("URL")+"?dpage="+i+"&action="+props.getProperty("FORMAT"))
	    		 	.userAgent(props.getProperty("USER_AGENT"))
	    		 	.timeout(Integer.parseInt(props.getProperty("TIMEOUT")))
					.get();
			
			Elements e = d.select("div.thumb_page" );
			
			for(Element cont : e)
			{
				RetrievableDeck deck = new RetrievableDeck();
				Element info = cont.select("a").get(0);
				
				String name = info.attr("title").replaceAll("Lien vers ", "").trim();
				String url = info.attr("href");
				String auteur = cont.select("small").select("a").text();
				
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
		//nothing to do;
	}

	@Override
	public String getName() {
		return "LotusNoir";
	}

	@Override
	public String toString() {
		return getName();
	}
	
}
