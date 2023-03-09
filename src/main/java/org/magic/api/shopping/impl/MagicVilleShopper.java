package org.magic.api.shopping.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.beans.technical.RetrievableTransaction;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;
import org.magic.services.AccountsManager;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.RequestBuilder.METHOD;
import org.magic.services.network.URLTools;
import org.magic.services.providers.PluginsAliasesProvider;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

public class MagicVilleShopper extends AbstractMagicShopper {


	String urlBase= "https://www.magic-ville.com";

	String urlListOrders = urlBase + "/fr/register/my_shopping.php?type=S";
	String urlLogin = urlBase+"/fr/connexion.php";
	String urlDetailOrder=urlBase+"/fr/register/";
	
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

		
	@Override
	public Transaction getTransaction(RetrievableTransaction rt) throws IOException {
		
		var t = buildTransaction(rt);
			t.setCurrency(Currency.getInstance("EUR"));
			
	    var doc= RequestBuilder.build().setClient(client).url(rt.getUrl()).method(METHOD.GET).toHtml();
	    Elements table = doc.select("table tr[onmouseover]");
	
	    try {
	        var shippingTable =  doc.select("table tr[height] td.b12");
		    shippingTable.remove(0);
		    t.setShippingPrice(UITools.parseDouble(shippingTable.text()));
	    }catch(Exception e)
	    {
	    	logger.error(e);
	    }
	    
		for (Element e : table) {
				t.getItems().add(buildCard(e));
		}
		return t;
	}

	private MTGStockItem buildCard(Element e) {
		
		var st = new MagicCardStock();
			 st.setPrice(UITools.parseDouble(e.select("td").get(4).html()));
			 st.setQte(Integer.parseInt(e.select("td").get(5).html()));
			 st.setComment(e.select("td").get(1).text());
			 st.setLanguage(e.select("td").get(2).text().contains(" VF")?"French":"English");
			 st.setCondition(PluginsAliasesProvider.inst().getReversedConditionFor(this, e.select("td").get(3).text(), EnumCondition.NEAR_MINT));
		try {
			var card = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(e.select("td").get(1).text(), null, false).get(0);
				 st.setProduct(card);
			return st;
		}
		catch(IndexOutOfBoundsException ex)
		{
			logger.error("No card found for {}",e.select("td").get(1).text());
		} catch (IOException e1) {
			logger.error(e);
		}
		return st;
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
				
				entry.setDateTransaction(UITools.parseDate(tr.select("td").get(0).html(), "dd/MM/yy"));
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
