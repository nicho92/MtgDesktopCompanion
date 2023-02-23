package org.magic.api.shopping.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.beans.technical.RetrievableTransaction;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;
import org.magic.services.AccountsManager;
import org.magic.services.MTGControler;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.RequestBuilder.METHOD;
import org.magic.services.network.URLTools;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

public class MagicVilleShopper extends AbstractMagicShopper {


	String urlBase= "https://www.magic-ville.com";

	String urlListOrders = urlBase + "/fr/register/my_shopping.php?type=S";
	String urlLogin = urlBase+"/fr/connexion.php";
	String urlDetailOrder=urlBase+"/fr/register/";
	
	MTGHttpClient client;
	
	
	private void init()
	{
		if(client==null)
		{
			client = URLTools.newClient();
			
			try {
				RequestBuilder.build().method(METHOD.POST)
				.url(urlLogin)
				.setClient(client)
				.addContent("pseudo", getAuthenticator().getLogin())
				.addContent("pass", getAuthenticator().getPassword())
				.addContent("return_url", urlLogin)
				.addContent("data", "1")
				.addContent("x", "14")
				.addContent("y", "11").execute();
			} catch (IOException e) {
				logger.error(e);
			}


		}
	}

	public static void main(String[] args) throws IOException, SQLException {
		MTGControler.getInstance().init();
		
		var prov = new MagicVilleShopper();
		var res = prov.listOrders();
		prov.getTransaction(res.get(0));
		
	}
	
	@Override
	public Transaction getTransaction(RetrievableTransaction rt) throws IOException {
		
		var t = buildTransaction(rt);
		
	    var doc= RequestBuilder.build().setClient(client).url(rt.getUrl()).method(METHOD.GET).toHtml();
	    Elements table = doc.select("table tr[onmouseover]");
		for (Element e : table) {
			var item = buildCard(e);
			
			if(item!=null)
				t.getItems().add(item);
			
			
		}
		/*
		
		private List<OrderEntry> parse(Document doc, String id, Date date) {
			for (Element e : table) {
				var entrie = new OrderEntry();
							entrie.setIdTransation(id);
							entrie.setSource(getName());
							entrie.setCurrency(Currency.getInstance("EUR"));
							entrie.setTypeTransaction(TransactionDirection.BUY);
							entrie.setTransactionDate(date);
							entrie.setType(EnumItems.CARD);
							entrie.setDescription(e.select("td").get(1).text());
							entrie.setItemPrice(UITools.parseDouble(e.select("td").get(6).html().replaceAll("\u0080", "").trim()));
						notify(entrie);
						entries.add(entrie);
			}



			return entries;
		}
		 */
		return t;
	}

	private MTGStockItem buildCard(Element e) {
		try {
			
			var card = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(e.select("td").get(1).text(), null, false).get(0);
			var st = new MagicCardStock(card);
				 st.setPrice(UITools.parseDouble(e.select("td").get(6).html()));
				 //TODO add language , quality shipping cose
				 
			return st;
		}
		catch(IndexOutOfBoundsException ex)
		{
			logger.error("No card found for {}",e.select("td").get(1).text());
		} catch (IOException e1) {
			logger.error(e);
		}
		return null;
	}
	

	
	@Override
	public List<RetrievableTransaction> listOrders() throws IOException {
		init();
		var entries = new ArrayList<RetrievableTransaction>();

		Document listOrders = URLTools.toHtml(client.toString(client.doGet(urlListOrders)));
		Elements tableOrders = listOrders.select("table[border=0]").get(6).select("tr");
		
		try {
			tableOrders.remove(0); //remove header
			tableOrders.remove(0); //remove separator
			tableOrders.remove(tableOrders.size()-1); //remove separator
			tableOrders.remove(tableOrders.size()-1); // remove table foot
			logger.debug("Found {} orders",tableOrders.size());
		}
		catch(Exception e)
		{
			logger.debug("Found no orders");
			return entries;
		}
		
		for(Element tr : tableOrders)
		{
			var entry = new RetrievableTransaction();
				entry.setSource(getName());
				entry.setUrl(urlDetailOrder+"/"+ tr.select("td").get(2).select("a").attr("href"));
				entry.setSourceId(tr.select("td").get(2).text().replace("# ", ""));
				var price =new String(tr.select("td").get(10).html().getBytes("ISO-8859-1"),"UTF-8" );
				
				if(!price.contains("n/a"))
					entry.setTotalValue(UITools.parseDouble(price));
				
				entry.setDateTransaction(UITools.parseDate(tr.select("td").get(0).html(), "dd/MM/yyyy"));
			entries.add(entry);
		}
		
		return entries;
	}

	
	@Override
	public String getName() {
		return "Magic-Ville";
	}


	@Override
	public List<String> listAuthenticationAttributes() {
		return AccountsManager.generateLoginPasswordsKeys();
	}

	
}
