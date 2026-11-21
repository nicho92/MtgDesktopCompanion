package org.magic.services;

import static org.magic.services.tools.MTG.listEnabledPlugins;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.math3.distribution.HypergeometricDistribution;
import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.MTGFormat;
import org.magic.api.beans.MTGFormat.FORMATS;
import org.magic.api.beans.enums.EnumColors;
import org.magic.api.beans.enums.EnumRarity;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGDeckSniffer;
import org.magic.api.interfaces.MTGPlugin.STATUT;
import org.magic.services.logging.MTGLogger;
import org.magic.services.tools.MTG;
import org.utils.patterns.observer.Observable;

public class MTGDeckManager extends Observable {

	private final Logger logger = MTGLogger.getLogger(this.getClass());

	public static boolean isArenaDeck(MTGDeck d) {
		for (final MTGCard mc : d.getUniqueCards()) {
			if (!mc.isArenaCard()) {
			    return false;
			}
		}

		return true;
	}

	public static boolean isLegal(MTGDeck magicDeck, MTGFormat.FORMATS format) {

		if (format == FORMATS.COMMANDER) {
		    return isCommander(magicDeck);
		}

		if (magicDeck.getMainAsList().size() < 60) {
		    return false;
		}

		final var mf = new MTGFormat();
		mf.setFormat(MTGFormat.toString(format));
		return magicDeck.isCompatibleFormat(mf);

	}

	public static boolean isCommander(MTGDeck magicDeck) {

		if (magicDeck.getMainAsList().size() != 100) {
		    return false;
		}

		for (final Entry<MTGCard, Integer> entry : magicDeck.getMain().entrySet()) {
			if (!entry.getKey().isBasicLand() && entry.getValue() > 1) {
			    return false;
			}
		}

		return true;
	}

	public MTGDeck getDeck(Integer id) {
		try {
			return MTG.getEnabledPlugin(MTGDao.class).getDeckById(id);
		} catch (final Exception e) {
			logger.error("Error getting deck with id={}", id, e);
			return null;
		}
	}

	public List<MTGDeck> listDecks() {
		try {
			return MTG.getEnabledPlugin(MTGDao.class).listDecks();
		} catch (final SQLException e) {
			logger.error(e);
			return new ArrayList<>();
		}
	}

	public List<MTGDeck> listDecksWith(MTGCard mc, boolean strict) {
		final List<MTGDeck> decks = new ArrayList<>();
		for (final MTGDeck deck : listDecks()) {
			if (deck.hasCard(mc, strict)) {
				decks.add(deck);
				setChanged();
				notifyObservers(deck);
			}
		}
		return decks;
	}

	public void saveDeck(MTGDeck deck) throws IOException {
		try {
			MTG.getEnabledPlugin(MTGDao.class).saveOrUpdateDeck(deck);
		} catch (final SQLException e) {
			logger.error(e);
			throw new IOException(e);
		}

	}

	public void remove(MTGDeck selectedDeck) throws IOException {
		try {
			MTG.getEnabledPlugin(MTGDao.class).deleteDeck(selectedDeck);
		} catch (final SQLException e) {
			logger.error(e);
			throw new IOException(e);
		}
	}

	public Map<String, Boolean> analyseLegalities(MTGDeck d) {
		final var temp = new TreeMap<String, Boolean>();

		for (final MTGFormat.FORMATS s : MTGFormat.FORMATS.values()) {
			temp.put(s.name(), isLegal(d, s));
		}
		return temp;
	}

	public Map<Integer, Integer> analyseCMC(List<MTGCard> cards) {
		final var cmcs = new TreeMap<Integer, Integer>();
		cards.forEach(card -> {
			if ((card.getCmc() != null) && !card.isLand()) {
			    cmcs.put(card.getCmc(), cmcs.get(card.getCmc()) == null ? 1 : cmcs.get(card.getCmc()) + 1);
			}
		});

		return cmcs;
	}

	public Map<String, Integer> analyseTypes(List<MTGCard> cards) {
		final var types = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);
		cards.forEach(card -> types.compute(card.getTypes().get(0), (_, v) -> (v == null) ? 1 : v + 1));
		return types;
	}

	public Map<EnumColors, Integer> analyseColors(List<MTGCard> cards) {
		final var colors = new TreeMap<EnumColors, Integer>();

		if (cards == null) {
		    return colors;
		}

		cards.forEach(
				card -> colors.compute(EnumColors.determine(card.getColors()), (_, v) -> (v == null) ? 1 : v + 1));

		return colors;
	}

	public Map<MTGCard, List<Double>> analyseDrawing(MTGDeck d) {
		final var ret = new HashMap<MTGCard, List<Double>>();

		for (final var mc : d.getUniqueCards()) {
			final var list = new ArrayList<Double>();
			for (var i = 0; i < 10; i++) {
				list.add(getProbability(d, i, mc));
			}

			ret.put(mc, list);
		}
		return ret;
	}

	public Map<EnumRarity, Integer> analyseRarities(List<MTGCard> cards) {
		final var rarity = new TreeMap<EnumRarity, Integer>();
		cards.forEach(card -> rarity.compute(card.getRarity(), (_, v) -> (v == null) ? 1 : v + 1));

		return rarity;

	}

	public double getProbability(MTGDeck deck, int turn, MTGCard mc) {
		if ((deck == null) || (mc == null)) {
		    return 0;
		}

		var drawedCards = 7;

		if (turn <= 0) {
		    drawedCards = 7;
		} else {
		    drawedCards = drawedCards + turn;
		}

		final var cardCount = deck.getCardCountByName(mc.getName());
		final var deckSize = deck.getNbCards();
		try {
			return new HypergeometricDistribution(deckSize, cardCount, drawedCards).upperCumulativeProbability(1);
		} catch (final Exception _) {
			return 0;
		}

	}

	public MTGDeck generateRandomDeck() throws IOException {
		try {
			final var random = SecureRandom.getInstanceStrong();

			final var deckServices = listEnabledPlugins(MTGDeckSniffer.class).stream()
					.filter(p -> p.getStatut() == STATUT.STABLE).toList();
			final var sniffer = deckServices.get(random.nextInt(deckServices.size()));
			final var formats = sniffer.listFilter();
			final var f = formats[random.nextInt(formats.length)];
			final var availableDecks = sniffer.getDeckList(f, null);

			if (availableDecks.isEmpty()) {
			    throw new IOException("No deck found for " + sniffer + " " + f);
			}

			final var d = availableDecks.get(random.nextInt(availableDecks.size()));

			logger.info("Generating random deck from {} : {} ", sniffer, d.getName());

			return sniffer.getDeck(d);

		} catch (final NoSuchAlgorithmException e) {
			logger.error(e);
			return new MTGDeck();
		}
	}

}
