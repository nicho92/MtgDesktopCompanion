package org.magic.services;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
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

import com.google.gson.JsonParser;

public class MTGEventProvider {

	private String url =MTGConstants.WIZARD_EVENTS_URL;
	private Logger logger = MTGLogger.getLogger(this.getClass());
	
	
	public static void main(String[] args) {
		new MTGEventProvider().listEvents(new Date());
		
	}
	
	public List<MagicEvent> listEvents(Date date)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH)+1;
		return listEvents(year,month);
	}
	
	private String read(String url) throws ParseException, IOException
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
	
	
	public List<MagicEvent> listEvents(int y, int m)
	{
		String link = url+y+"-"+m;
		List<MagicEvent> list = new ArrayList<>();
		try {
			
			String json = read(link);
			String e = new JsonParser().parse(json).getAsJsonObject().get("data").getAsString();
			Elements trs = Jsoup.parse(e).select("tr.multi-day,tr.single-day");
			for(Element td : trs.select("td"))
			{
				if(!td.select("a").isEmpty())
				{
					int nbDay=Integer.parseInt(td.attr("colspan"));
					Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(td.attr("data-date"));
					Calendar c = Calendar.getInstance();
					c.setTime(startDate);
					c.add(Calendar.DATE, nbDay);
					
					Element a = td.select("a").first();
					MagicEvent event = new MagicEvent();
							   event.setTitle(a.text());
							   event.setStartDate(startDate);
							   event.setEndDate(c.getTime());
							   event.setDuration(nbDay);
							   event.setColor(Color.decode("#"+a.attr("data-color")));
							   if(a.attr("href").startsWith("/"))
								   event.setUrl(new URL("https://magic.wizards.com"+a.attr("href")));
							   else
								   event.setUrl(new URL(a.attr("href")));
							   
							  list.add(event);
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return list;
	}

	
	
}
