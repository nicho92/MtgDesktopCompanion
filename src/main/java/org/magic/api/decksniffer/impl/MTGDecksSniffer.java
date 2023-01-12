package org.magic.api.decksniffer.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.technical.RetrievableDeck;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.network.URLTools;
import org.magic.services.tools.InstallCert;

public class MTGDecksSniffer extends AbstractDeckSniffer {


	private static final String MAX_PAGE = "MAX_PAGE";
	private static final String URL = "URL";
	private static final String LOAD_CERTIFICATE = "LOAD_CERTIFICATE";

	public MTGDecksSniffer() {
		super();
			if(getBoolean(LOAD_CERTIFICATE))
			{
				try {
					InstallCert.installCert("mtgdecks.net");
					setProperty(LOAD_CERTIFICATE, "false");
				} catch (Exception e1) {
					logger.error(e1);
				}
			}
	}

	@Override
	public String[] listFilter() {
		return new String[] { "Standard", "Modern", "Legacy", "Vintage", "Commander", "Historic", "Pauper", "Pioneer",	"Highlander" };
	}

	@Override
	public MagicDeck getDeck(RetrievableDeck info) throws IOException {

		var deck = info.toBaseDeck();
		deck.setName(info.getName());
		deck.setDescription("from " + info.getUrl());

		logger.debug("get deck at {}",info.getUrl());

		var d = URLTools.extractAsHtml(info.getUrl().toString());

		for (Element e : d.select("table.subtitle a"))
			deck.getTags().add(e.text());

		Elements tables = d.select("div.wholeDeck table");
		var isSideboard = false;

		for (Element table : tables) {
			isSideboard = table.select("th").first().hasClass("Sideboard");

			for (Element tr : table.select("tr.cardItem")) {
				var td = tr.select("td.number").first();
				var qte = td.text().substring(0, td.text().indexOf(' '));
				var name = td.select("a").text();
				if (name.contains("/"))
					name = name.substring(0, name.indexOf('/')).trim();

				try {
				var mc = getEnabledPlugin(MTGCardsProvider.class).searchCardByName(name, null, true).get(0);

				notify(mc);

				if (!isSideboard)
					deck.getMain().put(mc, Integer.parseInt(qte));
				else
					deck.getSideBoard().put(mc, Integer.parseInt(qte));

				}
				catch(Exception e)
				{
					logger.error("No card found for {}",name);
				}

			}

		}

		return deck;
	}

	@Override
	public List<RetrievableDeck> getDeckList(String filter) throws IOException {
		var url = getString(URL) + "/" + filter + "/decklists/page:1";
		logger.debug("get List deck at {}",url);
		List<RetrievableDeck> list = new ArrayList<>();
		var nbPage = 1;
		var maxPage = getInt(MAX_PAGE);

		for (var i = 1; i <= maxPage; i++) {
			url = getString(URL) + "/" + filter + "/decklists/page:" + nbPage;
			Document d = URLTools.extractAsHtml(url);

			Elements trs = d.select("table.hidden-xs tr ");

			for (var j = 1; j < trs.size(); j++) {
				Element tr = trs.get(j);
				var deck = new RetrievableDeck();

				deck.setName(tr.select("td a").first().text());
				try {
					deck.setUrl(new URI(getString(URL) + '/' + tr.select("td a").first().attr("href")));
				} catch (URISyntaxException e) {
					deck.setUrl(null);
				}
				deck.setAuthor(tr.select("td").get(2).select("strong").text());

				String manas = tr.select("td").get(3).html();

				var build = new StringBuilder();

				if (manas.contains("ms-w"))
					build.append("{W}");
				if (manas.contains("ms-u"))
					build.append("{U}");
				if (manas.contains("ms-b"))
					build.append("{B}");
				if (manas.contains("ms-r"))
					build.append("{R}");
				if (manas.contains("ms-g"))
					build.append("{G}");

				deck.setColor(build.toString());
				list.add(deck);
			}

		}

		return list;
	}


	@Override
	public String getName() {
		return "MTGDecks";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of(URL, "https://mtgdecks.net",
							   MAX_PAGE, "2",
							   LOAD_CERTIFICATE, "true");
	}

	@Override
	public String getVersion() {
		return "0.1";
	}

}
