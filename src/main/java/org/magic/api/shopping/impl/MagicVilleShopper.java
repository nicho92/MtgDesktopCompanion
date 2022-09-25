package org.magic.api.shopping.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.TransactionDirection;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;
import org.magic.services.AccountsManager;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.RequestBuilder.METHOD;
import org.magic.services.network.URLTools;
import org.magic.tools.UITools;

public class MagicVilleShopper extends AbstractMagicShopper {


	String urlBase= "https://www.magic-ville.com";

	String urlListOrders = urlBase + "/fr/register/my_shopping.php?type=S";
	String urlLogin = urlBase+"/fr/connexion.php";
	String urlDetailOrder=urlBase+"/fr/register/";

	@Override
	public List<OrderEntry> listOrders() throws IOException {
		MTGHttpClient client = URLTools.newClient();
		List<OrderEntry> entries = new ArrayList<>();

		RequestBuilder build = RequestBuilder.build().method(METHOD.POST)
													.url(urlLogin)
													.addContent("pseudo", getAuthenticator().getLogin())
													.addContent("pass", getAuthenticator().getPassword())
													.addContent("return_url", urlLogin)
													.addContent("data", "1")
													.addContent("x", "14")
													.addContent("y", "11");

		client.execute(build);

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
			String date = tr.select("td").get(0).html();
			String link = tr.select("td").get(2).select("a").attr("href");
			String id =tr.select("td").get(2).text().replace("# ", "");
			entries.addAll(parse(URLTools.toHtml(client.toString(client.doGet(urlDetailOrder+link))),id,UITools.parseDate(date,"dd/mm/yy")));
		}

		return entries;
	}




	private List<OrderEntry> parse(Document doc, String id, Date date) {
		List<OrderEntry> entries = new ArrayList<>();
		Elements table = doc.select("table tr[onmouseover]");

		logger.trace(table);

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


	@Override
	public String getName() {
		return "Magic-Ville";
	}


	@Override
	public List<String> listAuthenticationAttributes() {
		return AccountsManager.generateLoginPasswordsKeys();
	}


}
