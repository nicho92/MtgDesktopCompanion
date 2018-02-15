package org.magic.api.decksniffer.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class DeckstatsDeckSniffer extends AbstractDeckSniffer {

	Map<Integer,String> cacheColor;
	   

	
	@Override
	public STATUT getStatut() {
		return STATUT.STABLE;
	}
	
	
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
		cacheColor = new HashMap<>();
		initcache();
	}
	
	private void initcache() {
		cacheColor.put(1, "{W}");
		cacheColor.put(2, "{U}");
		cacheColor.put(3, "{W}{U}");
		cacheColor.put(4, "{B}");
		cacheColor.put(5, "{W}{B}");
		cacheColor.put(6, "{U}{B}");
		cacheColor.put(7, "{W}{U}{B}");
		cacheColor.put(8, "{R}");
		cacheColor.put(9, "{W}{R}");
		cacheColor.put(10, "{U}{R}");
		cacheColor.put(11, "{W}{U}{R}");
		cacheColor.put(12, "{B}{R}");
		cacheColor.put(13, "{W}{B}{R}");
		cacheColor.put(14, "{U}{B}{R}");
		cacheColor.put(15, "{W}{U}{B}{R}");
		cacheColor.put(16, "{G}");
		cacheColor.put(17, "{W}{G}");
		cacheColor.put(18, "{U}{G}");
		cacheColor.put(19, "{W}{U}{G}");
		cacheColor.put(20, "{B}{R}");
		cacheColor.put(21, "{W}{B}{G}");
		cacheColor.put(22, "{U}{B}{G}");
		cacheColor.put(23, "{W}{U}{B}{G}");
		cacheColor.put(24, "{R}{G}");
		cacheColor.put(25, "{W}{R}{G}");
		cacheColor.put(26, "{U}{R}{G}");
		cacheColor.put(27, "{W}{U}{R}{G}");
		cacheColor.put(28, "{B}{R}{G}");
		cacheColor.put(29, "{W}{B}{R}{G}");
		cacheColor.put(30, "{U}{B}{R}{G}");
		cacheColor.put(31, "{W}{U}{B}{R}{G}");
	}

	@Override
	public String[] listFilter() {
		return new String[]{"casual","standard","modern","legacy","edh-commander","highlander","frontier","pauper","vintage","extended","cube","tiny-leaders","peasant","other"};
	}

	@Override
	public MagicDeck getDeck(RetrievableDeck info) throws IOException {
		// 
		
		MagicDeck deck = new MagicDeck();
		
		logger.debug("get deck " + info.getUrl());
		Document d = Jsoup.connect(info.getUrl().toString())
    		 	.userAgent(props.getProperty("USER_AGENT"))
    		 	.timeout(Integer.parseInt(props.getProperty("TIMEOUT")))
				.get();
		
		deck.setDescription(info.getUrl().toString());
		deck.setName(info.getName());
		deck.setDateCreation(new Date());
		for(Element a : d.select("a.deck_tags_list_tag"))
			deck.getTags().add(a.text());

		Elements e = d.select("tr.deck_card");
		
		for(Element cont : e)
		{
				Integer qte = Integer.parseInt(cont.getElementsByClass("card_amount").get(0).text());
				String cardName = cont.getElementsByClass("deck_card_name").get(0).text().trim();
			
				
				if(cardName.contains("//"))
					cardName=cardName.substring(0, cardName.indexOf("//")).trim();
				
				
				String set = cont.getElementsByClass("deck_col_set").get(0).getElementsByTag("a").text().trim();
				MagicCard mc = null;
				
				if(set.equals(""))
				{
					if(cardName.trim().equalsIgnoreCase("Plains")||cardName.trim().equalsIgnoreCase("Island")||cardName.trim().equalsIgnoreCase("Swamp")||cardName.trim().equalsIgnoreCase("Mountain")||cardName.trim().equalsIgnoreCase("Forest"))
					{
						MagicEdition ed = new MagicEdition();
						ed.setId(MTGControler.getInstance().get("default-land-deck"));
						mc = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", cardName, ed,true).get(0);
					}
					else
					{
						mc = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", cardName, null,true).get(0);
					}
				}
				else
				{
					MagicEdition me = new MagicEdition();
								 me.setId(set);
					mc = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", cardName, me,true).get(0);
				}
				deck.getMap().put(mc, qte);
		}
		try{

		Elements s = d.select("table#cards_sideboard").select("tr");
		s.remove(0);
		s.remove(0);

			for(Element cont : s)
			{
				
					Integer qte = Integer.parseInt(cont.getElementsByClass("card_amount").get(0).text());
					String cardName = cont.getElementsByClass("deck_card_name").get(0).text().trim();
					MagicCard mc = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", cardName, null,true).get(0);
					deck.getMapSideBoard().put(mc, qte);
			}
		}
		catch(Exception ex)
		{
			MTGLogger.printStackTrace(ex);
		}

		return deck;
	}

	@Override
	public List<RetrievableDeck> getDeckList() throws IOException {
		
		int nbPage = Integer.parseInt(props.getProperty("MAX_PAGE"));
		List<RetrievableDeck> list = new ArrayList<>();
		
		
		for(int i=1;i<=nbPage;i++)
		{
			Document d = Jsoup.connect(props.getProperty("URL")+"/"+props.getProperty("FORMAT")+"/?lng=fr&page="+i)
	    		 	.userAgent(props.getProperty("USER_AGENT"))
	    		 	.timeout(Integer.parseInt(props.getProperty("TIMEOUT")))
					.get();
			
			Elements e = d.select("tr.touch_row" );
	
			for(Element cont : e)
			{
				RetrievableDeck deck = new RetrievableDeck();
				Element info = cont.select("a").get(0);
				String idColor = cont.select("img").get(0).attr("src");
				idColor=idColor.substring(idColor.lastIndexOf('/')+1,idColor.lastIndexOf('.'));
				String name = info.text();
				String url = info.attr("href");
				String auteur = cont.select("a").get(1).text();
				
				deck.setName(name);
				try {
					deck.setUrl(new URI(url));
				} catch (URISyntaxException e1) {
					deck.setUrl(null);
				}
				deck.setAuthor(auteur);
				deck.setColor(cacheColor.get(Integer.parseInt(idColor)));
				
				list.add(deck);
			}
		}
		return list;
	}

	@Override
	public void connect() throws IOException {
		//nothing to do
	}

	@Override
	public String getName() {
		return "DeckStats.net";
	}

}
