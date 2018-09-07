package org.magic.api.decksniffer.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.URLTools;

public class MTGoldFishDeck extends AbstractDeckSniffer {

	private static final String BRAWL = "brawl";
	private static final String COMMANDER = "commander";
	private static final String VINTAGE = "vintage";
	private static final String LEGACY = "legacy";
	private static final String PAUPER = "pauper";
	private static final String MODERN = "modern";
	private static final String STANDARD = "standard";
	private static final String SUPPORT = "SUPPORT";
	private static final String FORMAT = "FORMAT";

	

	private boolean metagames = false;

	@Override
	public String[] listFilter() {
		if (metagames)
			return new String[] { STANDARD, MODERN, PAUPER, LEGACY, VINTAGE, COMMANDER, BRAWL };
		else
			return new String[] { STANDARD, MODERN, PAUPER, LEGACY, VINTAGE, "arena_standard","block", COMMANDER, "limited",
					"frontier", "canadian_highlander", "penny_dreadful", "tiny_Leaders", "free_Form" };
	}

	@Override
	public MagicDeck getDeck(RetrievableDeck info) throws IOException {

		logger.debug("sniff url : " + info.getUrl());

		MagicDeck deck = new MagicDeck();
		deck.setName(info.getName());
		deck.setDescription(info.getUrl().toString());
		Document d = URLTools.extractHtml(info.getUrl().toString());
	
		Elements e = d.select("table.deck-view-deck-table").get(0).select(MTGConstants.HTML_TAG_TR);

		boolean sideboard = false;
		for (Element tr : e) {
			if (tr.select("td.deck-header").text().contains("Sideboard"))
				sideboard = true;

			if ((tr.select("td.deck-col-qty").text() + " " + tr.select("td.deck-col-card").text()).length() > 1) {

				int qte = Integer.parseInt(tr.select("td.deck-col-qty").text());
				String cardName = tr.select("td.deck-col-card").text();
				MagicEdition ed = null;
				if (MagicCard.isBasicLand(cardName)) {
					ed = new MagicEdition();
					ed.setId(MTGControler.getInstance().get("default-land-deck"));
				}

				if (cardName.contains("//"))
					cardName = cardName.substring(0, cardName.indexOf("//")).trim();

				MagicCard mc = MTGControler.getInstance().getEnabledCardsProviders()
						.searchCardByName( cardName, ed, true).get(0);
				if (!sideboard) {
					deck.getMap().put(mc, qte);
				} else {
					deck.getMapSideBoard().put(mc, qte);
				}
			}

		}
		return deck;
	}

	public List<RetrievableDeck> getDeckList() throws IOException {
		String url = "";
		metagames = getString("METAGAME").equals("true");

		List<RetrievableDeck> list = new ArrayList<>();
		int nbPage = 1;
		int maxPage = Integer.parseInt(getString("MAX_PAGE"));

		if (metagames)
			maxPage = 1;

		for (int i = 1; i <= maxPage; i++) {

			if (!metagames)
				url = getString("URL") + "/deck/custom/" + getString(FORMAT) + "?page=" + nbPage + "#"
						+ getString(SUPPORT);
			else
				url = getString("URL") + "metagame/" + getString(FORMAT) + "/full#" + getString(SUPPORT);

			logger.debug("sniff url : " + url);

			Document d = URLTools.extractHtml(url);
			logger.trace(d);
			
			Elements e = null;

			if (!metagames)
				e = d.select("div.deck-tile");
			else
				e = d.select("div.archetype-tile");

			for (Element cont : e) {

				Elements desc = cont.select("span.deck-price-" + getString(SUPPORT) + "> a");
				Elements colors = cont.select("span.manacost > img");
				StringBuilder deckColor = new StringBuilder();

				for (Element c : colors)
					deckColor.append("{").append(c.attr("alt").toUpperCase()).append("}");

				RetrievableDeck deck = new RetrievableDeck();
				deck.setName(desc.get(0).text());
				try {
					deck.setUrl(new URI(getString("URL") + desc.get(0).attr("href")));
				} catch (URISyntaxException e1) {
					deck.setUrl(null);
				}

				if (metagames)
					deck.setAuthor("MtgGoldFish");
				else
					deck.setAuthor(cont.select("div.deck-tile-author").text());

				deck.setColor(deckColor.toString());

				for (Element mc : cont.getElementsByTag("li")) {
					deck.getKeycards().add(mc.text());
				}

				list.add(deck);

			}
			nbPage++;
		}
		return list;
	}


	@Override
	public String getName() {
		return "MTGoldFish";
	}

	@Override
	public void initDefault() {
		setProperty(SUPPORT, "paper");
		setProperty(FORMAT, STANDARD);
		
		setProperty("URL", "https://www.mtggoldfish.com/");
		setProperty("MAX_PAGE", "2");
		setProperty("METAGAME", "false");

	}

	@Override
	public String getVersion() {
		return "3.0";
	}

}
