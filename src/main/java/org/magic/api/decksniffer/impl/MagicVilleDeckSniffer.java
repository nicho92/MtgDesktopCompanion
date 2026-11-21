package org.magic.api.decksniffer.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.beans.technical.RetrievableDeck;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;
import org.magic.services.tools.MTG;

public class MagicVilleDeckSniffer extends AbstractDeckSniffer {

	private static final String BASE_URL = "https://www.magic-ville.com/fr/decks/";

	private final MTGHttpClient client = URLTools.newClient();

	@Override
	public String[] listFilter() {
		return new String[] {
			"PIONEER",
			"PREMODERN",
			"MODERN",
			"STANDARD",
			"PEASANT",
			"ALCHEMY",
			"LEGACY",
			"VINTAGE",
			"DC"
		};
	}

	@Override
	public MTGDeck getDeck(RetrievableDeck info) throws IOException {
		final var doc = RequestBuilder.build().setClient(client).get().url(info.getUrl()).toHtml();
		final var urlimport = BASE_URL + doc.select("div.lil_menu > a[href^=dl_mws]").first().attr("href");
		var content = RequestBuilder.build().newClient().get().url(urlimport).toContentString();
		final var imp = MTG.getPlugin("MagicWorkStation", MTGCardsExport.class);
		try {
			imp.addObserver(listObservers().get(0));
		} catch (final IndexOutOfBoundsException _) {
			logger.warn("error adding current observer to {}", imp);
		}

		content = content.replace("<br />", "");
		final var d = imp.importDeck(content, info.getName());
		d.setCreationDate(new Date());
		d.setDateUpdate(new Date());
		d.setDescription(getName() + " at  " + info.getUrl());

		imp.removeObservers();

		return d;
	}

	@Override
	public List<RetrievableDeck> getDeckList(String filter, MTGCard mc) throws IOException {

		final var ret = new ArrayList<RetrievableDeck>();
		final int maxPage = getInt("MAX_PAGE");


		final var resp=RequestBuilder.build().post().setClient(client).url(BASE_URL + "resultats")
		.addContent("data","1")
		.addContent("dci", filter.toLowerCase())
		.addContent("tour_cur","1")
		.addContent("tour_orig","1")
		.addContent("MD_check", "1")
		.execute();

		logger.info("switch to {} : {}",filter, resp.getStatusLine());

		for (var currPage = 0; currPage < maxPage; currPage++) {
			final var d = RequestBuilder.build().get().setClient(client).url(BASE_URL + "resultats")
							.addContent("data","1")
							.addContent("dci", filter.toLowerCase())
							.addContent("tour_cur","1")
							.addContent("tour_orig","1")
							.addContent("MD_check", "1")
							.addContent("page_nb",""+(currPage+1))
							.toHtml();
			final var trs = d.select("tr[height=33]");
			for (final var tr : trs) {
				final var tds = tr.select("td");

				try {
					final var de = new RetrievableDeck();
					de.setName(tds.get(0).text());
					de.setUrl(new URI(BASE_URL + tds.get(0).select("a").attr("href") + "&decklanglocal=eng"));
					de.setAuthor(tds.get(1).text());
					final var temp = new StringBuilder();
					tds.get(3).select("img").forEach(e -> {
						var img = e.attr("src");
						img = img.substring(img.indexOf("png/") + 4, img.indexOf(".png"));

						if (img.length() > 1) {
						    img = img.substring(1);
						}

						if (img.equals("W") || img.equals("U") || img.equals("B") || img.equals("G") || img.equals("R")) {
						    temp.append("{").append(img).append("}");
						}
					});
					de.setColor(temp.toString());
					de.setDescription(tds.get(4).text());
					ret.add(de);
				} catch (final URISyntaxException _) {
					logger.error("error for url {}", BASE_URL + tds.get(0).select("a").attr("href"));
				}
			}
		}
		return ret;
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		final var m = super.getDefaultAttributes();
		m.put("MAX_PAGE", MTGProperty.newIntegerProperty("2", "number of page to query", 1, 10));
		return m;
	}

	@Override
	public String getName() {
		return "Magic-Ville";
	}

}
