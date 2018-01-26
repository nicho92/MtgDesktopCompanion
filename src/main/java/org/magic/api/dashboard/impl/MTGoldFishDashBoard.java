package org.magic.api.dashboard.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.CardDominance;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MagicCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractDashBoard;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;

public class MTGoldFishDashBoard extends AbstractDashBoard{
	private Date updateTime;
	private Map<Date,Double> historyPrice;
    boolean stop ;	    
    private Map<String,String> mapConcordance;
    
    
    
	@Override
	public STATUT getStatut() {
		return STATUT.STABLE;
	}
	
	public MTGoldFishDashBoard() 
	{
		super();
		initConcordance();
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("URL_MOVERS", "http://www.mtggoldfish.com/movers-details/");
			props.put("URL_EDITIONS", "http://www.mtggoldfish.com/index/");
			props.put("WEBSITE", "http://www.mtggoldfish.com/");
			props.put("USER_AGENT", "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6");
			props.put("FORMAT", "paper");
			props.put("TIMEOUT", "0");
			props.put("DAILY_WEEKLY", "wow");
		save();
		}
	}
	


	
	public Map<Date,Double> getPriceVariation(MagicCard mc,MagicEdition me) throws IOException {
		 
		stop = false;	    
		String url ="";
		historyPrice = new TreeMap<>();
		int index=0;
		
		if(me==null)
			 me=mc.getEditions().get(0);
		 
		 if(mc==null)
		 {
			 url = props.getProperty("URL_EDITIONS")+replace(me.getId(),false)+"#"+props.getProperty("FORMAT");
			 index=6;
		 }
		 else
		 {
			 String cardName=mc.getName().replaceAll(" ", "+").replaceAll("'", "").replaceAll(",", "");
			 String editionName=me.toString().replaceAll(" ", "+").replaceAll("'", "").replaceAll(",", "").replaceAll(":","");
			 url =props.getProperty("WEBSITE")+"/price/"+convert(editionName)+"/"+cardName+"#"+props.getProperty("FORMAT");
			 index=8;
		
		 }
		 
		 
		try{
		 
		 logger.debug("get shakes from " + url);
	    
		 Document d = Jsoup.connect(url)
	    		 	.userAgent(props.getProperty("USER_AGENT"))
					.timeout(Integer.parseInt(props.get("TIMEOUT").toString()))
					.get();
			 
		 Element js = d.getElementsByTag("script").get(index);
		 
		 
	     AstNode node = new Parser().parse(js.html(), "", 1);
	     		 node.visit( new NodeVisitor() {
	 	             public boolean visit(AstNode node) 
	 	             {
	 	            	 
	    	        		 if(stop==false)
	    	        		 {
	    	        			 if(node.toSource().startsWith("d"))
		    	        		 {
		    	        			 String val = node.toSource();
		    	        			 val=val.replaceAll("d \\+\\= ", "");
		    	        			 val=val.replaceAll("\\\\n", "");
		    	        			 val=val.replaceAll(";", "");
		    	        			 val=val.replaceAll("\"", "");
		    	        			 String[] res = val.split(",");
		    	        			try {
		    	        				Date d = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(res[0]+ " 00:00");
		    							if(historyPrice.get(d)==null)
		    								historyPrice.put(d, Double.parseDouble(res[1]));
		    						} 
		    	        			catch (Exception e) {
		    							logger.error(e);
		    						} 
		    	        		 }
	    	        		 }
	    	        		 
	    	        		 if(node.toSource().startsWith("g ="))
	    	        		 {
	    	        			 stop=true;
	    	        		 }
	    	        	 
	    	        	return true;
	    	         }});

	    return historyPrice;
	    
		}catch(Exception e)
		{
			logger.error(e);
			return historyPrice;
		}
	}
	
	public List<CardShake> getShakerFor(String gameFormat) throws IOException
	{
		
		String urlW= props.getProperty("URL_MOVERS")+props.getProperty("FORMAT")+"/"+gameFormat+"/winners/"+props.getProperty("DAILY_WEEKLY");
		String urlL= props.getProperty("URL_MOVERS")+props.getProperty("FORMAT")+"/"+gameFormat+"/losers/"+props.getProperty("DAILY_WEEKLY");
		
		
		logger.debug("Loding Shake " + urlW);
		logger.debug("Loding Shake " + urlL);
		
		
		
		Document doc = Jsoup.connect(urlW)
							.userAgent(props.getProperty("USER_AGENT"))
							.timeout(Integer.parseInt(props.get("TIMEOUT").toString()))
							.get();
		
		Document doc2 = Jsoup.connect(urlL)
				.userAgent(props.getProperty("USER_AGENT"))
				.timeout(Integer.parseInt(props.get("TIMEOUT").toString()))
				.get();
		
		
		try {
			updateTime= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(doc.getElementsByClass("timeago").get(0).attr("title"));
		} catch (ParseException e1) {
			logger.error(e1);
		}
		
		Element table =null;
		try{
		
		table = doc.select("table").get(0).getElementsByTag("tbody").get(0).appendChild(doc2.select("table").get(0).getElementsByTag("tbody").get(0));//combine 2 results
		
		List<CardShake> list = new ArrayList<>();
		
		
		for(Element e : table.getElementsByTag("tr"))
		{
			CardShake cs = new CardShake();
					cs.setName(e.getElementsByTag("TD").get(3).text().replaceAll("\\(RL\\)", "").trim());
					cs.setImg(new URL("http://"+e.getElementsByTag("TD").get(3).getElementsByTag("a").get(0).attr("data-full-image")));
					cs.setPrice(parseDouble(e.getElementsByTag("TD").get(4).text()));
					cs.setPriceDayChange(parseDouble(e.getElementsByTag("TD").get(1).text()));
					cs.setPercentDayChange(parseDouble(e.getElementsByTag("TD").get(5).text()));
			
			String set = e.getElementsByTag("TD").get(2).getElementsByTag("img").get(0).attr("alt");
			cs.setEd(replace(set,true));
			
			list.add(cs);
			
		}
		return list;
		
		
		}
		catch(IndexOutOfBoundsException e)
		{
			logger.error(e);
		}
		return null;
		
		
		
	}
	
	public List<CardShake> getShakeForEdition(MagicEdition edition) throws IOException
	{
		String oldID=edition.getId();
		String urlEditionChecker = "";
		
		if(edition.isOnlineOnly())
				urlEditionChecker=props.getProperty("URL_EDITIONS")+replace(edition.getId().toUpperCase(),false)+"#online";
			else
				urlEditionChecker=props.getProperty("URL_EDITIONS")+replace(edition.getId().toUpperCase(),false)+"#"+props.getProperty("FORMAT");
		
		logger.debug("Parsing dashboard "+ urlEditionChecker);
		
		Document doc = Jsoup.connect(urlEditionChecker)
							.userAgent(props.getProperty("USER_AGENT"))
							.timeout(Integer.parseInt(props.get("TIMEOUT").toString()))
							.get();
		
		
		
		
		Element table =null;
		try{
			List<CardShake> list = new ArrayList<>();
			
		table = doc.select("table").get(1).getElementsByTag("tbody").get(0);
		
		for(Element e : table.getElementsByTag("tr"))
		{
			CardShake cs = new CardShake();
				
				cs.setName(e.getElementsByTag("TD").get(0).text().replaceAll("\\(RL\\)", "").trim());
				cs.setImg(new URL("http://"+e.getElementsByTag("TD").get(0).getElementsByTag("a").get(0).attr("data-full-image")));
				cs.setRarity(e.getElementsByTag("TD").get(2).text());
				cs.setPrice(parseDouble(e.getElementsByTag("TD").get(3).text()));
				cs.setPriceDayChange(parseDouble(e.getElementsByTag("TD").get(4).text()));
				cs.setPercentDayChange(parseDouble(e.getElementsByTag("TD").get(5).text()));
				cs.setPriceWeekChange(parseDouble(e.getElementsByTag("TD").get(6).text()));
				cs.setPercentWeekChange(parseDouble(e.getElementsByTag("TD").get(7).text()));
				//cs.setEd(e.getElementsByTag("TD").get(1).text());
				cs.setEd(oldID);
				cs.setDateUpdate(new Date());
				
			list.add(cs);
		}
		return list;
		
		
		}
		catch(IndexOutOfBoundsException e)
		{
			logger.error(e);
		}
		return null;
		
		
	}
	
	
	@Override
	public List<CardDominance> getBestCards(FORMAT f,String filter) throws IOException {
		
		//spells, creatures, all, lands
		String u = new String("https://www.mtggoldfish.com/format-staples/"+f+"/full/"+filter);
		Document doc = Jsoup.connect(u)
				.userAgent(props.getProperty("USER_AGENT"))
				.timeout(Integer.parseInt(props.get("TIMEOUT").toString()))
				.get();
		
		logger.debug("get best cards : " + u);
		Elements trs =doc.select("table tr");
		trs.remove(0);
		trs.remove(0);
		List<CardDominance> ret = new ArrayList<>();
		for(Element e : trs)
		{
			Elements tds = e.select("td");
			try {
				int correct = filter.equalsIgnoreCase("lands")?1:0;
				
				
				
			CardDominance d = new CardDominance();
						  d.setPosition(Integer.parseInt(tds.get(0).text()));
						  d.setCardName(tds.get(1).text());
						  d.setDominance(Double.parseDouble(tds.get(3-correct).text().replaceAll("\\%", "")));
						  d.setDecksPercent(Double.parseDouble(tds.get(4-correct).text().replaceAll("\\%", "")));
						  d.setPlayers(Double.parseDouble(tds.get(5-correct).text()));
						  ret.add(d);
			}
			catch(Exception ex)
			{
				logger.error("Error parsing " + tds,ex);
			}
			
		}
		return ret;
	}
	
	private String convert(String editionName)
	{
		
		switch(editionName){
			case "Grand+Prix"        : return "Grand+Prix+Promos";
			case "Prerelease+Events" : return "Prerelease+Cards";
			case "Champs+and+States" : return "Champs+Promos";
			case "Ugins+Fate+promos" : return "Ugins+Fate+Promos";
			case "Magic+Game+Day"    : return "Game+Day+Promos";
			case "Media+Inserts"     : return "Media+Promos";
			case "Judge+Gift+Program": return "Judge+Promos";
			case "Friday+Night+Magic": return "FNM+Promos";
			case "Arena+League":	   return "Arena+Promos";
			case "Masterpiece+Series+Amonkhet+Invocations" : return "Amonkhet+Invocations";
			case "Masterpiece+Series+Kaladesh+Inventions" : return "Kaladesh+Inventions";
			case "You+Make+the+Cube" : return "Treasure+Chest";
			default : return editionName;
		}
	}
	
	private void initConcordance() {
		mapConcordance = new HashMap<>();
		
		mapConcordance.put("TMP", "TE");
		mapConcordance.put("STH", "ST");
		mapConcordance.put("PCY", "PR");
		mapConcordance.put("MIR", "MI");
		mapConcordance.put("UDS", "UD");
		mapConcordance.put("NMS", "NE");
		mapConcordance.put("ULG", "UL");
		mapConcordance.put("USG", "UZ");
		mapConcordance.put("WTH", "WL");
		mapConcordance.put("ODY", "OD");
		mapConcordance.put("EXO", "EX");
		mapConcordance.put("APC", "AP");
		mapConcordance.put("PLS", "PS");
		mapConcordance.put("INV", "IN");
		mapConcordance.put("MMQ", "MM");
		mapConcordance.put("VIS", "VI");
		mapConcordance.put("7ED", "7E");
		mapConcordance.put("MPS", "MS2");
		mapConcordance.put("MPS_AKH","MS3");
		mapConcordance.put("pGRU", "PRM-GUR");
		mapConcordance.put("pMGD", "PRM-GDP");
		mapConcordance.put("pMEI", "PRM-MED");
		mapConcordance.put("pJGP", "PRM-JUD");
		mapConcordance.put("pGPX", "PRM-GPP");
		mapConcordance.put("pFNM", "PRM-FNM");
		mapConcordance.put("pARL", "PRM-ARN");
		//p15A
		
	}
	
	
	public String replace(String id, boolean byValue) {
		
		if(byValue)
		{
			 for (Entry<String, String> entry : mapConcordance.entrySet()) {
			        if (Objects.equals(id, entry.getValue())) {
			            return entry.getKey();
			        }
			    }
		}
		else
		{ 
			if(mapConcordance.get(id)!=null)
				return mapConcordance.get(id);
		}
			
		
		
		return id;
		
		
	}


	private double parseDouble(String number)
	{
		return Double.parseDouble(number.replaceAll(",","").replaceAll("%", ""));
	}
		
	@Override
	public String getName() {
		return "MTGoldFish";
	}

	@Override
	public Date getUpdatedDate() {
		return updateTime;
	}

	
	@Override
	public String[]  getDominanceFilters() {
		return new String[] { "all","spells", "creatures","lands"};
	}

	

}
