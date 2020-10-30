package org.magic.api.decksniffer.impl;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.beans.enums.CardsPatterns;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.MTGConstants;
import org.magic.tools.URLTools;


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
	private static final String FORMAT = "FORMAT";

	

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
	public MagicDeck getDeck(RetrievableDeck info) throws IOException {

		logger.debug("sniff url : " + info.getUrl());

		MagicDeck deck = new MagicDeck();
		deck.setName(info.getName());
		deck.setDescription(info.getUrl().toString());
		Document d = URLTools.extractHtml(info.getUrl().toString());
	
		Elements trs = d.select("table.deck-view-deck-table").get(0).select(MTGConstants.HTML_TAG_TR);
		boolean sideboard = false;
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
					Integer qty = Integer.parseInt(tds.get(0).text().trim());
					String name = tds.get(1).select("a").first().text();
					Pattern p = Pattern.compile("\\["+CardsPatterns.REGEX_ANY_STRING+"\\]");
					Matcher m  = p.matcher(tds.get(1).select("a").first().attr("data-card-id"));
					MagicEdition ed  = null;
					
					if(m.find())
						ed = new MagicEdition(m.group(1));
						
					try {
						MagicCard mc = getEnabledPlugin(MTGCardsProvider.class).searchCardByName(name, ed, false).get(0);
						
						if(sideboard)
							deck.getSideBoard().put(mc, qty);
						else
							deck.getMain().put(mc, qty);
						
						notify(mc);
					}
					catch(Exception e)
					{
						logger.error("No card found for " + name + " "+ ed);
					}
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
		int maxPage = getInt("MAX_PAGE");

		if (metagames)
			maxPage = 1;

		for (int i = 1; i <= maxPage; i++) {

			if (!metagames)
				url = getString("URL") + "/deck/custom/" + getString(FORMAT) + "?page=" + nbPage + "#"+ getString(SUPPORT);
			else
				url = getString("URL") + "metagame/" + getString(FORMAT) + "/full#" + getString(SUPPORT);

			logger.debug("sniff url : " + url);

			Document d = URLTools.extractHtml(url);
			logger.trace(d);
			
			Elements e = d.select("div.archetype-tile");

			for (Element cont : e) {
				
				RetrievableDeck deck = new RetrievableDeck();
				try 
				{
					Elements desc = cont.select("span.deck-price-" + getString(SUPPORT) + "> a");
					String colors = cont.select("span.manacost").attr("aria-label");
					StringBuilder deckColor = new StringBuilder();
						
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
					deck.setUrl(new URI(getString("URL") + desc.get(0).attr("href")));
					
					if (metagames)
						deck.setAuthor("MtgGoldFish");
					else
						deck.setAuthor(cont.select("div.deck-tile-author").text());
	
					deck.setColor(deckColor.toString());
	
					list.add(deck);
				
				} catch (URISyntaxException e1) {
					logger.error("Error setting url for " + deck.getName());
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
	public void initDefault() {
		setProperty(SUPPORT, "paper");
		setProperty(FORMAT, STANDARD);
		
		setProperty("URL", "https://www.mtggoldfish.com/");
		setProperty("MAX_PAGE", "2");
		setProperty("METAGAME", "false");

	}

	@Override
	public String getVersion() {
		return "4.0";
	}

}
