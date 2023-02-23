package org.magic.api.shopping.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.beans.technical.RetrievableTransaction;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;
import org.magic.services.AccountsManager;
import org.magic.services.MTGControler;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.RequestBuilder.METHOD;
import org.magic.services.network.URLTools;
import org.magic.services.tools.UITools;

public class MagicCorporationShopper extends AbstractMagicShopper {

	private String urlLogin ="https://boutique.magiccorporation.com/moncompte.php?op=login_db";
	private String urlCommandes="https://boutique.magiccorporation.com/moncompte.php?op=suivi_commande";
	private String urlDetailCommandes="https://boutique.magiccorporation.com/moncompte.php?op=commande&num_commande=";
	
	public void init()
	{
		if(client==null)
		{
			try {
				client = URLTools.newClient();
				RequestBuilder.build()
					.method(METHOD.POST)
					.url(urlLogin)
					.setClient(client)
					.addContent("email", getAuthenticator().getLogin())
					.addContent("pass", getAuthenticator().getPassword())
					.execute();
				
				
				
			} catch (IOException e) {
				logger.error(e);
			}


		}
	}
	
	@Override
	public List<RetrievableTransaction> listOrders() throws IOException {
		init();
		
		var orders = new ArrayList<RetrievableTransaction>();
		
		Document doc = URLTools.toHtml(client.toString(client.doGet(urlCommandes)));
		Elements tables = doc.select("table tbody tr");
		
		
		for(Element e : tables)
		{
				var order = new RetrievableTransaction();
					order.setSourceId(e.select("td").get(0).text());
					order.setDateTransaction(UITools.parseDate(e.select("td").get(1).text(), "dd/MM/yy"));
					order.setSource(getName());
					order.setUrl(urlDetailCommandes+order.getSourceId());
					order.setTotalValue(UITools.parseDouble(e.select("td").get(2).text()));
			
			orders.add(order);
			
		}
		
		return orders;
	}
	

	public static void main(String[] args) throws IOException, SQLException {
		MTGControler.getInstance().init();
		
		var prov = new MagicCorporationShopper();
		var res = prov.listOrders();
		prov.getTransaction(res.get(0));
		
	}
	
	


	@Override
	public Transaction getTransaction(RetrievableTransaction rt) throws IOException {
		var t = buildTransaction(rt);
		
	    var d= RequestBuilder.build().setClient(client).url(rt.getUrl()).method(METHOD.GET).toHtml();
	    Elements table = d.select("table tbody tr");
		for (Element e : table) {
			
			logger.info(e);
			
		}
		
		return t;
	}
	
/*

	private List<OrderEntry> parse(Document d,String id,String date) {

		List<OrderEntry> entries = new ArrayList<>();
		Elements detail = d.select("table tbody tr");

		for (Element element : detail) {
			try {
				var e = new OrderEntry();
				e.setSource(getName());
				e.setCurrency(Currency.getInstance("EUR"));
				e.setIdTransation(id);
				e.setTypeTransaction(TransactionDirection.BUY);
				e.setTransactionDate(UITools.parseDate(date,"dd/MM/yy"));
				e.setDescription(element.select("td").get(0).text());
				e.setItemPrice(UITools.parseDouble(element.select("td").get(3).text().replace("€", "")));
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
*/


	@Override
	public String getName() {
		return "MagicCorporation";
	}

	@Override
	public List<String> listAuthenticationAttributes() {
		return AccountsManager.generateLoginPasswordsKeys();
	}


}
