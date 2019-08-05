package org.magic.api.shopping.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.OrderEntry.TYPE_TRANSACTION;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;
import org.magic.tools.UITools;
import org.magic.tools.URLTools;
import org.magic.tools.URLToolsClient;

public class MagicCorporationShopper extends AbstractMagicShopper {

	private String urlLogin ="https://boutique.magiccorporation.com/moncompte.php?op=login_db";
	private String urlCommandes="https://boutique.magiccorporation.com/moncompte.php?op=suivi_commande";
	private String urlDetailCommandes="https://boutique.magiccorporation.com/moncompte.php?op=commande&num_commande=";
	
	@Override
	public List<OrderEntry> listOrders() throws IOException {
		
		URLToolsClient client = URLTools.newClient();
		
		List<OrderEntry> entries = new ArrayList<>();
		
		Map<String, String> nvps = client.buildMap()
									 .put("email",  getString("LOGIN"))
									 .put("pass", getString("PASS")).build();
							
							
		client.doPost(urlLogin, nvps, null);
	
		Document doc = URLTools.toHtml(client.doGet(urlCommandes));
		Elements numCommands = doc.select("table tbody tr");
		
		logger.debug("found "+ numCommands.size()+ " orders. Parsing details");
		for(int i=0;i<numCommands.size();i++)
		{
			String id=numCommands.get(i).select("td").get(0).text();
			String date=numCommands.get(i).select("td").get(1).text();
			try {
				logger.trace("parsing " + i + "/"+numCommands.size());
				entries.addAll(parse(URLTools.toHtml(client.doGet(urlDetailCommandes+id)),id,date));
				
			}
			catch(Exception e)
			{
				logger.error("can't get order "+ id,e);
			}
			
			
			
		}
		
		
		return entries;
		
	}

	private List<OrderEntry> parse(Document d,String id,String date) {
		
		List<OrderEntry> entries = new ArrayList<>();
		Elements detail = d.select("table tbody tr");
		
		for(int i=0;i<detail.size();i++)
		{
			try {
				OrderEntry e = new OrderEntry();
				e.setSource(getName());
				e.setCurrency(Currency.getInstance("EUR"));
				e.setIdTransation(id);
				e.setTypeTransaction(TYPE_TRANSACTION.BUY);
				e.setTransactionDate(UITools.parseDate(date,"dd/MM/yy"));
				e.setDescription(detail.get(i).select("td").get(0).text());
				e.setItemPrice(UITools.parseDouble(detail.get(i).select("td").get(3).text().replaceAll("€", "")));
				entries.add(e);
				notify(e);
			}
			catch(IndexOutOfBoundsException e)
			{
				//do nothing
			}
		}
		return entries;
	}


	@Override
	public String getName() {
		return "MagicCorporation";
	}

	
	@Override
	public void initDefault() {
		setProperty("LOGIN","");
		setProperty("PASS", "");
	}
	

	@Override
	public boolean equals(Object obj) {
		
		if(obj ==null)
			return false;
		
		return hashCode()==obj.hashCode();
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode();
	}
}
