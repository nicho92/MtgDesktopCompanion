package org.magic.api.shopping.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.OrderEntry.TYPE_ITEM;
import org.magic.api.beans.OrderEntry.TYPE_TRANSACTION;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;
import org.magic.services.MTGControler;
import org.magic.tools.UITools;
import org.magic.tools.URLTools;
import org.magic.tools.URLToolsClient;

public class MagicVilleShopper extends AbstractMagicShopper {

	
	String urlBase= "https://www.magic-ville.com";

	String urlListOrders = urlBase + "/fr/register/my_shopping.php?type=S";
	String urlLogin = urlBase+"/fr/connexion.php";
	String urlDetailOrder=urlBase+"/fr/register/";
	
	
	public static void main(String[] args) throws IOException {
		new MagicVilleShopper().listOrders();
	}
	
	@Override
	public List<OrderEntry> listOrders() throws IOException {
		URLToolsClient client = URLTools.newClient();
		List<OrderEntry> entries = new ArrayList<>();
		
		List<NameValuePair> nvps = new ArrayList<>();
		nvps.add(new BasicNameValuePair("pseudo", getString("LOGIN")));
		nvps.add(new BasicNameValuePair("pass", getString("PASS")));
		nvps.add(new BasicNameValuePair("return_url", urlLogin));
		nvps.add(new BasicNameValuePair("data", "1"));
		nvps.add(new BasicNameValuePair("x", "14"));
		nvps.add(new BasicNameValuePair("y", "11"));
		
		client.doPost(urlLogin, nvps, null);
		
		Document listOrders = URLTools.toHtml(client.doGet(urlListOrders, null));
		Elements tableOrders = listOrders.select("table[border=0]").get(6).select("tr");
		try {
			tableOrders.remove(0); //remove header
			tableOrders.remove(0); //remove separator
			tableOrders.remove(tableOrders.size()-1); //remove separator
			tableOrders.remove(tableOrders.size()-1); // remove table foot
			logger.debug("Found " + tableOrders.size() + " orders");
		}
		catch(Exception e)
		{
			logger.debug("Found no orders");
			return entries;
		}
		
		for(Element tr : tableOrders)
		{
			String date = tr.select("td").get(0).html();
			String link = tr.select("td").get(2).select("a").attr("href");
			String id =tr.select("td").get(2).text().replaceAll("# ", "");
			entries.addAll(parse(URLTools.toHtml(client.doGet(urlDetailOrder+link)),id,UITools.parseDate(date,"dd/mm/yy")));
		}
			
		return entries;
	}

	
	
	
	private List<OrderEntry> parse(Document doc, String id, Date date) {
		List<OrderEntry> entries = new ArrayList<>();
		Elements table = doc.select("table tr[onmouseover]");
		
		logger.trace(table);
		
		for(int i=0;i<table.size();i++)
		{
			Element e = table.get(i);
		
			OrderEntry entrie = new OrderEntry();
						entrie.setIdTransation(id);
						entrie.setSource(getName());
						entrie.setCurrency(Currency.getInstance("EUR"));
						entrie.setTypeTransaction(TYPE_TRANSACTION.BUY);
						entrie.setTransationDate(date);
						entrie.setType(TYPE_ITEM.CARD);
						entrie.setDescription(e.select("td").get(1).text());
						entrie.setItemPrice(UITools.parseDouble(e.select("td").get(6).html().replaceAll("\u0080", "").trim()));
					notify(entrie);
					entries.add(entrie);	
		}
		
		
		
		return entries;
	}


	@Override
	public String getName() {
		return "Magic-Ville";
	}
	
	
	@Override
	public void initDefault() {
		setProperty("LOGIN", "");
		setProperty("PASS", "");
	}

}
