package org.magic.api.shopping.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGSealedStock;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.beans.technical.MoneyValue;
import org.magic.api.beans.technical.RetrievableTransaction;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGSealedProvider;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;
import org.magic.services.AccountsManager;
import org.magic.services.CardsManagerService;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

public class PlayInShopper extends AbstractMagicShopper {

	private static final String FRENCH = "French";

	String urlBase = "https://en.play-in.com";

	String urlListOrders = urlBase + "/user/list_order.php";
	String urlLogin = urlBase + "/user/signin.php";

	@Override
	public STATUT getStatut() {
		return STATUT.BUGGED;
	}

	private void init() {

		if (client == null) {

			client = URLTools.newClient();

			try {

				logger.info("connection with : {}", getAuthenticator().getLogin());
				final var res = RequestBuilder.build().setClient(client).post().url(urlLogin)
						.addContent("_email", getAuthenticator().getLogin())
						.addContent("_pwd", getAuthenticator().getPassword()).addContent("_submit_login", "Log in")
						.addContent("redirect", "/user/list_order.php").toHtml();

				logger.info("{} connection response : {}", getName(), res);
			} catch (final IOException e) {
				logger.error(e);
			}
		}
	}

	@Override
	public List<RetrievableTransaction> listOrders() throws IOException {

		init();

		final List<RetrievableTransaction> entries = new ArrayList<>();

		final Document listOrders = URLTools.toHtml(client.toString(client.doGet(urlListOrders)));
		final Elements e = listOrders.select("div.total_list a");

		for (final Element element : e) {

			final var o = new RetrievableTransaction();
			o.setSource(getName());
			o.setSourceId(element.select("div.num_commande").text());
			o.setDateTransaction(UITools.parseDate(element.select("div.hide_mobile").first().html(), "MM/dd/yy"));
			o.setTotalValue(new MoneyValue(
					UITools.parseDouble(StringEscapeUtils.unescapeHtml4(element.select("div.price").html())),
					getCurrency()));
			o.setComments(element.select("div.num_item").get(1).html());
			o.setUrl(element.attr("href"));
			entries.add(o);

		}
		return entries;
	}

	@Override
	public Transaction getTransaction(RetrievableTransaction rt) throws IOException {

		final var t = buildTransaction(rt);

		final var doc = RequestBuilder.build().setClient(client).url(urlBase + rt.getUrl()).get().toHtml();

		final Elements table = doc.select("div.table div.tr");
		table.remove(0);
		for (final Element e : table) {
			final boolean iscard = e.hasAttr("attribute_default");
			final var elementName = e.select("div.td.name");
			if (!elementName.isEmpty()) {
				if (iscard) {
					final var b = buildCard(e, elementName);
					if (b != null) {
					    t.getItems().add(b);
					}
				} else {
					final var b = buildSealed(e, elementName);
					if (b != null) {
					    t.getItems().add(b);
					}
				}
				notify(t);
			}
		}

		return t;
	}

	private MTGStockItem buildSealed(Element e, Elements elementName) {
		final String name = elementName.first().text();
		final var value = UITools.parseDouble(StringEscapeUtils.unescapeHtml4(e.select("div.price").html().trim()));
		final var qty = Integer.parseInt(StringEscapeUtils.unescapeHtml4(e.select("div.qty").html().trim()));
		final var productUrl = elementName.first().getElementsByTag("a").first().attr("href").replace("/produit/", "");

		try {

			final var ed = CardsManagerService.detectEdition(name);

			if (ed == null) {
				logger.warn("can't found ed product for {}", name);
				return null;
			} else {
				var typeProduct = EnumItems.SET;
				if (name.toLowerCase().contains("booster")) {
				    typeProduct = EnumItems.BOOSTER;
				}

				if (name.toLowerCase().startsWith("boite de") || name.contains("Display")) {
				    typeProduct = EnumItems.BOX;
				}

				if (name.toLowerCase().contains("Bundle")) {
				    typeProduct = EnumItems.BUNDLE;
				}

				final var products = MTG.getEnabledPlugin(MTGSealedProvider.class).get(ed, typeProduct);
				final var st = new MTGSealedStock(products.get(0));
				st.setComment(name);
				st.setPrice(value);
				st.setQte(qty);
				if (name.contains("VF") || name.contains(FRENCH) || name.contains("FR")) {
				    st.setLanguage(FRENCH);
				} else {
				    st.setLanguage("English");
				}

				st.getTiersAppIds().put(getName(), productUrl.substring(0, productUrl.indexOf('-')));

				return st;
			}

		} catch (final Exception ex) {
			logger.error(ex);
			return null;
		}
	}

	private MTGStockItem buildCard(Element e, Elements elementName) {
		final String name = URLTools.extractElementText(elementName.first());
		final String set = e.select("div.td.ext img").attr("title");
		final var productUrl = elementName.get(1).getElementsByTag("a").first().attr("href").replace("/magic/carte/", "");

		try {
			final var edition = MTG.getEnabledPlugin(MTGCardsProvider.class).getSetByName(set);
			final var card = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(name, edition, false).get(0);
			final var langEtat = URLTools.extractElementText(e.select("div.update_variation_sell").first()).split(" - ");

			final var st = new MTGCardStock(card);
			st.setPrice(UITools.parseDouble(e.attr("attribute_price")));
			st.setLanguage(langEtat[0].equalsIgnoreCase("Fr") ? FRENCH : "English");
			st.setCondition(aliases.getReversedConditionFor(this, langEtat[1], EnumCondition.NEAR_MINT));
			st.setQte(Integer.parseInt(URLTools.extractElementText(e.select("div.qty").first())));
			st.getTiersAppIds().put(getName(), productUrl);
			return st;
		} catch (final Exception ex) {
			logger.error(ex);
			return null;
		}
	}

	@Override
	public String getName() {
		return "PlayIn";
	}

	@Override
	public List<String> listAuthenticationAttributes() {
		return AccountsManager.generateLoginPasswordsKeys();
	}

	@Override
	protected Currency getCurrency() {
		return Currency.getInstance("EUR");
	}

}
