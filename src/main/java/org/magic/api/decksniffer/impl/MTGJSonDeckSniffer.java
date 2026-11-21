package org.magic.api.decksniffer.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.technical.RetrievableDeck;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.api.interfaces.abstracts.extra.AbstractMTGJsonProvider;
import org.magic.services.network.URLTools;

import com.google.gson.JsonArray;

public class MTGJSonDeckSniffer extends AbstractDeckSniffer {

	@Override
	public String[] listFilter() {
		return new String[]{"<no filter>"};
	}

	@Override
	public MTGDeck getDeck(RetrievableDeck info) throws IOException {
		final var el = URLTools.extractAsJson(info.getUrl().toString()).getAsJsonObject();

		final MTGDeck deck = info.toBaseDeck();

		try {
			deck.getTags().add(el.getAsJsonObject().get("data").getAsJsonObject().get("type").getAsString());
		} catch (final Exception _) {
			// do nothing
		}

		init(deck, el.get("data").getAsJsonObject().get("mainBoard").getAsJsonArray(), false, false);

		if (el.getAsJsonObject().get("sideBoard") != null) {
		    init(deck, el.getAsJsonObject().get("sideBoard").getAsJsonArray(), true, false);
		}

		if (el.get("data").getAsJsonObject().get("commander") != null) {
		    init(deck, el.get("data").getAsJsonObject().get("commander").getAsJsonArray(), false, true);
		}

		return deck;
	}

	private void init(MTGDeck d, JsonArray arr, boolean side, boolean commander) {
		arr.forEach(element -> {

			final var ed = element.getAsJsonObject().get("printings").getAsJsonArray().get(0).getAsString();
			final var qty = element.getAsJsonObject().get("count").getAsInt();
			final var scryfallId = element.getAsJsonObject().get("identifiers").getAsJsonObject().get("scryfallId")
					.getAsString();
			final var name = element.getAsJsonObject().get("name").getAsString();
			try {
				final var mc = getEnabledPlugin(MTGCardsProvider.class).getCardByScryfallId(scryfallId);
				if (!side) {
				    d.getMain().put(mc, qty);
				} else {
				    d.getSideBoard().put(mc, qty);
				}

				notify(mc);

				if (commander) {
				    d.setCommander(mc);
				}

			} catch (final Exception e) {
				logger.error("error loading {}/{}", name, ed, e);
			}

		});

	}

	@Override
	public List<RetrievableDeck> getDeckList(String filter, MTGCard mc) throws IOException {
		final var d = URLTools.extractAsJson(AbstractMTGJsonProvider.MTG_JSON_DECKS_LIST);
		final var arr = d.getAsJsonObject().get("data").getAsJsonArray();

		final List<RetrievableDeck> decks = new ArrayList<>();
		arr.forEach(element -> {

			final var ob = element.getAsJsonObject();

			final var rd = new RetrievableDeck();
			rd.setName(ob.get("name").getAsString());
			rd.setAuthor(ob.get("type").getAsString());
			try {
				rd.setDescription(
						getEnabledPlugin(MTGCardsProvider.class).getSetById(ob.get("code").getAsString()).getSet());
				rd.setUrl(URI
						.create(AbstractMTGJsonProvider.MTG_JSON_DECKS + ob.get("fileName").getAsString() + ".json"));
			} catch (final Exception e) {
				logger.error(e);
			}

			decks.add(rd);

		});

		return decks;

	}

	@Override
	public String getName() {
		return "MTGJson";
	}

}
