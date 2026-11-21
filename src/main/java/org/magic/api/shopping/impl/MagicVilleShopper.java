package org.magic.api.shopping.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.beans.technical.MoneyValue;
import org.magic.api.beans.technical.RetrievableTransaction;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;
import org.magic.services.AccountsManager;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

public class MagicVilleShopper extends AbstractMagicShopper {

	String urlBase = "https://www.magic-ville.com";

	String urlListOrders = urlBase + "/fr/register/my_shopping.php?type=S";
	String urlLogin = urlBase + "/fr/connexion.php";
	String urlDetailOrder = urlBase + "/fr/register/";

	@Override
	protected Currency getCurrency() {
		return Currency.getInstance("EUR");
	}

	private void init() {
		if (client == null) {
			client = URLTools.newClient();

			try {
				final var res = RequestBuilder.build().post().url(urlLogin).setClient(client)
						.addContent("pseudo", getAuthenticator().getLogin())
						.addContent("pass", getAuthenticator().getPassword()).addContent("return_url", urlLogin)
						.addContent("data", "1").addContent("x", "14").addContent("y", "11").execute();

				logger.debug("Connexion : {}", res.getStatusLine().getReasonPhrase());
			} catch (final IOException e) {
				logger.error(e);
			}

		}
	}

	@Override
	public Transaction getTransaction(RetrievableTransaction rt) throws IOException {

		final var t = buildTransaction(rt);

		final var doc = RequestBuilder.build().setClient(client).url(rt.getUrl()).get().toHtml();
		final Elements table = doc.select("table tr[onmouseover]");

		try {
			final var shippingTable = doc.select("table tr[height] td[align].b12");
			shippingTable.remove(0);
			t.setShippingPrice(UITools.parseDouble(shippingTable.text()));
		} catch (final Exception e) {
			logger.error(e);
		}

		for (final Element e : table) {
			t.getItems().add(buildCard(e));
		}
		return t;
	}

	private MTGStockItem buildCard(Element e) {
		final var st = new MTGCardStock();
		try {
			st.setPrice(UITools.parseDouble(e.select("td").get(4).html()));
		} catch (final NumberFormatException _) {
			logger.error("error parsing Price {}", e.select("td").get(4).html());
		}

		try {

			st.setQte(Integer.parseInt(e.select("td").get(5).html()));
		} catch (final NumberFormatException _) {
			logger.error("error parsing qty {}", e.select("td").get(5).html());
		}

		st.setComment(e.select("td").get(1).text());
		st.setLanguage(e.select("td").get(2).text().contains(" VF") ? "French" : "English");
		st.setCondition(aliases.getReversedConditionFor(this, e.select("td").get(3).text(), EnumCondition.NEAR_MINT));
		st.getTiersAppIds().put(getName(), e.select("td a").attr("href").replace("show_card_sale?gamerid=", "").trim());

		MTGEdition edition = null;

		try {
			final var setId = st.getTiersAppIds().get(getName()).substring(0, 3).toUpperCase();
			edition = MTG.getEnabledPlugin(MTGCardsProvider.class).getSetById(aliases.getSetIdFor(this, setId));
		} catch (final Exception _) {
			logger.error("No set found for {}", st.getTiersAppIds().get(getName()));
		}

		MTGCard card = null;
		try {
			final var name = RequestBuilder.build().setClient(client)
					.url(urlDetailOrder + "show_card_sale?gamerid=" + st.getTiersAppIds(getName())).get().toHtml()
					.select("td.S14 a").first().html().split("<br>")[0].trim();
			card = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(name, edition, true).get(0);
			st.setProduct(card);
		} catch (final Exception _) {
			logger.error("No card found for {} {}", st.getTiersAppIds().get(getName()).substring(3), edition);
			st.setProduct(new MTGCard());
		}
		return st;
	}

	@Override
	public List<RetrievableTransaction> listOrders() throws IOException {
		init();
		final var entries = new ArrayList<RetrievableTransaction>();

		final Document listOrders = URLTools.toHtml(client.toString(client.doGet(urlListOrders)));
		final Elements tableOrders = listOrders.select("table[border=0]").get(6).select("tr");

		try {
			tableOrders.remove(0); // remove header
			tableOrders.remove(0); // remove separator
			tableOrders.remove(tableOrders.size() - 1); // remove separator
			tableOrders.remove(tableOrders.size() - 1); // remove table foot
			logger.debug("Found {} orders", tableOrders.size());
		} catch (final Exception _) {
			logger.debug("Found no orders");
			return entries;
		}

		for (final Element tr : tableOrders) {
			final var entry = new RetrievableTransaction();
			entry.setSource(getName());
			entry.setUrl(urlDetailOrder + "/" + tr.select("td").get(2).select("a").attr("href"));
			entry.setSourceId(tr.select("td").get(2).text().replace("# ", ""));
			final var price = new String(tr.select("td").get(10).html().getBytes(StandardCharsets.ISO_8859_1),
					StandardCharsets.UTF_8);

			if (!price.contains("n/a")) {
			    entry.setTotalValue(new MoneyValue(UITools.parseDouble(price), getCurrency()));
			}

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
