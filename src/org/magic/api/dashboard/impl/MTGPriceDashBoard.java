package org.magic.api.dashboard.impl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractDashBoard;
import org.magic.services.MTGControler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MTGPriceDashBoard extends AbstractDashBoard {

	static final Logger logger = LogManager.getLogger(MTGPriceDashBoard.class.getName());

	private Date updateTime;
	
	public MTGPriceDashBoard() 
	{
		super();
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("PERIOD", "WEEKLY");
			props.put("WEBSITE", "http://www.mtgprice.com");
			props.put("USER_AGENT", "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6");
			props.put("TIMEOUT", "0");
		save();
		}
	}
	
	@Override
	public List<CardShake> getShakerFor(String gameFormat) throws IOException {

		String url = props.getProperty("WEBSITE")+"/taneLayout/mtg_price_tracker.jsp?period="+props.getProperty("PERIOD");
		Document doc = Jsoup.connect(url)
							.userAgent(props.getProperty("USER_AGENT"))
							.timeout(Integer.parseInt(props.get("TIMEOUT").toString()))
							.get();
		try {
			logger.debug("Get Shake for " + url );
			String date = doc.getElementsByClass("span6").get(1).text().replaceAll("Updated:", "").replaceAll("UTC ", "").trim();
			SimpleDateFormat forma = new SimpleDateFormat("E MMMM dd hh:mm:ss yyyy", Locale.ENGLISH);
			updateTime= forma.parse(date);
		} catch (ParseException e1) {
			logger.error(e1);
		}
		
		if(gameFormat.toUpperCase().equals("STANDARD"))
			gameFormat="Standard";
		else
		if(gameFormat.toUpperCase().equals("MODERN"))
			gameFormat="Modern";
		else
		if(gameFormat.toUpperCase().equals("VINTAGE"))
			gameFormat="Vintage";
		else
		if(gameFormat.toUpperCase().equals("LEGACY"))
			gameFormat="All";
		
		Element table =doc.getElementById("top50"+gameFormat);
		Element table2 =doc.getElementById("bottom50"+gameFormat);
		
		try{
			List<CardShake> list = new ArrayList<CardShake>();
		
			for(Element e : table.select("tr"))
			{
				CardShake cs = new CardShake();
						cs.setName(e.getElementsByTag("TD").get(0).text().trim());
						cs.setPrice(parseDouble(e.getElementsByTag("TD").get(2).text()));
						cs.setPriceDayChange(parseDouble(e.getElementsByTag("TD").get(4).text()));
						
						String set = e.getElementsByTag("TD").get(1).text();
						set =set.replaceAll("_\\(Foil\\)", "");
						cs.setEd(getCodeForExt(set));
			
				list.add(cs);
				
			}
			
			for(Element e : table2.select("tr"))
			{
				CardShake cs = new CardShake();
						cs.setName(e.getElementsByTag("TD").get(0).text().trim());
						cs.setPrice(parseDouble(e.getElementsByTag("TD").get(2).text()));
						cs.setPriceDayChange(parseDouble(e.getElementsByTag("TD").get(4).text()));
						
						String set = e.getElementsByTag("TD").get(1).text();
						set =set.replaceAll("_\\(Foil\\)", "");
						cs.setEd(getCodeForExt(set));
				list.add(cs);
			}
			
			return list;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
		
	private double parseDouble(String number)
	{
		return Double.parseDouble(number.replaceAll("\\$", ""));
	}
		
	private String getCodeForExt(String name)
	{
		try {
			for(MagicEdition ed : MTGControler.getInstance().getEnabledProviders().loadEditions())
				if(ed.getSet().toUpperCase().contains(name.toUpperCase()))
					return ed.getId();
		} catch (Exception e) {
			logger.error(e);
		}
		
		return name;
	}

	@Override
	public List<CardShake> getShakeForEdition(MagicEdition edition) throws IOException {
		
		String name = edition.getSet().replaceAll(" ", "_");
		
	
		String url = "http://www.mtgprice.com/spoiler_lists/"+name;
		logger.debug("get Prices for " + name + " " + url);
		
		
		
		Document doc = Jsoup.connect(url)
				.userAgent(props.getProperty("USER_AGENT"))
				.timeout(Integer.parseInt(props.get("TIMEOUT").toString()))
				.get();
		
			Element table =doc.getElementsByTag("body").get(0).getElementsByTag("script").get(2);
		
			List<CardShake> list = new ArrayList<CardShake>();
			String data = table.html();
			data = data.substring(data.indexOf("["),data.indexOf("]")+1);
			JsonElement root = new JsonParser().parse(data);
			JsonArray arr = root.getAsJsonArray();
			for(int i = 0;i<arr.size();i++)
			{
				JsonObject card = arr.get(i).getAsJsonObject();
				CardShake shake = new CardShake();
				
				shake.setName(card.get("name").getAsString());
				shake.setEd(edition.getId());
				shake.setPrice(card.get("fair_price").getAsDouble());
				try {
					shake.setPercentDayChange(card.get("percentageChangeSinceYesterday").getAsDouble());
				} catch (Exception e) {
					
				}
				try {
					shake.setPercentWeekChange(card.get("percentageChangeSinceOneWeekAgo").getAsDouble());
				} catch (Exception e) {
					
				}
				shake.setPriceDayChange(card.get("absoluteChangeSinceYesterday").getAsDouble());
				shake.setPriceWeekChange(card.get("absoluteChangeSinceOneWeekAgo").getAsDouble());
				
				
				
				list.add(shake);
			}
			
			
		return list;
	}

	@Override
	public Map<Date, Double> getPriceVariation(MagicCard mc, MagicEdition me) throws IOException {
		
		Map<Date,Double> historyPrice = new TreeMap<Date,Double>();
		String name="";
		
		if(mc!=null)
			name=mc.getName().replaceAll(" ", "_");
		
		
		String edition="";
		
		if(me==null)
			edition=mc.getEditions().get(0).getSet();
		else
			edition=me.getSet();	
		
		edition=edition.replaceAll(" ", "_");
		
		 String url = "http://www.mtgprice.com/sets/"+edition+"/"+name;
		 Document d = Jsoup.connect(url)
	    		 	.userAgent(props.getProperty("USER_AGENT"))
					.timeout(Integer.parseInt(props.get("TIMEOUT").toString()))
					.get();
		 
		 logger.debug("get Prices for " + name + " " + url);
			
		 
		 Element js = d.getElementsByTag("body").get(0).getElementsByTag("script").get(29);
	 
		 String html = js.html();
		 html=html.substring(html.indexOf("[[")+1, html.indexOf("]]")+1);
		 
		 Pattern p = Pattern.compile("\\[(.*?)\\]");
		 Matcher m = p.matcher(html);	
		 while(m.find()) {
			    
			 Date date = new Date(Long.parseLong(m.group(1).split(",")[0]));
			 Double val = Double.parseDouble(m.group(1).split(",")[1]);
			 
			 historyPrice.put(date, val);
		 }
		return historyPrice;
	}

	@Override
	public String getName() {
		return "MTGPrice";
	}

	@Override
	public Date getUpdatedDate() {
		return updateTime;
	}

}
