package org.magic.api.shopping.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.magic.api.beans.MTGSealedProduct;
import org.magic.api.beans.MTGSealedStock;
import org.magic.api.beans.enums.EnumTransactionStatus;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.beans.technical.MoneyValue;
import org.magic.api.beans.technical.RetrievableTransaction;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;
import org.magic.services.AccountsManager;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.RequestBuilder.METHOD;
import org.magic.services.network.URLTools;
import org.magic.services.tools.UITools;

public class PhilibertShopper extends AbstractMagicShopper {

	private static final String BASE_URL="https://www.philibertnet.com/";
	private Document s;
	private static final String DATE_FORMAT = "MM/dd/yyyy";
	 
	 
	 
	@Override
	protected Currency getCurrency() {
		return Currency.getInstance("EUR");
	}
	
	@Override
	public List<RetrievableTransaction> listOrders() throws IOException {
			init();
			var  ret = new ArrayList<RetrievableTransaction>();
			for(Element tr : s.select("div.ant-accordion__content div.ant-order__right"))
			{
				var id = tr.select("div.ant-order__name span").get(0).text().replace("Order #", "");
				var date = tr.select("div.ant-order__name span").get(1).text().replace("from ", "");
				var price = tr.select("div.ant-order__articles span").text().trim();
			
				var rt = new RetrievableTransaction();
					 rt.setSourceId(id);
					 rt.setSource(getName());
					 rt.setUrl(BASE_URL+"/en/index.php?controller=order-detail&id_order="+id+"&ajax=true");
					 
					 rt.setDateTransaction(UITools.parseDate(date, DATE_FORMAT));
					 rt.setTotalValue(new MoneyValue(UITools.parseDouble(price), getCurrency()));
					 rt.setComments(tr.select("div.ant-order__articles span").text());
					 ret.add(rt);
			}
			
			return ret;
	}

	@Override
	public Transaction getTransaction(RetrievableTransaction rt) throws IOException {
		var t = buildTransaction(rt);
			 
		var orderPage = RequestBuilder.build().url(rt.getUrl())
				   .setClient(client)
				   .get()
				   .addHeader("x-requested-with","XMLHttpRequest")
				   .toHtml();
		
		
		var stepsTable = orderPage.select("table.detail_step_by_step tbody");
		
		for(var tr : stepsTable.select("tr"))
		{
			if(tr.select("TD").get(1).text().equals("Order cashed"))
			{
				t.setDatePayment(UITools.parseDate(tr.select("td").get(0).text(),DATE_FORMAT));
				t.setStatut(EnumTransactionStatus.PAID);
			}
			
			if(tr.select("TD").get(1).text().equals("Shipped"))
			{
				t.setDateSend(UITools.parseDate(tr.select("td").get(0).text(),DATE_FORMAT));
				t.setStatut(EnumTransactionStatus.SENT);
			}
			
			if(tr.select("TD").get(1).text().equals("Livré"))
			{
				t.setDateSend(UITools.parseDate(tr.select("td").get(0).text(),DATE_FORMAT));
				t.setStatut(EnumTransactionStatus.DELIVRED);
			}
		}
		
		try {
			var trackingTable = orderPage.select("table[data-sort=false]").get(1);
			t.setTransporterShippingCode(trackingTable.select("td a").first().text());
			t.setTransporter(trackingTable.select("td").get(1).text());
			t.setShippingPrice(UITools.parseDouble(trackingTable.select("td").get(2).attr("data-value")));
			
		}catch(Exception e)
		{
			//do nothing
		}
		
		
		
		for(Element tr : orderPage.select("div.detail-table-row"))
		{
		
			var stock = new MTGSealedStock(new MTGSealedProduct());
				 stock.setComment(tr.select("div.detail-table-row__name").text());
				 stock.setPrice(UITools.parseDouble(tr.select("div.detail-table-row__price div.detail-table-row__value").first().text()));
				 stock.setQte(Integer.parseInt(tr.select("div.detail-table-row__qty div.detail-table-row__value").text()));

				 
			if(stock.getPrice()>0)
				t.getItems().add(stock);
		}
		
		
		return t;
	}
	
	private void init()
	{
		
		if(client==null) {
			
			client = URLTools.newClient();
			try {
				s = RequestBuilder.build()
						  .post()
						  .url(BASE_URL+"/en/authentication")
						  .setClient(client)
						  .addContent("email", getAuthenticator().getLogin())
						  .addContent("passwd",getAuthenticator().getPassword())
						  .addContent("back","history")
						  .addContent("SubmitLogin","")
						  .addHeader(":path", "/en/authentication")
						  .addHeader(":authority", "www.philibertnet.com")
						  .addHeader(":method", METHOD.POST.name())
						  .addHeader(URLTools.CONTENT_TYPE, "application/x-www-form-urlencoded")
						  .toHtml();
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}
	
	
	@Override
	public List<String> listAuthenticationAttributes() {
		return AccountsManager.generateLoginPasswordsKeys();
	}


	@Override
	public String getName() {
		return "Philibert";
	}

}
