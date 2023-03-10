package org.magic.api.shopping.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MTGSealedProduct;
import org.magic.api.beans.SealedStock;
import org.magic.api.beans.enums.TransactionPayementProvider;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.beans.technical.RetrievableTransaction;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;
import org.magic.services.AccountsManager;
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
				var res = RequestBuilder.build()
					.method(METHOD.POST)
					.url(urlLogin)
					.setClient(client)
					.addContent("email", getAuthenticator().getLogin())
					.addContent("pass", getAuthenticator().getPassword())
					.toContentString();
				
				if(res.contains("Identifiant Incorrect !"))
				{
					logger.warn("Error Login/password");
					client=null;
					return;
				}
				
				
				
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
	
	@Override
	public Transaction getTransaction(RetrievableTransaction rt) throws IOException {
		var t = buildTransaction(rt);
			 
		
	    var d= RequestBuilder.build().setClient(client).url(rt.getUrl()).method(METHOD.GET).toHtml();
	    Elements table = d.select("table tbody tr");
	    
	    var header = d.select("div.main_bloc_article").get(2);
	    
	    fillInfo(t,header);
	    
	    
		for (Element tr : table) {
			
			//articles
			if(tr.childNodeSize()>2)
			{
				var name = tr.select("td").get(0).text();
				var price = UITools.parseDouble(tr.select("td").get(1).text());
				var qty = Integer.parseInt(tr.select("td").get(2).text());
				
				var st = new SealedStock();
					  st.setComment(name);
					  st.setQte(qty);	
					  st.setPrice(price);
					  st.setLanguage(name.contains("Français")?"French":"English");
					  st.setProduct(new MTGSealedProduct());
					  t.getItems().add(st);
					  
			}
			
			if(tr.childNodeSize()==2)
			{
				
			}
			
			
			
		}
		
		return t;
	}
	
	private void fillInfo(Transaction t, Element header) {

		var bs = header.select("b");
		
			switch(bs.get(2).text()) 
			{
				case "Carte Bancaire" : t.setPaymentProvider(TransactionPayementProvider.VISA);break;
				case "Paypal" : t.setPaymentProvider(TransactionPayementProvider.PAYPAL);break;
				default : break;
			}
			try {
				t.setDateCreation(UITools.parseDate(bs.get(3).text(), "EEEE dd MMMM yyyy 'à' hh:mm",Locale.FRANCE));	
			}
			catch(Exception e)
			{
				
			}
			
			try {
				t.setDatePayment(UITools.parseDate(bs.get(4).text(), "EEEE dd MMMM yyyy 'à' hh:mm",Locale.FRANCE));	
			}catch(Exception e)
			{
				t.setDatePayment(null);
			}
			
			try {
			t.setDateSend(UITools.parseDate(bs.get(7).text(), "dd/MM/yyyy 'à' hh:mm"));  //24/11/2015 à 14:48
			}
			catch(Exception e)
			{
				t.setDateSend(null);
			}
			
			try {
			t.setTransporterShippingCode(bs.get(8).text());
			}
			catch(Exception e)
			{
				t.setTransporterShippingCode(null);
			}
	}

	@Override
	public String getName() {
		return "MagicCorporation";
	}

	@Override
	public List<String> listAuthenticationAttributes() {
		return AccountsManager.generateLoginPasswordsKeys();
	}


}
