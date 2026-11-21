package org.magic.api.decksniffer.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Element;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.beans.technical.RetrievableDeck;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.MTGConstants;
import org.magic.services.network.URLTools;

public class LotusNoirDecks extends AbstractDeckSniffer {

	private static final String MAX_PAGE = "MAX_PAGE";

	@Override
	public String[] listFilter() {
		return new String[]{"derniers-decks", "decks-du-moment", "decks-populaires"};
	}

	@Override
	public MTGDeck getDeck(RetrievableDeck info) throws IOException {
		final MTGDeck deck = info.toBaseDeck();

		logger.debug("get deck at {}", info.getUrl());

		final var d = URLTools.extractAsHtml(info.getUrl().toString());
		final var e = d.select("div.demi_page>table").select(MTGConstants.HTML_TAG_TR);
		var sideboard = false;
		for (final Element cont : e) {
			final var cont2 = cont.select("span.card_title_us");

			if (cont.text().startsWith("R\u00E9serve")) {
			    sideboard = true;
			}

			if (!cont2.text().isEmpty()) {

				final Integer qte = parseString(cont2.text()).getValue();
				String cardName = parseString(cont2.text()).getKey();

				if (cardName.contains("//")) { // for transformatble cards
					cardName = cardName.substring(0, cardName.indexOf("//")).trim();
				}

				try {
					final MTGCard mc = getEnabledPlugin(MTGCardsProvider.class).searchCardByName(cardName, null, true).get(0);

					notify(mc);

					if (!sideboard) {
					    deck.getMain().put(mc, qte);
					} else {
					    deck.getSideBoard().put(mc, qte);
					}
				} catch (final Exception ex) {
					logger.error("Error loading card {}", cont, ex);
				}

			}
		}
		return deck;
	}

	@Override
	public List<RetrievableDeck> getDeckList(String filter, MTGCard mc) throws IOException {

		final var baseUrl = "http://www.lotusnoir.info/magic/decks/";

		final String decksUrl = baseUrl + "?dpage=" + getString(MAX_PAGE) + "&action=" + filter;

		logger.debug("snif decks : {} ", decksUrl);

		final int nbPage = getInt(MAX_PAGE);
		final List<RetrievableDeck> list = new ArrayList<>();

		for (var i = 1; i <= nbPage; i++) {
			final var d = URLTools.extractAsHtml(baseUrl + "?dpage=" + i + "&action=" + filter);
			final var e = d.select("div.thumb_page");

			for (final Element cont : e) {
				final var deck = new RetrievableDeck();
				final var info = cont.select("a").get(0);
				final var name = info.attr("title").replace("Lien vers ", "").trim();
				final var url = info.attr("href");
				final var auteur = cont.select("small").select("a").text();
				final var value = URLTools.extractAsHtml(url).select("span.card_title_us");
				final var deckColor = new StringBuilder();
				for (final Element element : value) {
					final var land = element.text().split(" ")[1];
					switch (land) {
						case "Plain", "Plains" :
							deckColor.append("{W}");
							break;
						case "Island", "Islands" :
							deckColor.append("{U}");
							break;
						case "Swamp", "Swamps" :
							deckColor.append("{B}");
							break;
						case "Mountain", "Mountains" :
							deckColor.append("{R}");
							break;
						case "Forest", "Forests" :
							deckColor.append("{G}");
							break;
						default :
							break;
					}
				}
				deck.setName(name);
				try {
					deck.setUrl(new URI(url));
				} catch (final URISyntaxException _) {
					deck.setUrl(null);
				}
				deck.setAuthor(auteur);
				deck.setColor(deckColor.toString());

				list.add(deck);
			}
		}
		return list;
	}

	@Override
	public String getName() {
		return "LotusNoir";
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		final var m = super.getDefaultAttributes();
		m.put(MAX_PAGE, MTGProperty.newIntegerProperty("2", "number of page to query", 1, 10));
		return m;
	}

}
