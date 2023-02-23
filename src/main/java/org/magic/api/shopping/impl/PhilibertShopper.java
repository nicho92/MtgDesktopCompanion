package org.magic.api.shopping.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.jsoup.nodes.Element;
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.enums.TransactionDirection;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.beans.technical.RetrievableTransaction;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;
import org.magic.services.AccountsManager;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.RequestBuilder.METHOD;
import org.magic.services.network.URLTools;
import org.magic.services.tools.UITools;

public class PhilibertShopper extends AbstractMagicShopper {

	private static final String BASE_URL="https://www.philibertnet.com/";

	
	@Override
	public List<RetrievableTransaction> listOrders() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	
	@Override
	public List<RetrievableTransaction> listOrders() throws IOException {
		MTGHttpClient c = URLTools.newClient();
		List<RetrievableTransaction> ret = new ArrayList<>();


		var s = RequestBuilder.build()
					  .method(METHOD.POST)
					  .url(BASE_URL+"/en/authentication")
					  .setClient(c)
					  .addContent("email", getAuthenticator().getLogin())
					  .addContent("passwd",getAuthenticator().getPassword())
					  .addContent("back","history")
					  .addContent("SubmitLogin","")
					  .addHeader(":path", "/en/authentication")
					  .addHeader(":authority", "www.philibertnet.com")
					  .addHeader(":method", METHOD.POST.name())
					  .addHeader(URLTools.CONTENT_TYPE, "application/x-www-form-urlencoded")
					  .toHtml();

		for(Element a : s.getElementById("order-list").select("tbody tr td a.color-myaccount"))
		{
			String id = a.text().trim();
			try {
				var orderPage = RequestBuilder.build().url(BASE_URL+"/en/index.php?controller=order-detail&id_order="+id+"&ajax=true")
														   .setClient(c)
														   .method(METHOD.GET)
														   .addHeader("x-requested-with","XMLHttpRequest")
														   .toHtml();

				String date = orderPage.select("table.table td.step-by-step-date").first().text();


				for(Element tr : orderPage.select("table").get(1).select("tbody>tr.item"))
				{

					int index = tr.selectFirst("td:has(input)")!=null?0:1; //check if first column is checkbox or not

					var oe = new OrderEntry();
								oe.setSource(getName());
								oe.setIdTransation(id);
								oe.setCurrency(Currency.getInstance("EUR"));
								oe.setDescription(tr.select("td").get(2-index).text());
								oe.setItemPrice(UITools.parseDouble(tr.select("td").get(5-index).text().replaceAll("\\€", "")));
								oe.setTransactionDate(UITools.parseDate(date, "MM/dd/yyyy"));
								oe.setTypeTransaction(TransactionDirection.BUY);
					if(oe.getItemPrice()>0)
						ret.add(oe);

				}

			}
			catch(Exception e)
			{
				logger.error("couldn't get order page for {} {}",id,e.getMessage());
			}
		}

		return ret;
	}
*/
	@Override
	public List<String> listAuthenticationAttributes() {
		return AccountsManager.generateLoginPasswordsKeys();
	}


	@Override
	public String getName() {
		return "Philibert";
	}

	@Override
	public Transaction getTransaction(RetrievableTransaction rt) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
