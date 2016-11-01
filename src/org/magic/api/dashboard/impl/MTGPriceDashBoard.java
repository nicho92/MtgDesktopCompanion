package org.magic.api.dashboard.impl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractDashBoard;

public class MTGPriceDashBoard extends AbstractDashBoard {


	private Date updateTime;
	
	
	public static void main(String[] args) throws IOException {
		MTGPriceDashBoard dash = new MTGPriceDashBoard();
		
		dash.getShakerFor("standard");
		
		System.out.println(dash.getUpdatedDate());
	}
	
	public MTGPriceDashBoard() 
	{
		super();
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("PERIOD", "WEEKLY");
			props.put("WEBSITE", "http://www.mtgprice.com/");
			props.put("USER_AGENT", "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6");
			props.put("TIMEOUT", "0");
		save();
		}
	}
	
	
	@Override
	public List<CardShake> getShakerFor(String gameFormat) throws IOException {

		Document doc = Jsoup.connect(props.getProperty("WEBSITE")+"/taneLayout/mtg_price_tracker.jsp?period="+props.getProperty("PERIOD"))
							.userAgent(props.getProperty("USER_AGENT"))
							.timeout(Integer.parseInt(props.get("TIMEOUT").toString()))
							.get();
		
		try {
			String date = doc.getElementsByClass("span6").get(1).text().replaceAll("Updated:", "").replaceAll("UTC ", "").trim();
			SimpleDateFormat forma = new SimpleDateFormat("E MMMM dd hh:mm:ss yyyy", Locale.ENGLISH);
			updateTime= forma.parse(date);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		
		
		
		
		
		
		
		
		return null;
	}

	@Override
	public List<CardShake> getShakeForEdition(MagicEdition edition) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Date, Double> getPriceVariation(MagicCard mc, MagicEdition me) throws IOException {
		// TODO Auto-generated method stub
		return null;
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
