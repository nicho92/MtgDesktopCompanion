package org.magic.api.decksniffer.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.technical.RetrievableDeck;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.URLTools;

import com.google.common.collect.ImmutableMap.Builder;

public class MTGTop8DeckSniffer extends AbstractDeckSniffer {

	private static final String COMPETITION_FILTER = "COMPETITION_FILTER";
	Map<String, String> formats;

	public MTGTop8DeckSniffer() {
		super();
		initFormats();
	}

	private void initFormats() {
		formats = new HashMap<>();
		formats.put("Standard", "ST");
		formats.put("Modern", "MO");
		formats.put("Legacy", "LE");
		formats.put("Vintage", "VI");
		formats.put("Duel Commander", "EDH");
		formats.put("MTGO Commander", "EDHM");
		formats.put("Block", "BL");
		formats.put("Extended", "EX");
		formats.put("Pauper", "PAU");
		formats.put("Highlander", "HIGH");
		formats.put("Canadian Highlander", "CHL");
		formats.put("Limited", "LI");

	}

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

	@Override
	public String[] listFilter() {
		return formats.keySet().toArray(new String[formats.keySet().size()]);
	}

	@Override
	public MTGDeck getDeck(RetrievableDeck info) throws IOException {
		Document root = URLTools.extractAsHtml(info.getUrl().toString());
		MTGDeck d = info.toBaseDeck();

		Elements blocks = root.select("div[style='margin:3px;flex:1;']");

		var side = false;
		for (Element b : blocks)
		{

			for (Element line : b.children()) {

				if (line.hasClass("O14"))
				{
					if(line.text().equalsIgnoreCase("SIDEBOARD"))
						side = true;

				}
				else if(line.hasClass("deck_line"))
				{
					var qte = Integer.parseInt(line.text().substring(0, line.text().indexOf(' ')));
						String name = line.select("span.L14").text();

						if (!name.equals(""))
						{
							try {

							MTGCard mc = getEnabledPlugin(MTGCardsProvider.class).searchCardByName( name, null, true).get(0);
							if (!side)
								d.getMain().put(mc, qte);
							else
								d.getSideBoard().put(mc, qte);

							notify(mc);

							}
							catch(IndexOutOfBoundsException err)
							{
								logger.error("Error getting {}",name,err);
							}
						}
					}

			}

		}

		return d;
	}

	@Override
	public List<RetrievableDeck> getDeckList(String filter) throws IOException {
		MTGHttpClient httpClient = URLTools.newClient();
		var res = new StringBuilder();
		for (var i = 0; i < getInt("MAX_PAGE"); i++) {

			Builder<String,String> nvps = httpClient.buildMap();

			nvps.put("current_page", String.valueOf(i + 1));
			nvps.put("event_titre", getString("EVENT_FILTER"));
			nvps.put("deck_titre", "");
			nvps.put("player", "");
			nvps.put("format", formats.get(filter));
			nvps.put("MD_check", "1");
			nvps.put("cards", getString("CARD_FILTER"));
			nvps.put("date_start", getString("DATE_START_FILTER"));
			nvps.put("date_end", "");

			if (getString(COMPETITION_FILTER) != null) {
				for (String c : getArray(COMPETITION_FILTER))
					nvps.put(" compet_check[" + c.toUpperCase() + "]", "1");
			}

			logger.debug("snif decks : {}",getString("URL") + "/search");

			res.append(httpClient.doPost(getString("URL") + "/search", nvps.build(), null));
		}

		Document d = URLTools.toHtml(res.toString());
		Elements els = d.select("tr.hover_tr");

		List<RetrievableDeck> ret = new ArrayList<>();
		for (Element e : els) {
			var dk = new RetrievableDeck();
			dk.setName(e.select("td.s11 a").text());
			try {
				dk.setUrl(new URI(getString("URL") + e.select("td.s11 a").attr("href")));
			} catch (URISyntaxException e1) {
				dk.setUrl(null);
			}
			dk.setAuthor(e.select("td.g11 a").text());
			dk.setDescription(e.select("td.S10 a").text());
			ret.add(dk);
		}

		return ret;
	}


	@Override
	public String getName() {
		return "MTGTop8";
	}


	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of("URL", "http://mtgtop8.com/",
		"EVENT_FILTER", "",
		"MAX_PAGE", "2",
		"TIMEOUT", "0",
		"CARD_FILTER", "",
		COMPETITION_FILTER, "P,M,C,R",
		"DATE_START_FILTER", "");

	}



}
