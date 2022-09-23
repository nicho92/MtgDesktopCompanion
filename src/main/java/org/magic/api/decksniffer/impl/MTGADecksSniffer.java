package org.magic.api.decksniffer.impl;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RegExUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.technical.RetrievableDeck;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.RequestBuilder.METHOD;
import org.magic.services.network.URLTools;

public class MTGADecksSniffer extends AbstractDeckSniffer {

	private static final String TRUE = "true";
	private static final String FALSE = "false";
	private static final String URL="https://mtgadecks.net";
	
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEPRECATED;
	}
	
	@Override
	public String[] listFilter() {
		return new String[] {"Control","Aggro","Combo","Midrange","Aggro-Control"};
	}

	@Override
	public MagicDeck getDeck(RetrievableDeck info) throws IOException {
		MagicDeck d = info.toBaseDeck();
				  
		Document doc = URLTools.extractAsHtml(info.getUrl().toASCIIString());		  
		
		
		Elements div = doc.select("p#mtga");
		
		
		for(String s : div.html().split("<br>"))
		{
			if(!s.isEmpty())
			{
				
				try {
				AbstractMap.SimpleEntry<String,Integer> entry = (parseString(s));
				MagicCard mc = getEnabledPlugin(MTGCardsProvider.class).searchCardByName(entry.getKey().substring(0,entry.getKey().indexOf('(')).trim(), null, true).get(0);
				d.getMain().put(mc, entry.getValue());
				notify(mc);
				}
				catch(Exception e)
				{
					logger.error("error loading {} : {}",s,e);
				}
				
			}
		}
		return d;
	}

	@Override
	public List<RetrievableDeck> getDeckList(String filter) throws IOException {
		
		List<RetrievableDeck> ret = new ArrayList<>();
		
		RequestBuilder e = RequestBuilder.build()
				 .setClient(URLTools.newClient())
				 .method(METHOD.GET)
				 .url(URL+"/serverSide")
				 .addHeader("x-requested-with", "XMLHttpRequest")
				 .addContent("draw", "2")
				 .addContent("start", "0")
				 .addContent("length", "100")
				 .addContent("search[value]", "")
				 .addContent("search[regex]", FALSE)
				 .addContent("draw", "2")
				 .addContent("columns[0][data]","0")
				 .addContent("columns[0][name]","deckname")
				 .addContent("columns[0][searchable]",FALSE)
				 .addContent("columns[0][orderable]",TRUE)
				 .addContent("columns[0][orderable]","")
				 .addContent("columns[0][search][regex]",FALSE)
				 .addContent("columns[1][data]","1")
				 .addContent("columns[1][name]","colors")
				 .addContent("columns[1][searchable]",FALSE)
				 .addContent("columns[1][orderable]",FALSE)
				 .addContent("columns[1][search][value]","")
				 .addContent("columns[1][search][regex]",FALSE)
				 .addContent("columns[2][data]","2")
				 .addContent("columns[2][name]","archetype")
				 .addContent("columns[2][searchable]",FALSE)
				 .addContent("columns[2][orderable]",FALSE)
				 .addContent("columns[2][search][value]","")
				 .addContent("columns[2][search][regex]",FALSE)
				 .addContent("columns[3][name]","real_update")
				 .addContent("columns[3][searchable]",FALSE)
				 .addContent("columns[3][orderable]",TRUE)
				 .addContent("columns[3][search][value]","")
				 .addContent("columns[3][search][regex]",FALSE)
				 .addContent("&order[0][column]","3")
				 .addContent("&order[0][dir]","desc");
				 
				 if(filter!=null && !filter.isEmpty())
					 e.addContent("data", "archetype="+ArrayUtils.indexOf(listFilter(),filter));
					 
					 
				 var arr = e.toJson().getAsJsonObject().get("data").getAsJsonArray();

				 arr.forEach(a->{
					
					 var deck = new RetrievableDeck();
					
					 var name = URLTools.toHtml(a.getAsJsonArray().get(0).getAsString()).select("a").text();
			 			name = name.substring(0,name.indexOf(" by "));
			 			name = RegExUtils.replaceAll(name, "BO1","").trim();
			 			deck.setName(name);
				
					 try {
						deck.setUrl(new URI(URL+URLTools.toHtml(a.getAsJsonArray().get(0).getAsString()).select("a").attr("href")));
					 } catch (URISyntaxException e1) {
						logger.error(e1);
					 }
					 
					 deck.setAuthor(URLTools.toHtml(a.getAsJsonArray().get(0).getAsString()).select("p").text());

					 
					 var colors = URLTools.toHtml(a.getAsJsonArray().get(1).getAsString()).select("img").attr("alt");
					 var deckColor = new StringBuilder();
					
					 for(var i=0;i<colors.length();i++)
						 	deckColor.append("{").append(String.valueOf(colors.charAt(i)).toUpperCase()).append("}");
					 
					 deck.setColor(deckColor.toString());
					 
					 
					 deck.setDescription(URLTools.toHtml(a.getAsJsonArray().get(2).getAsString()).text());

					
					 ret.add(deck);
					 
				 });

				return ret;
	}

	@Override
	public String getName() {
		return "MTGADecks";
	}

	
	
}
