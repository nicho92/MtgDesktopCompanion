package org.magic.api.decksniffer.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class MTGTop8DeckSniffer extends AbstractDeckSniffer {

	Map<String,String> formats;
	
	public MTGTop8DeckSniffer() {
		super();
		initFormats();
	}
	
	private void initFormats() {
		formats=new HashMap<>();
		formats.put("Standard", "ST");
		formats.put("Modern", "MO");
		formats.put("Legacy", "LE");
		formats.put("Vintage", "VI");
		formats.put("Duel Commander", "EDH");
		formats.put("MTGO Commander", "EDHM");
		formats.put("Block", "BL");
		formats.put("Extended", "EX");
		formats.put("Pauper", "PAU");
		formats.put("Highlander", "HIGH");
		formats.put("Canadian Highlander", "CHL");
		formats.put("Limited", "LI");
		
	}

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

	@Override
	public String[] listFilter() {
		return formats.keySet().toArray(new String[formats.keySet().size()]);
	}

	@Override
	public MagicDeck getDeck(RetrievableDeck info) throws IOException {
		Document root = Jsoup.connect(info.getUrl().toString()).userAgent(getString("USER_AGENT")).timeout(0).get();
		MagicDeck d = new MagicDeck();
				  d.setDescription(info.getUrl().toString());
				  d.setName(info.getName());
				  d.setDateCreation(new Date());
				  
					
			Elements doc =root.select("table.Stable").get(1).select("td table").select(MTGConstants.HTML_TAG_TD);
				  
		boolean side=false;		  
		for(Element e : doc.select("td table td"))
		{
			
			if(e.hasClass("O13"))
			{
				if(e.text().equalsIgnoreCase("SIDEBOARD"))
					side=true;
			}
			else
			{

				int qte = Integer.parseInt(e.text().substring(0,e.text().indexOf(' ')));
				String name = e.select("span.L14").text();
				if(!name.equals(""))
				{
					MagicCard mc = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", name, null, true).get(0);
					if(!side)
						d.getMap().put(mc, qte);
					else
						d.getMapSideBoard().put(mc, qte);
				}
			}
			
			
		}
		
		return d;
	}

	@Override
	public List<RetrievableDeck> getDeckList() throws IOException {
		HttpClient httpClient = HttpClientBuilder.create().build();
		
		StringBuilder res=new StringBuilder();
		for(int i=0;i<Integer.parseInt(getString("MAX_PAGE"));i++)
		{
			HttpPost reqSearch = new HttpPost(getString("URL")+"/search");
			List <NameValuePair> nvps = new ArrayList <>();
								 nvps.add(new BasicNameValuePair("current_page", String.valueOf(i+1)));
						         nvps.add(new BasicNameValuePair("event_titre", getString("EVENT_FILTER")));
						         nvps.add(new BasicNameValuePair("deck_titre", ""));
						         nvps.add(new BasicNameValuePair("player", ""));
						         nvps.add(new BasicNameValuePair("format", formats.get(getString("FORMAT"))));
						         nvps.add(new BasicNameValuePair("MD_check", "1"));
						         nvps.add(new BasicNameValuePair("cards", getString("CARD_FILTER")));
						         nvps.add(new BasicNameValuePair("date_start", getString("DATE_START_FILTER")));
						         nvps.add(new BasicNameValuePair("date_end", ""));
						         
						         if(getString("COMPETITION_FILTER")!=null)
				        		 {
				        			 String[] comp = getString("COMPETITION_FILTER").split(",");
				        			 for(String c : comp)
				        				 nvps.add(new BasicNameValuePair(" compet_check["+c.toUpperCase()+"]", "1"));
				        		 }
						         
						         
			reqSearch.setEntity(new UrlEncodedFormEntity(nvps));
			
			logger.debug("snif decks : " + reqSearch.toString());
			
			HttpResponse rep = httpClient.execute(reqSearch);
			res.append(EntityUtils.toString(rep.getEntity()));
		}
		
		Document d = Jsoup.parse(res.toString());
		Elements els = d.select("tr.hover_tr");
		
		List<RetrievableDeck> ret = new ArrayList<>();
		for(int i = 0; i <els.size();i++)
		{
			Element e = els.get(i);
			RetrievableDeck dk = new RetrievableDeck();
							dk.setName(e.select("td.s11 a").text());
							try {
								dk.setUrl(new URI(getString("URL")+e.select("td.s11 a").attr("href")));
							} catch (URISyntaxException e1) {
								dk.setUrl(null);
							}
							dk.setAuthor(e.select("td.g11 a").text());
							dk.setDescription(e.select("td.S10 a").text());
			ret.add(dk);
		}
		
		return ret;
	}

	@Override
	public void connect() throws IOException {
		//Nothing to do

	}

	@Override
	public String getName() {
		return "MTGTop8";
	}

	@Override
	public void initDefault() {
		setProperty("USER_AGENT", MTGConstants.USER_AGENT);
		setProperty("URL", "http://mtgtop8.com/");
		setProperty("EVENT_FILTER", "");
		setProperty("FORMAT", "Standard");
		setProperty("MAX_PAGE", "2");
		setProperty("TIMEOUT", "0");
		setProperty("CARD_FILTER", "");
		setProperty("COMPETITION_FILTER", "P,M,C,R");
		setProperty("DATE_START_FILTER", "");
		
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

}
