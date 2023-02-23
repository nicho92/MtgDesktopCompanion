package org.magic.api.tomigrate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.enums.TransactionDirection;
import org.magic.api.beans.technical.RetrievableTransaction;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;
import org.magic.services.AccountsManager;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.URLTools;
import org.magic.services.tools.UITools;

public class MagicCorporationShopper extends AbstractMagicShopper {

	private String urlLogin ="https://boutique.magiccorporation.com/moncompte.php?op=login_db";
	private String urlCommandes="https://boutique.magiccorporation.com/moncompte.php?op=suivi_commande";
	private String urlDetailCommandes="https://boutique.magiccorporation.com/moncompte.php?op=commande&num_commande=";
	
	@Override
	public List<RetrievableTransaction> listOrders() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
/*
	@Override
	public List<OrderEntry> listOrders() throws IOException {

		MTGHttpClient client = URLTools.newClient();

		List<OrderEntry> entries = new ArrayList<>();

		Map<String, String> nvps = client.buildMap()
									 .put("email", getAuthenticator().getLogin())
									 .put("pass", getAuthenticator().getPassword()).build();


		client.doPost(urlLogin, nvps, null);

		Document doc = URLTools.toHtml(client.toString(client.doGet(urlCommandes)));
		Elements numCommands = doc.select("table tbody tr");

		logger.debug("found {} orders. Parsing details",numCommands);
		for(var i=0;i<numCommands.size();i++)
		{
			String id=numCommands.get(i).select("td").get(0).text();
			String date=numCommands.get(i).select("td").get(1).text();
			try {
				logger.trace("parsing {}/{}",i,numCommands.size());
				entries.addAll(parse(URLTools.toHtml(client.toString(client.doGet(urlDetailCommandes+id))),id,date));

			}
			catch(Exception e)
			{
				logger.error("can't get order {}",id,e);
			}
		}

		return entries;

	}

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
