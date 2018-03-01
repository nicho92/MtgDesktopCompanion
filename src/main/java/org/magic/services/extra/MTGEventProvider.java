package org.magic.services.extra;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicEvent;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;

import com.google.gson.JsonParser;

public class MTGEventProvider {

	private String url =MTGConstants.WIZARD_EVENTS_URL;
	private Logger logger = MTGLogger.getLogger(this.getClass());
	

	
	public List<MagicEvent> listEvents(Date date) throws IOException
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH)+1;
		return listEvents(year,month);
	}
	
	private String read(String url) throws IOException
	{
		logger.debug("retrieve events from " + url);
		HttpClient httpClient = HttpClients.custom()
				   .setUserAgent(MTGConstants.USER_AGENT)
				   .setRedirectStrategy(new LaxRedirectStrategy())
				   .build();
		HttpGet req = new HttpGet(url);
				req.addHeader("content-type", "application/json");
				HttpResponse resp = httpClient.execute(req);
		return EntityUtils.toString(resp.getEntity());
	}
	
	
	public List<MagicEvent> listEvents(int y, int m) throws IOException
	{
		String link = url+y+"-"+m;
		List<MagicEvent> list = new ArrayList<>();
		
	
		
			String json = read(link);
			
			String e = new JsonParser().parse(json).getAsJsonObject().get("data").getAsString();
			Elements trs = Jsoup.parse(e).select("tr.multi-day,tr.single-day");
			for(Element td : trs.select(MTGConstants.HTML_TAG_TD))
			{
				
				if(!td.select("a").isEmpty())
				{
					MagicEvent event = new MagicEvent();
					int nbDay=Integer.parseInt(td.attr("colspan"));
					Date startDate;
					try {
						startDate = new SimpleDateFormat("yyyy-MM-dd").parse(td.attr("data-date"));
					} catch (java.text.ParseException e1) {
						logger.error(e1);
						startDate=new Date();
					}
					Calendar c = Calendar.getInstance();
					c.setTime(startDate);
					c.add(Calendar.DATE, nbDay);
					
					Element a = td.select("a").first();
					
							   event.setTitle(a.text());
							   event.setStartDate(startDate);
							   event.setEndDate(c.getTime());
							   event.setDuration(nbDay);
							 
							   if(a.attr("href").startsWith("/"))
								   event.setUrl(new URL("https://magic.wizards.com"+a.attr("href")));
							   else
								   event.setUrl(new URL(a.attr("href")));
							
							   try{
								   event.setColor(Color.decode("#"+a.attr("data-color")));
							   }
							   catch(NumberFormatException ex)
							   {
								   event.setColor(Color.WHITE);
							   }
							   list.add(event);
				}
			}
		
		return list;
	}
}
