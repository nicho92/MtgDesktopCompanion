package org.magic.api.decksniffer.impl;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.network.URLTools;

public class CardMarketDeckSniffer extends AbstractDeckSniffer {
	private static final String CARDLIST = ".cardlist";
	private static final String PAUPER = "Pauper";
	private static final String HIGHLANDER = "Highlander";
	private static final String URL = "URL";
	private static final String COMMANDER = "Commander";
	private static final String VINTAGE = "Vintage";
	private static final String LEGACY = "Legacy";
	private static final String MODERN = "Modern";
	private static final String STANDARD = "Standard";
	private static final String FORMAT = "FORMAT";

	@Override
	public String[] listFilter() {
		return new String[] { STANDARD, MODERN, LEGACY, VINTAGE, COMMANDER, HIGHLANDER, PAUPER };
	}

	@Override
	public MagicDeck getDeck(RetrievableDeck info) throws IOException {
		logger.debug("get deck at " + info.getUrl());
		MagicDeck deck = info.toBaseDeck();
		Document d = URLTools.extractHtml(info.getUrl().toString());

		Elements maincardsList = d.select(".mainboard.clearfix").select(CARDLIST);
		for (Element cardList : maincardsList.select("li")) {
			String qte = cardList.select("span").text();
			String cardName = cardList.select(".cardName.truncate.vAlignMiddle").text();
			if (cardName.contains("//")) // for transformatble cards
				cardName = cardName.substring(0, cardName.indexOf("//")).trim();
			try {
				MagicCard mc = getEnabledPlugin(MTGCardsProvider.class).searchCardByName(cardName, null, true).get(0);
				notify(mc);
				deck.getMain().put(mc, Integer.valueOf(qte));
			}
			catch(Exception e)
			{
				logger.error("Can't find " + cardName +" : " + e);
			}
		}

		Elements sidecardsList = d.select(".sideboard.clearfix").select(CARDLIST);
		for (Element cardList : sidecardsList.select("li")) {
			String qte = cardList.select("span").text();
			String cardName = cardList.select(".cardName.truncate.vAlignMiddle").text();
			if (cardName.contains("//")) // for transformatble cards
				cardName = cardName.substring(0, cardName.indexOf("//")).trim();
			try {
			MagicCard mc = getEnabledPlugin(MTGCardsProvider.class)
					.searchCardByName(cardName, null, true).get(0);

				notify(mc);
				deck.getSideBoard().put(mc, Integer.valueOf(qte));
			}
			catch(Exception e)
			{
				logger.error("Can't find " + cardName +" : " + e);
			}
		}
		return deck;
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEPRECATED;
	}

	@Override
	public List<RetrievableDeck> getDeckList() throws IOException {
		String url = getString(URL) + "/en/Magic/Decks/Events/Format/" + getString(FORMAT);
		logger.debug("get List deck at " + url);
		List<RetrievableDeck> list = new ArrayList<>();
		Document d = URLTools.extractHtml(url);
		for (Element topDecks : d.select(".vAlignMiddle.latestEvents-name")) {
			String eventName = topDecks.select("a").text();
			Elements topDeck = topDecks.select("select");
			for (Element option : topDeck.select("option").subList(1, topDeck.select("option").size())) {
				try {
					var deck = new RetrievableDeck();

					String[] text = option.html().split("&nbsp;");
					String deckName = text[1];
					if (text.length > 2) {
						String author = text[2].replaceAll("[()]", "");
						deck.setAuthor(author);

					}
					deck.setUrl(new URI(getString(URL) + "/en/Magic"+ option.getAllElements().select("option[value]").val().replace(" ", "%20")));

					
					//  Start Colors used in the deck this metod slows the deck list load
					
					if(getBoolean("DETAILED")) {
					
					  Document deckurl = URLTools.extractHtml(deck.getUrl().toString()); Elements
					  maincardslist = deckurl.select(".mainboard.clearfix").select(CARDLIST);
					  var deckColor = new StringBuilder();
					  if(maincardslist.select("li").text().contains("Plain") ||
							  maincardslist.select("li").text().contains("Plains")) {
					  deckColor.append("{W}"); }
					  if(maincardslist.select("li").text().contains("Island") ||
							  maincardslist.select("li").text().contains("Islands")) {
					  deckColor.append("{U}"); }
					  if(maincardslist.select("li").text().contains("Swamp") ||
							  maincardslist.select("li").text().contains("Swamps")) {
					  deckColor.append("{B}"); }
					  if(maincardslist.select("li").text().contains("Mountain") ||
							  maincardslist.select("li").text().contains("Mountains")) {
					  deckColor.append("{R}"); }
					  if(maincardslist.select("li").text().contains("Forest") ||
							  maincardslist.select("li").text().contains("Forests")) {
					  deckColor.append("{G}"); } deck.setColor(deckColor.toString());
					 
					  
					}
					
					deck.setDescription(eventName);
					deck.setName(deckName);
					  
					list.add(deck);
				} catch (Exception e) {
					logger.error(e);
				}

			}
		}
		return list;
	}

	@Override
	public String getName() {
		return "MagicCardMarket";
	}

	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of(FORMAT, STANDARD,
							   URL, "https://www.cardmarket.com",
							   "DETAILED", "false");
	}

	@Override
	public String termsAndCondition() {
		return "Thanks to sirrion1";
	}

}