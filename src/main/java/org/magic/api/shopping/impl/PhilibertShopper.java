package org.magic.api.shopping.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.OrderEntry.TYPE_TRANSACTION;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;
import org.magic.tools.RequestBuilder;
import org.magic.tools.UITools;
import org.magic.tools.RequestBuilder.METHOD;
import org.magic.tools.URLTools;
import org.magic.tools.URLToolsClient;

public class PhilibertShopper extends AbstractMagicShopper {

	private static final String BASE_URL="https://www.philibertnet.com/";
	
	@Override
	public List<OrderEntry> listOrders() throws IOException {
		URLToolsClient c = URLTools.newClient();
		List<OrderEntry> ret = new ArrayList<>();
		
		
		Document s = RequestBuilder.build()
					  .method(METHOD.POST)
					  .url(BASE_URL+"/en/authentication")
					  .setClient(c)
					  .addContent("email", getString("LOGIN"))
					  .addContent("passwd",getString("PASSWORD"))
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
				Document orderPage = RequestBuilder.build().url(BASE_URL+"/en/index.php?controller=order-detail&id_order="+id+"&ajax=true")
														   .setClient(c)
														   .method(METHOD.GET)
														   .addHeader("x-requested-with","XMLHttpRequest")
														   .toHtml();
				
				String date = orderPage.select("table.table td.step-by-step-date").first().text();
				
				
				for(Element tr : orderPage.select("table").get(1).select("tbody>tr.item"))
				{
					
					int index = tr.selectFirst("td:has(input)")!=null?0:1; //check if first column is checkbox or not
			
					OrderEntry oe = new OrderEntry();
								oe.setSource(getName());
								oe.setIdTransation(id);
								oe.setCurrency(Currency.getInstance("EUR"));
								oe.setDescription(tr.select("td").get(2-index).text());
								oe.setItemPrice(UITools.parseDouble(tr.select("td").get(5-index).text().replaceAll("\\€", "")));
								oe.setTransactionDate(UITools.parseDate(date, "MM/dd/yyyy"));
								oe.setTypeTransaction(TYPE_TRANSACTION.BUY);
					if(oe.getItemPrice()>0)
						ret.add(oe);
					
				}
			
			}
			catch(Exception e)
			{
				logger.error("couldn't get order page for " + id + " " + e);
			}
		}
		
		return ret;
	}

	
	@Override
	public void initDefault() {
		setProperty("LOGIN", "");
		setProperty("PASSWORD", "");
	}
	
	
	
	@Override
	public String getName() {
		return "Philibert";
	}

}
