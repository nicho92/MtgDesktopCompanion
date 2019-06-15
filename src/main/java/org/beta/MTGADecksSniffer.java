package org.beta;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.tools.RequestBuilder;
import org.magic.tools.RequestBuilder.METHOD;
import org.magic.tools.URLTools;

import com.google.gson.JsonArray;

public class MTGADecksSniffer extends AbstractDeckSniffer {

	private static final String TRUE = "true";
	private static final String FALSE = "false";
	private static final String FORMAT = "FORMAT";
	private final String url="https://mtgadecks.net";
	
	public static void main(String[] args) throws IOException {
	 new MTGADecksSniffer().getDeckList();
	} 
	
	
	@Override
	public String[] listFilter() {
		return new String[] {"Control","Aggro","Combo","Midrange","Aggro-Control"};
	}

	@Override
	public MagicDeck getDeck(RetrievableDeck info) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RetrievableDeck> getDeckList() throws IOException {
		
		List<RetrievableDeck> ret = new ArrayList<>();
		
		RequestBuilder e = RequestBuilder.build()
				 .setClient(URLTools.newClient())
				 .method(METHOD.GET)
				 .url(url+"/serverSide")
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
				 
				 if(!getString(FORMAT).isEmpty())
					 e.addContent("data", "archetype="+ArrayUtils.indexOf(listFilter(), getString(FORMAT)));
					 
					 
				 JsonArray arr = e.toJson().getAsJsonObject().get("data").getAsJsonArray();

				 arr.forEach(a->{
					
					 RetrievableDeck deck = new RetrievableDeck();
					 
					 try {
						deck.setUrl(new URI(url+URLTools.toHtml(a.getAsJsonArray().get(0).getAsString()).select("a").attr("href")));
					 } catch (URISyntaxException e1) {
						logger.error(e1);
					 }
					 
					 
					 String name = URLTools.toHtml(a.getAsJsonArray().get(0).getAsString()).select("a").text();
				 			name = name.substring(0,name.indexOf(" by "));
				 			name = RegExUtils.replaceAll(name, "BO1","").trim();
					 
					 deck.setName(name);
					 
					 System.out.println(deck.getName());
					 ret.add(deck);
					 
				 });

				return ret;
	}

	@Override
	public String getName() {
		return "MTGADecks";
	}

	@Override
	public void initDefault() {
		setProperty(FORMAT, "");
	}

	
}
