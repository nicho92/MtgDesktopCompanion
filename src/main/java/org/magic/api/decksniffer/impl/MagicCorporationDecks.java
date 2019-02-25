package org.magic.api.decksniffer.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.MTGConstants;
import org.magic.tools.URLTools;

public class MagicCorporationDecks extends AbstractDeckSniffer {

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public String[] listFilter() {
		return new String[] { "" };
	}

	@Override
	public MagicDeck getDeck(RetrievableDeck info) throws IOException {
		Document d = URLTools.extractHtml(getString("URL") + "/" + info.getUrl().toString());

		MagicDeck deck = new MagicDeck();
		deck.setName(info.getName());
		deck.setDescription(d.select("div.block_content").get(4).text().trim());
		Elements list = d.select("div.liste_deck>ul");

		for (Element ul : list) {
			for (Element li : ul.select("li")) {

				Integer qte = Integer.parseInt(li.text().substring(0, li.text().indexOf(' ')));
				String name = li.getElementsByTag("a").attr("title");
				//TODO waiting for foreignnames search
				logger.debug(qte + " " + name);
			}
		}

		return deck;
	}

	@Override
	public List<RetrievableDeck> getDeckList() throws IOException {
		Document d = URLTools.extractHtml(getString("URL") + "/mc.php?rub=decks&limit=0");

		Elements e = d.select("table.html_table > tbody");

		List<RetrievableDeck> list = new ArrayList<>();
		for (Element cont : e.get(0).getElementsByTag(MTGConstants.HTML_TAG_TR)) {
			String name = cont.getElementsByTag(MTGConstants.HTML_TAG_TD).get(1).text();
			String url = cont.getElementsByTag(MTGConstants.HTML_TAG_TD).get(1).getElementsByTag("a").attr("href");
			String auteur = cont.getElementsByTag(MTGConstants.HTML_TAG_TD).get(5).text();

			StringBuilder temp = new StringBuilder();

			for (Element color : cont.getElementsByTag(MTGConstants.HTML_TAG_TD).get(2).select("img")) {
				if (color.attr("src").contains("white"))
					temp.append("{W}");
				if (color.attr("src").contains("blue"))
					temp.append("{U}");
				if (color.attr("src").contains("black"))
					temp.append("{B}");
				if (color.attr("src").contains("red"))
					temp.append("{R}");
				if (color.attr("src").contains("green"))
					temp.append("{G}");
			}

			RetrievableDeck deck = new RetrievableDeck();

			deck.setName(name);
			try {
				deck.setUrl(new URI(url));
			} catch (URISyntaxException e1) {
				deck.setUrl(null);
			}
			deck.setAuthor(auteur);
			deck.setColor(temp.toString());
			list.add(deck);
		}

		return list;
	}



	@Override
	public String getName() {
		return "MagicCorporation";
	}

	@Override
	public void initDefault() {
		
		setProperty("URL", "http://www.magiccorporation.com/");

	}

	@Override
	public String getVersion() {
		return "0.1";
	}

}
