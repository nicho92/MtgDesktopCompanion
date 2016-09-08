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
import java.util.TreeMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractDashBoard;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;

public class MTGoldFishDashBoard extends AbstractDashBoard{

	static final Logger logger = LogManager.getLogger(MTGoldFishDashBoard.class.getName());

	private Date updateTime;
	Map<Date,Double> historyPrice;
    boolean stop ;	    
	
	Map<String,String> mapConcordance = new HashMap<String,String>();
	

	
	public MTGoldFishDashBoard() 
	{
		super();
		
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
		historyPrice = new TreeMap<Date,Double>();
		int index=0;
		
		if(me==null)
			 me=mc.getEditions().get(0);
		 
		 if(mc==null)
		 {
			 url = props.getProperty("URL_EDITIONS")+replace(me.getId())+"#"+props.getProperty("FORMAT");
			 index=3;
		 }
		 else
		 {
			 String cardName=mc.getName().replaceAll(" ", "+").replaceAll("'", "").replaceAll(",", "");
			 String editionName=me.toString().replaceAll(" ", "+").replaceAll("'", "").replaceAll(",", "").replaceAll(":","");
			 url =props.getProperty("WEBSITE")+"/price/"+editionName+"/"+cardName+"#"+props.getProperty("FORMAT");
			 index=5;
		
		 }
		 
		 
		try{
		 
		 logger.debug(url);
	    
		 Document d = Jsoup.connect(url)
	    		 	.userAgent(props.getProperty("USER_AGENT"))
					.timeout(Integer.parseInt(props.get("TIMEOUT").toString()))
					.get();
		 
		 
	     Element js = d.getElementsByTag("body").get(0).getElementsByTag("script").get(index);
	     AstNode node = new Parser().parse(js.html(), "", 1);
	     		 node.visit( new NodeVisitor() {
	 	        	
	    	         public boolean visit(AstNode node) {
	    	        	
	    	        	 if(node.getType()==133)
	    	        	 {
	    	        		 
	    	        		 if(stop==false)
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
	    							
	    						} catch (Exception e) {
	    							logger.error(e);
	    						} 
	    	        		 }
	    	        		 
	    	        		 if(node.toSource().startsWith("g ="))
	    	        		 {
	    	        			 stop=true;
	    	        		 }
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
		Document doc = Jsoup.connect(props.getProperty("URL_MOVERS")+props.getProperty("FORMAT")+"/"+gameFormat.toString()+"/winners/"+props.getProperty("DAILY_WEEKLY"))
							.userAgent(props.getProperty("USER_AGENT"))
							.timeout(Integer.parseInt(props.get("TIMEOUT").toString()))
							.get();
		
		Document doc2 = Jsoup.connect(props.getProperty("URL_MOVERS")+props.getProperty("FORMAT")+"/"+gameFormat+"/losers/"+props.getProperty("DAILY_WEEKLY"))
				.userAgent(props.getProperty("USER_AGENT"))
				.timeout(Integer.parseInt(props.get("TIMEOUT").toString()))
				.get();
		
		
		try {
			updateTime= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(doc.getElementsByClass("timeago").get(0).attr("title"));
		} catch (ParseException e1) {
			logger.error(e1);
		}
		logger.debug("Parsing dashboard "+getName()+props.getProperty("URL_MOVERS")+props.getProperty("FORMAT")+"/"+gameFormat+"/losers/"+props.getProperty("DAILY_WEEKLY"));
		
		Element table =null;
		try{
		
		table = doc.select("table").get(0).getElementsByTag("tbody").get(0).appendChild(doc2.select("table").get(0).getElementsByTag("tbody").get(0));//combine 2 results
		
		List<CardShake> list = new ArrayList<CardShake>();
		
		
		for(Element e : table.getElementsByTag("tr"))
		{
			CardShake cs = new CardShake();
			cs.setName(e.getElementsByTag("TD").get(3).text().replaceAll("\\(RL\\)", "").trim());
			cs.setImg(new URL("http://"+e.getElementsByTag("TD").get(3).getElementsByTag("a").get(0).attr("data-full-image")));
			cs.setPrice(parseDouble(e.getElementsByTag("TD").get(4).text()));
			cs.setPriceDayChange(parseDouble(e.getElementsByTag("TD").get(1).text()));
			cs.setPercentDayChange(parseDouble(e.getElementsByTag("TD").get(5).text()));
			cs.setEd(e.getElementsByTag("TD").get(2).getElementsByTag("img").get(0).attr("alt"));
			
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
		String urlEditionChecker = props.getProperty("URL_EDITIONS")+replace(edition.getId())+"#"+props.getProperty("FORMAT");
		
		logger.debug("Parsing dashboard "+ urlEditionChecker);
		
		Document doc = Jsoup.connect(urlEditionChecker)
							.userAgent(props.getProperty("USER_AGENT"))
							.timeout(Integer.parseInt(props.get("TIMEOUT").toString()))
							.get();
		
		
		
		
		Element table =null;
		try{
			List<CardShake> list = new ArrayList<CardShake>();
			
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
	
	public String replace(String id) {
		
		
		switch(id){
			case "TMP" : return "TE";
			case "STH" : return "ST";
			case "PCY" : return "PR";
			case "MIR" : return "MI";
			case "UDS" : return "UD";
			case "ULG" : return "UL";
			case "USG" : return "UZ";
			case "WTH" : return "WL";
			case "ODY" : return "OD";
			case "EXO": return "EX";
			case "APC": return "AP";
			case "pGRU": return "PRM-GUR";
			case "PLS" : return "PS";
			case "INV" : return "IN";
			case "MMQ" : return "MM";
			case "VIS" : return "VI";
		default : return id;
		}
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
	public String toString() {
		return getName();
	}
	
}
