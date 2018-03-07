package org.magic.api.shopping.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.magic.api.beans.ShopItem;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;
import org.magic.services.MTGConstants;
import org.magic.tools.InstallCert;

public class LeboncoinShopper extends AbstractMagicShopper  {

	Document doc;
	CloseableHttpClient httpclient;
	SimpleDateFormat formatter;

	
	@Override
	public STATUT getStatut() {
		return STATUT.STABLE;
	}
	
	
	public LeboncoinShopper() {
		super();	
		init();
	}
	
	
	private void init() {
		httpclient = HttpClients.createDefault();
		formatter=new SimpleDateFormat(getString("DATE_FORMAT"));
		
		try {
    			InstallCert.install(getString("CERT_SERV"));
    			System.setProperty("javax.net.ssl.trustStore",new File(MTGConstants.CONF_DIR,MTGConstants.KEYSTORE_NAME).getAbsolutePath());
		} catch (Exception e1) {
			
			logger.error(e1);
		}
	}
	
	
	
	@Override
	public List<ShopItem> search(String search) {
		
			
		List<ShopItem> list = new ArrayList<>();
		String html ="";
						int maxPage = Integer.parseInt(getString("MAX_PAGE"));
			
						for(int p=1;p<=maxPage;p++)
						{
							 html = getString("URL")
							 .replaceAll("%SEARCH%", search)
							 .replaceAll("%PAGE%", String.valueOf(p))
							 .replaceAll("%TITLE_ONLY%",getString("TITLE_ONLY"));
							
							 logger.debug("parsing item from " + html) ;
							
							 try {
								doc = Jsoup.connect(html).userAgent(getString("USER_AGENT")).get();
							} catch (IOException e1) {
								logger.error(e1);
							}
							 
							Elements listElements = doc.select(getString("ROOT_TAG")).get(0).getElementsByTag("li");
								 
							for(int i=0;i<listElements.size();i++)
								{
									String url =listElements.get(i).getElementsByTag("a").get(0).attr("href");
									ShopItem a = new ShopItem();
											a.setName(listElements.get(i).getElementsByTag("a").get(0).attr("title"));
											try {
												a.setUrl(new URL(getString("PROTOCOLE")+url));
											} catch (MalformedURLException e1) {
												a.setUrl(null);
											}
											a.setLieu(listElements.get(i).getElementsByClass("item_infos").get(0).getElementsByClass("item_supp").get(1).text());
											a.setType(listElements.get(i).getElementsByClass("item_infos").get(0).getElementsByClass("item_supp").get(0).text());
											a.setId(url.substring(url.lastIndexOf('/')+1, url.lastIndexOf('.')).trim());
											a.setShopName(getName());
											try{
												a.setImage(new URL(listElements.get(i).getElementsByClass("lazyload").get(0).attr("data-imgsrc")));
											}
											catch(IndexOutOfBoundsException e)
											{
													try {
														a.setImage(new URL(getString("PROTOCOLE")+"//static.leboncoin.fr/img/no-picture.png"));
													} catch (MalformedURLException e1) {
														logger.error(e1);
													}
											}
											catch(MalformedURLException e1)
											{
												logger.error(e1);
											}
											
											if(!listElements.get(i).getElementsByClass("item_price").isEmpty())
												a.setPrice(parsePrice(listElements.get(i).getElementsByClass("item_price").get(0).text()));
											
											try{
											a.setDate(parseDate(listElements.get(i).getElementsByClass("item_infos").get(0).getElementsByClass("item_supp").get(2).text()));
											}
											catch(Exception e)
											{
												logger.error(e);
											}
											a.setUrgent(listElements.get(i).getElementsByClass("item_infos").get(0).getElementsByClass("item_supp").get(2).text().startsWith("Urgent"));
											list.add(a);
											
								}
						}
				
		
		
		if(list.size()>Integer.parseInt(getString("MAX_RESULT")) && (Integer.parseInt(getString("MAX_RESULT"))>-1))
			 return list.subList(0, Integer.parseInt(getString("MAX_RESULT")));
		 
		return list;
	}
	

	private Date parseDate(String e)
	{
		String aujourdhui="Aujourd'hui, ";
		String hier="Hier, ";
		e=e.replaceAll("Urgent ", "");
		Calendar cal = GregorianCalendar.getInstance(Locale.FRANCE);
		
		if(e.contains(aujourdhui))
		{
			String hour = e.substring(aujourdhui.length(),aujourdhui.length()+2).trim();
			String minute = e.substring(aujourdhui.length()+3,aujourdhui.length()+5).trim();
			cal.setTime(new Date());
			cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
			cal.set(Calendar.MINUTE, Integer.parseInt(minute));
			return cal.getTime();
		}
		
		if(e.contains(hier))
		{
			if(e.startsWith(" "))
				e=e.substring(1, e.length());
			String hour = e.substring(hier.length(),hier.length()+2).trim();
			String minute = e.substring(hier.length()+3,hier.length()+5).trim();
			cal.setTime(new Date());
			cal.add(Calendar.DATE, -1);
			cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
			cal.set(Calendar.MINUTE, Integer.parseInt(minute));
			return cal.getTime();
		}
		
		try {
			cal.setTime(formatter.parse(e.replaceAll(",",".")));
			cal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
			
			return cal.getTime();
		} catch (ParseException e1) {
			logger.error(e1.getMessage());
		}
		return null;
	}
	
	private Double parsePrice(String price) {
		try{
			price = price.substring(0, price.length()-2).trim().replaceAll(" ", "");
			return Double.parseDouble(price);
		}catch(Exception e)
		{
			return 0.0;
		}
		
	}

	@Override
	public String getName() {
		return "LeBonCoin";
	}


	@Override
	public void initDefault() {
		setProperty("TITLE_ONLY", "0");
		setProperty("MAX_PAGE", "2");
		setProperty("MAX_RESULT", "30");
		setProperty("URL", "http://www.leboncoin.fr/li?o=%PAGE%&q=%SEARCH%&it=%TITLE_ONLY%");
		setProperty("USER_AGENT", MTGConstants.USER_AGENT);
		setProperty("PROTOCOLE", "http:");
		setProperty("WEBSITE", "http://www.leboncoin.fr/");
		setProperty("DATE_FORMAT", "dd MMMM. H:m");
		setProperty("ROOT_TAG", "section[class=tabsContent block-white dontSwitch]");
		setProperty("CERT_SERV", "www.leboncoin.fr");
		
	}


	@Override
	public String getVersion() {
		return "1.0";
	}

	
	
}
