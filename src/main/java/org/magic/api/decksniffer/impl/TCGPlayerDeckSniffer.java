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
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.beans.technical.RetrievableDeck;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.MTGConstants;
import org.magic.services.network.URLTools;

public class TCGPlayerDeckSniffer extends AbstractDeckSniffer {
	private static final String MAX_PAGE = "MAX_PAGE";
	private static final String SUBDECK_GROUP_CARD_QTY = "subdeck-group__card-qty";
		

	@Override
	public String[] listFilter() {
		return new String[] { "standard", "modern", "legacy", "vintage", "commander","pioneer","pauper","historic","brawl"};
	}

	@Override
	public MTGDeck getDeck(RetrievableDeck info) throws IOException {
		logger.debug("get deck at {}",info.getUrl());
		MTGDeck deck = info.toBaseDeck();
		Document d = URLTools.extractAsHtml(info.getUrl().toString());
		for (Element e : d.select("span.singleTag")) {
			deck.getTags().add(e.text());
		}

		var main = d.getElementsByClass("subdeck");

		int taille = main.get(0).getElementsByClass(SUBDECK_GROUP_CARD_QTY).size();
		for (var i = 0; i < taille; i++) {
			var qte = Integer.parseInt(main.get(0).getElementsByClass(SUBDECK_GROUP_CARD_QTY).get(i).text());
			String cardName = main.get(0).getElementsByClass("subdeck-group__card-name").get(i).text();


			if (cardName.contains("//"))
				cardName = cardName.substring(0, cardName.indexOf("//")).trim();

			MTGCard mc;
			try {
				mc = getEnabledPlugin(MTGCardsProvider.class).searchCardByName( cardName, null, true).get(0);
				deck.getMain().put(mc, qte);
				notify(mc);
			} catch (IndexOutOfBoundsException e1) {
				logger.error("{} is not found",cardName,e1);
			}


		}

		if (main.size() > 1) {
			int tailleSide = main.get(1).getElementsByClass(SUBDECK_GROUP_CARD_QTY).size();
			for (var i = 0; i < tailleSide; i++) {
				var qte = Integer.parseInt(main.get(1).getElementsByClass(SUBDECK_GROUP_CARD_QTY).get(i).text());
				String cardName = main.get(1).getElementsByClass("subdeck-group__card-name").get(i).text();


				if (cardName.contains("//"))
					cardName = cardName.substring(0, cardName.indexOf("//")).trim();
				try {
					MTGCard mc = getEnabledPlugin(MTGCardsProvider.class).searchCardByName( cardName, null, true).get(0);
					deck.getSideBoard().put(mc, qte);

				}
				 catch (IndexOutOfBoundsException e1) {
					 logger.error("{} is not found",cardName,e1);
				}

			}
		}

		return deck;

	}

	@Override
	public List<RetrievableDeck> getDeckList(String filter, MTGCard mc) throws IOException {
		
		var baseUrl="https://decks.tcgplayer.com";
		
		String url = baseUrl + "/magic/deck/search?format=" + filter;
		logger.debug("get List deck at {}",url);
		List<RetrievableDeck> list = new ArrayList<>();
		int maxPage = getInt(MAX_PAGE);

		for (var i = 1; i <= maxPage; i++) {
			url = baseUrl + "/magic/deck/search?format=" + filter + "&page=" + i;
			Document d = URLTools.extractAsHtml(url);

			for (Element tr : d.getElementsByClass("gradeA")) {
				var deck = new RetrievableDeck();

				var mana = "";

				Element manaEl = tr.getElementsByTag(MTGConstants.HTML_TAG_TD).get(0);
				if (manaEl.toString().contains("white-mana"))
					mana += "{W}";
				if (manaEl.toString().contains("blue-mana"))
					mana += "{U}";
				if (manaEl.toString().contains("black-mana"))
					mana += "{B}";
				if (manaEl.toString().contains("red-mana"))
					mana += "{R}";
				if (manaEl.toString().contains("green-mana"))
					mana += "{G}";

				String deckName = tr.getElementsByTag(MTGConstants.HTML_TAG_TD).get(1).text();
				String link = baseUrl + tr.getElementsByTag(MTGConstants.HTML_TAG_TD).get(1).getElementsByTag("a").attr("href");
				String deckPlayer = tr.getElementsByTag(MTGConstants.HTML_TAG_TD).get(2).text();
				String deckDesc = tr.getElementsByTag(MTGConstants.HTML_TAG_TD).get(3).text();

				deck.setColor(mana);
				deck.setAuthor(deckPlayer);
				deck.setName(deckName);
				deck.setDescription(deckDesc);

				try {
					deck.setUrl(new URI(link));
				} catch (URISyntaxException e) {
					deck.setUrl(null);
				}

				list.add(deck);

			}

		}

		return list;

	}


	@Override
	public String getName() {
		return "TCGPlayer";
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		
		var m = super.getDefaultAttributes();
		m.put(MAX_PAGE, MTGProperty.newIntegerProperty("1", "number of page to query", 1, 10));
		return m;
	}

}
