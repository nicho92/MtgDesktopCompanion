package org.magic.api.shopping.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MTGSealedProduct;
import org.magic.api.beans.MTGSealedStock;
import org.magic.api.beans.enums.EnumPaymentProvider;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.beans.technical.MoneyValue;
import org.magic.api.beans.technical.RetrievableTransaction;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;
import org.magic.services.AccountsManager;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.tools.UITools;

public class MagicCorporationShopper extends AbstractMagicShopper {

	private final String urlLogin = "https://boutique.magiccorporation.com/moncompte.php?op=login_db";
	private final String urlCommandes = "https://boutique.magiccorporation.com/moncompte.php?op=suivi_commande";
	private final String urlDetailCommandes = "https://boutique.magiccorporation.com/moncompte.php?op=commande&num_commande=";

	public void init() {
		if (client == null) {
			try {
				client = URLTools.newClient();
				final var res = RequestBuilder.build().post().url(urlLogin).setClient(client)
						.addContent("email", getAuthenticator().getLogin())
						.addContent("pass", getAuthenticator().getPassword()).toContentString();

				if (res.contains("Identifiant Incorrect !")) {
					logger.warn("Error Login/password");
					client = null;
				}
			} catch (final IOException e) {
				logger.error(e);
			}

		}
	}

	@Override
	public List<RetrievableTransaction> listOrders() throws IOException {
		init();

		final var orders = new ArrayList<RetrievableTransaction>();

		final Document doc = URLTools.toHtml(client.toString(client.doGet(urlCommandes)));
		final Elements tables = doc.select("table tbody tr");

		for (final Element e : tables) {
			final var order = new RetrievableTransaction();
			order.setSourceId(e.select("td").get(0).text());
			order.setDateTransaction(UITools.parseDate(e.select("td").get(1).text(), "dd/MM/yy"));
			order.setSource(getName());
			order.setUrl(urlDetailCommandes + order.getSourceId());
			order.setTotalValue(new MoneyValue(UITools.parseDouble(e.select("td").get(2).text()), getCurrency()));

			orders.add(order);

		}

		return orders;
	}

	@Override
	protected Currency getCurrency() {
		return Currency.getInstance("EUR");
	}

	@Override
	public Transaction getTransaction(RetrievableTransaction rt) throws IOException {
		final var t = buildTransaction(rt);

		final var d = RequestBuilder.build().setClient(client).url(rt.getUrl()).get().toHtml();
		final Elements table = d.select("table tbody tr");

		final var header = d.select("div.main_bloc_article").get(2);

		fillInfo(t, header);

		for (final Element tr : table) {

			// articles
			if (tr.childNodeSize() > 2) {
				final var name = tr.select("td").get(0).text();
				final var price = UITools.parseDouble(tr.select("td").get(1).text());
				final var qty = Integer.parseInt(tr.select("td").get(2).text());

				final var st = new MTGSealedStock(new MTGSealedProduct());
				st.setComment(name);
				st.setQte(qty);
				st.setPrice(price);
				st.setLanguage(name.contains("Français") ? "French" : "English");
				st.getTiersAppIds().put(getName(), name.substring(name.indexOf("#") + 1, name.indexOf('-')).trim());

				t.getItems().add(st);

			}

			if (tr.childNodeSize() == 2) {
				// TODO fill the code
			}

		}

		return t;
	}

	private void fillInfo(Transaction t, Element header) {

		final var bs = header.select("b");

		switch (bs.get(2).text()) {
			case "Carte Bancaire" :
				t.setPaymentProvider(EnumPaymentProvider.VISA);
				break;
			case "Paypal" :
				t.setPaymentProvider(EnumPaymentProvider.PAYPAL);
				break;
			default :
				break;
		}
		try {
			t.setDateCreation(UITools.parseDate(bs.get(3).text(), "EEEE dd MMMM yyyy 'à' hh:mm", Locale.FRANCE));
		} catch (final Exception _) {
			// do nothing
		}

		try {
			t.setDatePayment(UITools.parseDate(bs.get(4).text(), "EEEE dd MMMM yyyy 'à' hh:mm", Locale.FRANCE));
		} catch (final Exception _) {
			t.setDatePayment(null);
		}

		try {
			t.setDateSend(UITools.parseDate(bs.get(7).text(), "dd/MM/yyyy 'à' hh:mm")); // 24/11/2015 à 14:48
		} catch (final Exception _) {
			t.setDateSend(null);
		}

		try {
			t.setTransporterShippingCode(bs.get(8).text());
		} catch (final Exception _) {
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
