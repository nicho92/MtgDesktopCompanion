package org.magic.api.decksniffer.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.util.EntityUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.URLTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AetherhubDeckSniffer extends AbstractDeckSniffer {

	private static final String FORMAT = "FORMAT";
	private static final String URL = "URL";
	private HttpClient httpclient;
	private Map<String,String> formats;
	
	
	public AetherhubDeckSniffer() {
		super();
		
		formats = new HashMap<>();	
		formats.put("All", "");
		formats.put("Standard", "?formatId=1");
		formats.put("Modern", "?formatId=2");
		formats.put("Commander", "?formatId=3");
		formats.put("Legacy", "?formatId=4");
		formats.put("Vintage", "?formatId=5");
		formats.put("MTG Arena", "?formatId=6");
		formats.put("Commander 1vs1", "?formatId=7");
		formats.put("Frontier", "?formatId=8");
		formats.put("Pauper", "?formatId=9");
		formats.put("Brawl", "?formatId=10");
		
		
		httpclient = HttpClients.custom().setUserAgent(MTGConstants.USER_AGENT)
				 .setRedirectStrategy(new LaxRedirectStrategy()).build();

	}

	@Override
	public String[] listFilter() {
		return formats.keySet().toArray(new String[formats.keySet().size()]);
	}

	@Override
	public MagicDeck getDeck(RetrievableDeck info) throws IOException {
		
		logger.debug("get deck from " + info.getUrl());
		Document d =URLTools.extractHtml(info.getUrl().toString());
		Elements div = d.select("div#tab_text");
		MagicDeck deck = new MagicDeck();
		deck.setName(info.getName());
		boolean sideboard=false;
		String[] lines = div.html().replaceAll("</h4>", "<br>").split("<br>");
		for(int i=1;i<lines.length;i++)
		{
			String line=lines[i].trim();
			
			if(line.startsWith("<h4>Sideboard"))
			{
				sideboard=true;
			}
			else
			{
				Integer qte = Integer.parseInt(line.substring(0, line.indexOf(' ')));
				String cardName = line.substring(line.indexOf(' '), line.length()).trim();
				
				if(sideboard)
					deck.getMapSideBoard().put(MTGControler.getInstance().getEnabledCardsProviders().searchCardByName(cardName, null, true).get(0), qte);
				else
					deck.getMap().put(MTGControler.getInstance().getEnabledCardsProviders().searchCardByName(cardName, null, true).get(0), qte);
			}
		}
		return deck;
	}

	@Override
	public List<RetrievableDeck> getDeckList() throws IOException {
		List<RetrievableDeck> list = new ArrayList<>();
		
		HttpResponse resp = httpclient.execute(new HttpGet(getString(URL)+formats.get(getString(FORMAT))));
		String ret = EntityUtils.toString(resp.getEntity());
		JsonObject el = new JsonParser().parse(ret).getAsJsonObject();
		JsonArray arr = el.get("metadecks").getAsJsonArray();
		
		for(JsonElement je : arr)
		{
			RetrievableDeck d = new RetrievableDeck();
						    d.setAuthor(je.getAsJsonObject().get("username").getAsString());
						    d.setName(je.getAsJsonObject().get("name").getAsString());
						    d.setDescription(je.getAsJsonObject().get("updated").toString());
						    
						    JsonArray colors = je.getAsJsonObject().get("color").getAsJsonArray();
						    StringBuilder temp = new StringBuilder();
						    if (colors.get(0).getAsInt() > 0) {
	                           temp.append("{W}");
	                        }
	                        if (colors.get(1).getAsInt() > 0) {
	                        	temp.append("{U}");
	                        }
	                        if (colors.get(2).getAsInt() > 0) {
	                        	temp.append("{B}");
	                        }
	                        if (colors.get(3).getAsInt() > 0) {
	                        	temp.append("{R}");
	                        }
	                        if (colors.get(4).getAsInt() > 0) {
	                        	temp.append("{G}");
	                        }
	                        d.setColor(temp.toString());
	                        
	                        try {
								d.setUrl(new URI("https://aetherhub.com/Deck/Public?id="+je.getAsJsonObject().get("id").getAsInt()));
							} catch (URISyntaxException e) {
								logger.error(e);
							}
	                       
						    list.add(d);
		}
		return list;
	}


	@Override
	public String getName() {
		return "Aetherhub";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public void initDefault() {
		setProperty(URL, "https://aetherhub.com/Meta/FetchMetaList");
		setProperty(FORMAT, "All");	
	}

	@Override
	public String getVersion() {
		return "0.3";
	}



}
