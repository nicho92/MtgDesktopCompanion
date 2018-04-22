package org.magic.services;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.magic.api.beans.MTGFormat;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicFormat;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.exports.impl.MTGDesktopCompanionExport;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.tools.DeckCalculator;
import org.utils.patterns.observer.Observable;

public class MTGDeckManager extends Observable {

	private MTGCardsExport serialis;
	private Logger logger = MTGLogger.getLogger(this.getClass());

	public MTGDeckManager(MTGCardsExport sniff) {
		serialis = sniff;
	}

	public MTGDeckManager() {
		serialis = new JsonExport();
	}

	public boolean isLegal(MagicDeck magicDeck, String format) {
		MagicFormat mf = new MagicFormat();
		mf.setFormat(format);
		return magicDeck.isCompatibleFormat(mf);

	}

	public MagicDeck getDeck(String name) throws IOException {
		File f = new File(MTGConstants.MTG_DECK_DIRECTORY, name + serialis.getFileExtension());
		return serialis.importDeck(f);
	}

	public List<MagicDeck> listDecks() {
		List<MagicDeck> decks = new ArrayList<>();
		for (File f : MTGConstants.MTG_DECK_DIRECTORY.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(serialis.getFileExtension().toLowerCase());
			}
		})) {
			try {
				MagicDeck deck = serialis.importDeck(f);
				decks.add(deck);
				setChanged();
				notifyObservers(deck);
			} catch (Exception e) {
				logger.error("error import deck " + f, e);
			}
		}
		return decks;
	}

	public void saveDeck(MagicDeck deck) throws IOException {
		if (!MTGConstants.MTG_DECK_DIRECTORY.exists())
			MTGConstants.MTG_DECK_DIRECTORY.mkdir();

		deck.setDateUpdate(new Date());
		serialis.export(deck, new File(MTGConstants.MTG_DECK_DIRECTORY, deck.getName() + serialis.getFileExtension()));

	}

	public void saveDeck(MagicDeck deck, MTGCardsExport exp) throws IOException {
		if (!MTGConstants.MTG_DECK_DIRECTORY.exists())
			MTGConstants.MTG_DECK_DIRECTORY.mkdir();

		exp.export(deck, new File(MTGConstants.MTG_DECK_DIRECTORY, deck.getName() + exp.getFileExtension()));

	}

	public void remove(MagicDeck selectedDeck) {
		FileUtils.deleteQuietly(new File(MTGConstants.MTG_DECK_DIRECTORY, selectedDeck.getName() + ".deck"));
	}

	public Map<String, Boolean> analyseLegalities(MagicDeck d) {
		TreeMap<String, Boolean> temp = new TreeMap<>();

		for (MTGFormat s : MTGFormat.values()) {
			temp.put(s.name(), isLegal(d, s.name()));
		}
		return temp;
	}

	public Map<Integer, Integer> analyseCMC(List<MagicCard> cards) {
		TreeMap<Integer, Integer> temp = new TreeMap<>();

		for (MagicCard mc : cards) {
			if ((mc.getCmc() != null) && !mc.getTypes().contains("Land"))
				temp.put(mc.getCmc(), countCmc(mc.getCmc(), cards));
		}
		return temp;
	}

	public Map<String, Integer> analyseTypes(List<MagicCard> cards) {
		TreeMap<String, Integer> temp = new TreeMap<>();

		for (MagicCard mc : cards) {
			if (!mc.getTypes().isEmpty())
				temp.put(mc.getTypes().get(0), countType(mc.getTypes().get(0), cards));
		}
		return temp;
	}

	public Map<String, Integer> analyseColors(List<MagicCard> cards) {
		TreeMap<String, Integer> temp = new TreeMap<>();

		for (MagicCard mc : cards) {
			if (!mc.getColors().isEmpty()) {
				if (mc.getColors().size() == 1)
					temp.put(mc.getColors().get(0), countColors(mc.getColors().get(0), cards));

				if (mc.getColors().size() > 1)
					temp.put("Multi", countColors("Multi", cards));

			} else {
				temp.put("Uncolor", countColors("Uncolor", cards));
			}
		}
		return temp;

	}

	public Map<MagicCard, List<Double>> analyseDrawing(MagicDeck d) {
		DeckCalculator calc = new DeckCalculator(d);

		Map<MagicCard, List<Double>> ret = new HashMap<>();

		for (MagicCard mc : calc.getUniqueCards()) {
			List<Double> list = new ArrayList<>();
			for (int i = 0; i < 10; i++) {
				list.add(calc.getProbability(i, mc));
			}

			ret.put(mc, list);
		}
		return ret;
	}

	public Map<String, Integer> analyseRarities(List<MagicCard> cards) {
		TreeMap<String, Integer> temp = new TreeMap<>();

		for (MagicCard mc : cards) {
			temp.put(mc.getEditions().get(0).getRarity(), countRarities(mc.getEditions().get(0).getRarity(), cards));
		}
		return temp;

	}

	private Integer countRarities(String rarity, List<MagicCard> cards) {
		int count = 0;
		for (MagicCard mc : cards) {
			try {
				if (mc.getEditions().get(0).getRarity().equals(rarity))
					count++;

			} catch (Exception e) {
				logger.error("error in count", e);
			}
		}
		return count;

	}

	private Integer countColors(String string, List<MagicCard> cards) {
		Integer count = 0;

		if (string.equals("Uncolor")) {
			for (MagicCard mc : cards)
				if (mc.getColors().isEmpty())
					count++;

			return count;
		} else if (string.equals("Multi")) {
			for (MagicCard mc : cards)
				if (mc.getColors().size() > 1)
					count++;

			return count;
		} else {
			for (MagicCard mc : cards)
				if (mc.getColors().size() == 1 && mc.getColors().get(0).equals(string))
					count++;

			return count;
		}

	}

	private Integer countCmc(Integer cmc, List<MagicCard> cards) {
		int count = 0;

		for (MagicCard mc : cards) {
			if (!mc.getTypes().contains("Land")) {
				int cm = (mc.getCmc() == null) ? 0 : mc.getCmc();
				if (cm == cmc)
					count++;
			}
		}
		return count;

	}

	private Integer countType(String type, List<MagicCard> cards) {
		Integer count = 0;
		for (MagicCard mc : cards)
			if (!mc.getTypes().isEmpty() && mc.getTypes().get(0).equals(type))
				count++;

		return count;

	}

}
