package org.magic.api.shopping.impl;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.TransactionDirection;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;
import org.magic.services.AccountsManager;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.URLTools;
import org.magic.tools.UITools;

public class MagicBazarShopper extends AbstractMagicShopper {


	String urlBase= "https://en.play-in.com/";

	String urlListOrders = urlBase + "/user/list_order.php";
	String urlLogin = urlBase+"/user/signin.php";

	@Override
	public List<OrderEntry> listOrders() throws IOException {
		MTGHttpClient client = URLTools.newClient();
		List<OrderEntry> entries = new ArrayList<>();

		Map<String, String> nvps = client.buildMap().put("_email", getAuthenticator().getLogin())
															 .put("_pwd", getAuthenticator().getPassword())
															 .put("_submit_login", "Me connecter").build();
		client.doPost(urlLogin, nvps, null);

		Document listOrders = URLTools.toHtml(client.toString(client.doGet(urlListOrders)));
		Elements e = listOrders.select("div.total_list a");

		logger.debug("Found {} orders",e.size());
		for (Element element : e) {
			String id = element.select("div.num_commande").text();
			String link = element.attr("href");
			String date = element.select("div.hide_mobile").first().html();
			entries.addAll(parse(URLTools.toHtml(client.toString(client.doGet(urlBase+link))),id,UITools.parseDate(date,"MM/dd/yy")));

		}
		return entries;
	}

	private List<OrderEntry> parse(Document doc, String id, Date date) {
		List<OrderEntry> entries = new ArrayList<>();
		Elements table = doc.select("div.table div.tr");
		table.remove(0);


		for (Element e : table) {
			boolean iscard=e.hasClass("filterElement");
			String name = e.select("div.td.name").text();


			if(!name.isEmpty())
			{

				var entrie = new OrderEntry();
					entrie.setIdTransation(id);
					entrie.setSource(getName());
					entrie.setCurrency(Currency.getInstance("EUR"));
					entrie.setSeller(getName());
					entrie.setTypeTransaction(TransactionDirection.BUY);
					entrie.setTransactionDate(date);
					entrie.setDescription(name);
					if(iscard)
					{
						entrie.setType(EnumItems.CARD);
						entrie.setDescription(e.select("div.td.name.name_mobile").text());
						entrie.setItemPrice(UITools.parseDouble(e.attr("attribute_price")));
						String set = e.select("div.td.ext img").attr("title");
						try {

							entrie.setEdition(getEnabledPlugin(MTGCardsProvider.class).getSetByName(set));
						}
						catch(Exception ex)
						{
							logger.error("{} is not found",set);
						}


					}
					else
					{
						String price =e.select("div.new_price").html().replaceAll("&nbsp;"+Currency.getInstance("EUR").getSymbol(), "").trim();
						entrie.setItemPrice(UITools.parseDouble(price));
						if(entrie.getDescription().contains("Set")||entrie.getDescription().toLowerCase().contains("collection"))
							entrie.setType(EnumItems.FULLSET);
						else if(entrie.getDescription().toLowerCase().contains("booster"))
							entrie.setType(EnumItems.BOOSTER);
						else if(entrie.getDescription().toLowerCase().startsWith("boite de") || entrie.getDescription().contains("Display") )
							entrie.setType(EnumItems.BOX);
						else
							entrie.setType(EnumItems.LOTS);
					}
					notify(entrie);
					entries.add(entrie);
			}



		}



		return entries;
	}


	@Override
	public String getName() {
		return "MagicBazar";
	}

	@Override
	public List<String> listAuthenticationAttributes() {
		return AccountsManager.generateLoginPasswordsKeys();
	}


}
