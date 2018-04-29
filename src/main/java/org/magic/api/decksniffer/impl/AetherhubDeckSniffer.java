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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.exports.impl.MTGArenaExport;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AetherhubDeckSniffer extends AbstractDeckSniffer {

	private HttpClient httpclient;
	private Map<String,String> formats;
	
	public static void main(String[] args) throws IOException {
		AetherhubDeckSniffer sniff = new AetherhubDeckSniffer();
		RetrievableDeck r = sniff.getDeckList().get(0);
		sniff.getDeck(r);
	}
	
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
		Document d = Jsoup.connect(info.getUrl().toString()).userAgent(MTGConstants.USER_AGENT).get();
		Element e = d.select("textarea#exportList").first();
		MagicDeck deck = new MagicDeck();
		deck.setName(info.getName());
		
		for (String line : e.text().split("\r\n"))
		{
			int qte = Integer.parseInt(line.substring(0, line.indexOf(' ')));
			String name = line.substring(line.indexOf(' '), line.indexOf('('));
			String ed =  line.substring( line.indexOf('(')+1,line.indexOf(')'));
			MagicEdition me = MTGControler.getInstance().getEnabledCardsProviders().getSetById(ed);
			deck.getMap().put(MTGControler.getInstance().getEnabledCardsProviders().searchCardByCriteria("name", name.trim(), me, true).get(0), qte);
		
		}
		
		Element t = d.select("div .decknotes").first();
		deck.setDescription(t.text());
		
		
	
		return deck;
	}

	@Override
	public List<RetrievableDeck> getDeckList() throws IOException {
		List<RetrievableDeck> list = new ArrayList<>();
		
		//todo concordance with FORMAT
		HttpResponse resp = httpclient.execute(new HttpGet(getString("URL")+formats.get(getString("FORMAT"))));
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
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	                       
						    list.add(d);
		}
		return list;
	}

	@Override
	public void connect() throws IOException {
		// TODO Auto-generated method stub

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
		setProperty("URL", "https://aetherhub.com/Meta/FetchMetaList");
		setProperty("FORMAT", "All");	
	}

	@Override
	public String getVersion() {
		return "0.1";
	}


}
