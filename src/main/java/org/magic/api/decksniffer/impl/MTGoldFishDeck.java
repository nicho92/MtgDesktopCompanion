package org.magic.api.decksniffer.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumCardsPatterns;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.beans.technical.RetrievableDeck;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.MTGConstants;
import org.magic.services.network.URLTools;


public class MTGoldFishDeck extends AbstractDeckSniffer {

	private static final String ARENA_STANDARD = "arena_standard";
	private static final String BRAWL = "brawl";
	private static final String COMMANDER = "commander";
	private static final String VINTAGE = "vintage";
	private static final String LEGACY = "legacy";
	private static final String PAUPER = "pauper";
	private static final String MODERN = "modern";
	private static final String STANDARD = "standard";
	private static final String SUPPORT = "SUPPORT";

	private boolean metagames = false;

	@Override
	public String[] listFilter() {
		if (metagames)
			return new String[] { STANDARD, MODERN, PAUPER, LEGACY, VINTAGE, COMMANDER, BRAWL,ARENA_STANDARD };
		else
			return new String[] { STANDARD, MODERN, PAUPER, LEGACY, VINTAGE, ARENA_STANDARD,"block", COMMANDER, "limited",
					 "canadian_highlander", "penny_dreadful", "tiny_Leaders", "free_Form","pioneer"};
	}


	@Override
	public MTGDeck getDeck(RetrievableDeck info) throws IOException {

		logger.debug("sniff url : {} ",info.getUrl());

		MTGDeck deck = info.toBaseDeck();
		Document d = URLTools.extractAsHtml(info.getUrl().toString());

		Elements trs = d.select("table.deck-view-deck-table").get(0).select(MTGConstants.HTML_TAG_TR);
		var sideboard = false;
		for (Element tr : trs)
		{
			if (tr.hasClass("deck-category-header") && tr.text().contains("Sideboard"))
			{
				sideboard = true;
			}
			else
			{
				Elements tds = tr.select("td");
				if(!tds.isEmpty())
				{
					var qty = Integer.parseInt(tds.get(0).text().trim());
					var name = tds.get(1).select("a").first().text();
					var p = Pattern.compile("\\["+EnumCardsPatterns.REGEX_ANY_STRING+"\\]");
					var m  = p.matcher(tds.get(1).select("a").first().attr("data-card-id"));
					MTGEdition ed  = null;

					if(m.find())
						ed = new MTGEdition(m.group(1));

					try {
						MTGCard mc = getEnabledPlugin(MTGCardsProvider.class).searchCardByName(name, ed, false).get(0);

						if(sideboard)
							deck.getSideBoard().put(mc, qty);
						else
							deck.getMain().put(mc, qty);

						notify(mc);
					}
					catch(Exception _)
					{
						logger.error("No card found for {} {}",name,ed);
					}
				}
			}
		}
		return deck;
	}


	@Override
	public List<RetrievableDeck> getDeckList(String filter, MTGCard mc) throws IOException {
		var url = "";
		metagames = getBoolean("METAGAME");

		List<RetrievableDeck> list = new ArrayList<>();
		var nbPage = 1;
		var maxPage = getInt("MAX_PAGE");

		if (metagames)
			maxPage = 1;

		var baseUrl="https://www.mtggoldfish.com/";
		for (var i = 1; i <= maxPage; i++) {
			
			if (!metagames)
				url = baseUrl + "/deck/custom/" + filter + "?page=" + nbPage + "#"+ getString(SUPPORT);
			else
				url = baseUrl + "metagame/" + filter + "#" + getString(SUPPORT);

			logger.debug("sniff url : {} ",url);

			var d = URLTools.extractAsHtml(url);
			logger.trace(d);

			Elements e = d.select("div.archetype-tile");

			for (Element cont : e) {

				var deck = new RetrievableDeck();
				try
				{
					var desc = cont.select("span.deck-price-" + getString(SUPPORT) + "> a");
					var colors = cont.select("span.manacost").attr("aria-label");
					var deckColor = new StringBuilder();

					if (colors.contains("white"))
						deckColor.append("{W}");

					if (colors.contains("blue"))
						deckColor.append("{U}");

					if (colors.contains("black"))
						deckColor.append("{B}");

					if (colors.contains("red"))
						deckColor.append("{R}");

					if (colors.contains("green"))
						deckColor.append("{G}");


					deck.setName(desc.get(0).text());
					deck.setUrl(new URI(baseUrl + desc.get(0).attr("href")));

					if (metagames)
						deck.setAuthor(getName());
					else
						deck.setAuthor(cont.select("div.deck-tile-author").text());

					deck.setColor(deckColor.toString());

					list.add(deck);

				} catch (URISyntaxException _) {
					logger.error("Error setting url for {}",deck.getName());
				}

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
	public Map<String, MTGProperty> getDefaultAttributes() {
		
		var m = super.getDefaultAttributes();
		
		m.put(SUPPORT,new MTGProperty("paper","get physical decks or MTGO","paper","online"));
		m.put("MAX_PAGE", MTGProperty.newIntegerProperty("2", "number of page to query", 1, 10));
		m.put("METAGAME", MTGProperty.newBooleanProperty("false", "load metagames deck (true) or user's deck (false)"));
		return m;
	}





	@Override
	public String getVersion() {
		return "4.0";
	}

}
