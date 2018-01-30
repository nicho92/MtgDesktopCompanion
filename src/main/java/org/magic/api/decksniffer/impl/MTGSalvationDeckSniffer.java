package org.magic.api.decksniffer.impl;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.magic.tools.ColorParser;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstNode;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MTGSalvationDeckSniffer extends AbstractDeckSniffer {

	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	

	public MTGSalvationDeckSniffer() {
		super();
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("USER_AGENT", "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.10 (maverick) Firefox/3.6.13");
			props.put("URL", "http://www.mtgsalvation.com/");
			props.put("MAX_PAGE", "2");
			props.put("FORMAT", "Standard");
			props.put("FILTER", "1");//HOT=1, NEW=2, TOPWEEK=3,TOPMONTH=4,TOPALLTIME=5
			save();
		}
	}
	
	@Override
	public String[] listFilter() {
		return new String[] { "Standard","Casual","Classic","Commander","Legacy","Vintage","Modern"};
	}

	@Override
	public MagicDeck getDeck(RetrievableDeck info) throws Exception {
		
		
		String url = info.getUrl()+"#Details:deck-export";
		
		logger.debug("sniff url : " +url);
		
		MagicDeck deck = new MagicDeck();
			deck.setName(info.getName());
			deck.setDateCreation(new Date());
		
		Document d = Jsoup.connect(url)
						  .userAgent(props.getProperty("USER_AGENT"))
						  .get();

		
		deck.setDescription(info.getUrl().toString()+"\n"+d.select("section.guide div").text());
		
		for(Element a : d.select("span.deck-type"))
			deck.getTags().add(a.text());
		
		String plainDeck = d.select("section.deck-export-section pre" ).get(1).text();
		
		boolean sideboard = false;
		
		List<String> elements= new ArrayList<>(Arrays.asList(plainDeck.split("\n")));
		elements.remove(0);
		for(String s : elements)
		{
			if(s.toLowerCase().startsWith("sideboard"))
			{
				sideboard=true;
			}
			else if(s.length()>1)
					{
						try
						{
							int qte = Integer.parseInt(s.substring(0,s.indexOf(' ')));
							String cardName = s.substring(s.indexOf(' '),s.length()).trim();
							MagicEdition ed = null;
							if(cardName.trim().equalsIgnoreCase("Plains")||cardName.trim().equalsIgnoreCase("Island")||cardName.trim().equalsIgnoreCase("Swamp")||cardName.trim().equalsIgnoreCase("Mountain")||cardName.trim().equalsIgnoreCase("Forest"))
							{
								ed = new MagicEdition();
								ed.setId(MTGControler.getInstance().get("default-land-deck"));
							}
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
						catch (Exception e)
						{
							logger.error(e);
						}
					}
		}
		
		return deck;
	}
	
	public List<RetrievableDeck> getDeckList() throws Exception {
		
		
		String url=props.getProperty("URL")+"/decks?filter-format="+getFormatCode(props.getProperty("FORMAT"))+"&filter-deck-time-frame="+props.getProperty("FILTER");
		
		List<RetrievableDeck> list = new ArrayList<>();
		
		int nbPage=1;
		int maxPage = Integer.parseInt(props.getProperty("MAX_PAGE"));
		
		for(int i=1;i<=maxPage;i++)
		{
			url=url+"&page="+nbPage;
			logger.debug("sniff url : " + url);
				
		Document d = Jsoup.connect(url)
    		 	.userAgent(props.getProperty("USER_AGENT"))
				.get();
		
		Elements e = null;
		
		e = d.select("tr.deck-row" );
			
		for(Element cont : e)
		{
			RetrievableDeck deck = new RetrievableDeck();
							deck.setName(cont.select("a.deck-name").html());
							deck.setAuthor(cont.select("small.deck-credit a").text());
							deck.setUrl(new URL(props.getProperty("URL")+"/"+cont.select("a.deck-name").attr("href")).toURI());
							deck.setDescription(cont.select("span.deck-type").html());
							deck.setColor(parseColor(cont.select("script").html()));
			list.add(deck);
		}
		nbPage++;
		}
		return list;
	}
	private String manajson;
	private String parseColor(String string) {
		AstNode node = new Parser().parse(string, "", 1);
		 node.visit( n-> {
				manajson=n.toSource();
				return false;
		});
		 
		manajson=manajson.substring(manajson.indexOf("series")+"series: ".length(),manajson.length()-8);

		JsonArray arr = new JsonParser().parse(manajson).getAsJsonArray();
		manajson="";
		boolean hascolor=false;
		for(int i=0;i<arr.size();i++)
		{
			JsonObject obj = arr.get(i).getAsJsonObject();
			String c = ColorParser.parse(obj.get("name").getAsString());
			JsonArray tab = obj.get("data").getAsJsonArray();
			hascolor=false;
			for(int j=0;j<tab.size();j++)
			{
				if(tab.get(j).getAsInt()>0)
					hascolor=true;
			}
			
			if(hascolor&& !c.equals("{C}"))
			{
				manajson+=c;
			}
		}
		
		return manajson;
	}


	private Integer getFormatCode(String property) {
		switch(property)
		{
			case "Standard":return 32;
			case "Casual" : return 16;
			case "Classic" : return 64;
			case "Commander" : return 2;
			case "Legacy" :return 4;
			case "Vintage": return 8;
			case "Modern" :return 1;
			default :return null;
		}
	}

	@Override
	public void connect() throws Exception {
		// do nothing

	}
	
	@Override
	public String getName() {
		return "MTGSalvation Deck";
	}

}
