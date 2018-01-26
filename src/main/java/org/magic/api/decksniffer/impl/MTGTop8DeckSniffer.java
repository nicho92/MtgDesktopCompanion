package org.magic.api.decksniffer.impl;

import java.io.File;
import java.net.URI;
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
import org.magic.api.interfaces.MagicCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.MTGControler;

public class MTGTop8DeckSniffer extends AbstractDeckSniffer {

	Map<String,String> formats;
	
	public MTGTop8DeckSniffer() {
		super();
		
		initFormats();
		
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("USER_AGENT", "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.10 (maverick) Firefox/3.6.13");
			props.put("URL", "http://mtgtop8.com/");
			props.put("EVENT_FILTER", "");
			props.put("FORMAT", "Standard");
			props.put("MAX_PAGE", "2");
			props.put("TIMEOUT", "0");
			props.put("CARD_FILTER", "");
			props.put("COMPETITION_FILTER", "P,M,C,R");
			props.put("DATE_START_FILTER", "");
			/*compet_check[P]:1
			compet_check[M]:1
			compet_check[C]:1
			compet_check[R]:1*/
			
			
			save();
		}
	}
	
	private void initFormats() {
		formats=new HashMap<String,String>();
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

	public static void main(String[] args) throws Exception {
		MTGTop8DeckSniffer snif = new MTGTop8DeckSniffer();
		RetrievableDeck d = snif.getDeckList().get(1);
		
		snif.getDeck(d);
		
	}

	@Override
	public String[] listFilter() {
		return formats.keySet().toArray(new String[formats.keySet().size()]);
	}

	@Override
	public MagicDeck getDeck(RetrievableDeck info) throws Exception {
		Document root = Jsoup.connect(info.getUrl().toString()).userAgent(props.getProperty("USER_AGENT")).timeout(0).get();
		MagicDeck d = new MagicDeck();
				  d.setDescription(info.getUrl().toString());
				  d.setName(info.getName());
				  d.setDateCreation(new Date());
				  
					
			Elements doc =root.select("table.Stable").get(1).select("td table").select("td");
				  
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

				int qte = Integer.parseInt(e.text().substring(0,e.text().indexOf(" ")));
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
	public List<RetrievableDeck> getDeckList() throws Exception {
		HttpClient httpClient = HttpClientBuilder.create().build();
		
		StringBuilder res=new StringBuilder();
		for(int i=0;i<Integer.parseInt(props.getProperty("MAX_PAGE"));i++)
		{
			HttpPost reqSearch = new HttpPost(props.getProperty("URL")+"/search");
			List <NameValuePair> nvps = new ArrayList <NameValuePair>();
								 nvps.add(new BasicNameValuePair("current_page", String.valueOf(i+1)));
						         nvps.add(new BasicNameValuePair("event_titre", getProperty("EVENT_FILTER").toString()));
						         nvps.add(new BasicNameValuePair("deck_titre", ""));
						         nvps.add(new BasicNameValuePair("player", ""));
						         nvps.add(new BasicNameValuePair("format", formats.get(getProperty("FORMAT"))));
						         nvps.add(new BasicNameValuePair("MD_check", "1"));
						         nvps.add(new BasicNameValuePair("cards", getProperty("CARD_FILTER").toString()));
						         nvps.add(new BasicNameValuePair("date_start", getProperty("DATE_START_FILTER").toString()));
						         nvps.add(new BasicNameValuePair("date_end", ""));
						         
						         if(getProperty("COMPETITION_FILTER")!=null)
				        		 {
				        			 String[] comp = getProperty("COMPETITION_FILTER").toString().split(",");
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
		
		List<RetrievableDeck> ret = new ArrayList<RetrievableDeck>();
		for(int i = 0; i <els.size();i++)
		{
			Element e = els.get(i);
			RetrievableDeck dk = new RetrievableDeck();
							dk.setName(e.select("td.s11 a").text());
							dk.setUrl(new URI(props.getProperty("URL")+e.select("td.s11 a").attr("href")));
							dk.setAuthor(e.select("td.g11 a").text());
							dk.setDescription(e.select("td.S10 a").text());
			ret.add(dk);
		}
		
		return ret;
	}

	@Override
	public void connect() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return "MTGTop8";
	}

}
