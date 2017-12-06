package org.magic.api.decksniffer.impl;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.interfaces.DeckSniffer;
import org.magic.api.interfaces.MagicCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;

public class MagicCorporationDecks extends AbstractDeckSniffer {


	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	
	public MagicCorporationDecks() {
		super();
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("USER_AGENT", "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.10 (maverick) Firefox/3.6.13");
			props.put("URL", "http://www.magiccorporation.com/");
			save();
	}
		
	}
	
	@Override
	public String[] listFilter() {
		return new String[]{""};
	}

	@Override
	public MagicDeck getDeck(RetrievableDeck info) throws Exception {
		Document d = Jsoup.connect(props.getProperty("URL")+"/"+info.getUrl().toString())
    		 	.userAgent(props.getProperty("USER_AGENT"))
				.get();
		
		MagicDeck deck = new MagicDeck();
				  deck.setName(info.getName());
		  	 	  deck.setDescription(d.select("div.block_content").get(4).text().trim());
		
		Elements list = d.select("div.liste_deck>ul");
		
		for(Element ul : list)
		{
			for(Element li : ul.select("li") )
			{
				
					Integer qte = Integer.parseInt(li.text().substring(0,li.text().indexOf(" ")));
					String name = li.getElementsByTag("a").attr("title");
					System.out.println(qte + " " + name);//TODO : have to find english name for french cards.
			}
		}
		
		
		return deck;
	}
	
	
	public static void main(String[] args) throws Exception {
		DeckSniffer snif = new MagicCorporationDecks();
		RetrievableDeck d = snif.getDeckList().get(8);
		snif.getDeck(d);
	}

	@Override
	public List<RetrievableDeck> getDeckList() throws Exception {
		Document d = Jsoup.connect(props.getProperty("URL")+"/mc.php?rub=decks&limit=0")
    		 	.userAgent(props.getProperty("USER_AGENT"))
				.get();
		
		
		Elements e = d.select("table.html_table > tbody" );
		
		List<RetrievableDeck> list = new ArrayList<RetrievableDeck>();
		for(Element cont : e.get(0).getElementsByTag("tr"))
		{
			String name = cont.getElementsByTag("td").get(1).text();
			String url = cont.getElementsByTag("td").get(1).getElementsByTag("a").attr("href");
			String auteur = cont.getElementsByTag("td").get(5).text();
			
			StringBuffer temp = new StringBuffer();
			
			for(Element color : cont.getElementsByTag("td").get(2).select("img"))
			{
				if(color.attr("src").contains("white"))
					temp.append("{W}");
				if(color.attr("src").contains("blue"))
					temp.append("{U}");
				if(color.attr("src").contains("black"))
					temp.append("{B}");
				if(color.attr("src").contains("red"))
					temp.append("{R}");
				if(color.attr("src").contains("green"))
					temp.append("{G}");
			}
		
			RetrievableDeck deck = new RetrievableDeck();
			
			deck.setName(name);
			deck.setUrl(new URI(url));
			deck.setAuthor(auteur);
			deck.setColor(temp.toString());
			list.add(deck);
		}
		
		return list;
	}

	@Override
	public void connect() throws Exception {
		//nothing to do;
	}

	@Override
	public String getName() {
		return "MagicCorporation";
	}

	
}
