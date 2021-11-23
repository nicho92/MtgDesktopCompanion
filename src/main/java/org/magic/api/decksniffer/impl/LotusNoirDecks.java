package org.magic.api.decksniffer.impl;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
import org.magic.services.MTGConstants;
import org.magic.services.network.URLTools;

public class LotusNoirDecks extends AbstractDeckSniffer {

	
	private static final String FORMAT = "FORMAT";
	private static final String URL = "URL";
	private static final String MAX_PAGE = "MAX_PAGE";

	@Override
	public String[] listFilter() {
		return new String[] { "derniers-decks", "decks-du-moment", "decks-populaires" };
	}

	@Override
	public MagicDeck getDeck(RetrievableDeck info) throws IOException {
		MagicDeck deck = info.toBaseDeck();

		logger.debug("get deck at " + info.getUrl());

		var d = URLTools.extractAsHtml(info.getUrl().toString());
		var e = d.select("div.demi_page>table").select(MTGConstants.HTML_TAG_TR);
		var sideboard = false;
		for (Element cont : e) {
			var cont2 = cont.select("span.card_title_us");

			if (cont.text().startsWith("R\u00E9serve"))
				sideboard = true;

			if (cont2.text().length() > 0) {
				
				Integer qte = parseString(cont2.text()).getValue();
				String cardName = parseString(cont2.text()).getKey();

				if (cardName.contains("//")) // for transformatble cards
					cardName = cardName.substring(0, cardName.indexOf("//")).trim();
				
				try {
					MagicCard mc = getEnabledPlugin(MTGCardsProvider.class).searchCardByName(cardName, null, true).get(0);
	
					notify(mc);
					
					if (!sideboard)
						deck.getMain().put(mc, qte);
					else
						deck.getSideBoard().put(mc, qte);
				}catch(Exception ex)
				{
					logger.error("ERror loading card" + cont,ex);
				}
				
			}
		}
		return deck;
	}

	@Override
	public List<RetrievableDeck> getDeckList() throws IOException {

		String decksUrl = getString(URL) + "?dpage=" + getString(MAX_PAGE) + "&action=" + getString(FORMAT);

		logger.debug("snif decks : " + decksUrl);

		int nbPage = getInt(MAX_PAGE);
		List<RetrievableDeck> list = new ArrayList<>();

		for (var i = 1; i <= nbPage; i++) {
			Document d = URLTools.extractAsHtml(getString(URL) + "?dpage=" + i + "&action=" + getString(FORMAT));

			Elements e = d.select("div.thumb_page");

			for (Element cont : e) {
				var deck = new RetrievableDeck();
				var info = cont.select("a").get(0);
				var name = info.attr("title").replace("Lien vers ", "").trim();
				var url = info.attr("href");
				var auteur = cont.select("small").select("a").text();
				var value = URLTools.extractAsHtml(url).select("span.card_title_us");
				var deckColor = new StringBuilder();
				for (Element element : value)
				{
					String land = element.text().split(" ")[1];
					switch (land) 
					{
						case "Plain","Plains":
							deckColor.append("{W}");
							break;
						case "Island","Islands":
							deckColor.append("{U}");
							break;
						case "Swamp","Swamps":
							deckColor.append("{B}");
							break;
						case "Mountain","Mountains":
							deckColor.append("{R}");
							break;
						case "Forest","Forests":
							deckColor.append("{G}");
							break;
						default:
							break;
					} 
				}
				deck.setName(name);
				try {
					deck.setUrl(new URI(url));
				} catch (URISyntaxException e1) {
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
	public Map<String, String> getDefaultAttributes() {
		return Map.of(URL, "http://www.lotusnoir.info/magic/decks/",
			FORMAT, "decks-populaires",
			MAX_PAGE, "2");
	

	}


}
