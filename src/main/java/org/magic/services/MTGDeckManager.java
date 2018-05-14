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
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.tools.DeckCalculator;
import org.utils.patterns.observer.Observable;

public class MTGDeckManager extends Observable {

	private static final String MULTI = "Multi";
	private MTGCardsExport serialis;
	private Logger logger = MTGLogger.getLogger(this.getClass());

	public MTGDeckManager(MTGCardsExport sniff) {
		serialis = sniff;
	}

	public MTGDeckManager() {
		serialis = new JsonExport();
	}

	public void setSerialiser(MTGCardsExport serialis) {
		this.serialis = serialis;
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
		for (File f : MTGConstants.MTG_DECK_DIRECTORY.listFiles((File dir, String name)->name.toLowerCase().endsWith(serialis.getFileExtension().toLowerCase()))) 
		{
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
	
	public List<MagicDeck> listDecksWithTag(String tag)
	{
		List<MagicDeck> decks = new ArrayList<>();
		for (MagicDeck deck : listDecks()) 
		{
				if(deck.getTags().contains(tag))
				{
					decks.add(deck);
					setChanged();
					notifyObservers(deck);
				}
		}
		return decks;
	}
	
	public List<MagicDeck> listDecksWith(MagicCard mc)
	{
		List<MagicDeck> decks = new ArrayList<>();
		for (MagicDeck deck : listDecks()) 
		{
				if(deck.hasCard(mc))
				{
					decks.add(deck);
					setChanged();
					notifyObservers(deck);
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
		File f = new File(MTGConstants.MTG_DECK_DIRECTORY, selectedDeck.getName() + serialis.getFileExtension());
		logger.debug("remove "+selectedDeck +":"+f.getAbsolutePath());
		try {
			FileUtils.forceDelete(f);
		} catch (IOException e) {
			logger.error("error removing " + f , e);
		}
	}

	public Map<String, Boolean> analyseLegalities(MagicDeck d) {
		TreeMap<String, Boolean> temp = new TreeMap<>();

		for (MTGFormat s : MTGFormat.values()) {
			temp.put(s.name(), isLegal(d, s.name()));
		}
		return temp;
	}

	public Map<Integer, Integer> analyseCMC(List<MagicCard> cards) {
		TreeMap<Integer, Integer> cmcs = new TreeMap<>();
		cards.forEach(card->{
			if ((card.getCmc() != null) && !card.getTypes().contains("Land"))
				cmcs.put(card.getCmc(), cmcs.get(card.getCmc())==null ? 1 : cmcs.get(card.getCmc())+1);	
		});
		
		return cmcs;
	}

	public Map<String, Integer> analyseTypes(List<MagicCard> cards) {
		TreeMap<String, Integer> types = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		cards.forEach(card->types.put(card.getTypes().get(0), types.get(card.getTypes().get(0))==null ? 1 : types.get(card.getTypes().get(0))+1));
		return types;
	}

	public Map<String,Integer> analyseColors(List<MagicCard> cards)
	{
		TreeMap<String, Integer> colors = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		cards.forEach(card->{
			
			if(!card.getColors().isEmpty()) {
				
				if (card.getColors().size() == 1){
					colors.put(card.getColors().get(0), colors.get(card.getColors().get(0))==null? 1 : colors.get(card.getColors().get(0))+1);
				}
				
				if (card.getColors().size() > 1) {
					colors.put(MULTI, colors.get(MULTI)==null? 1 : colors.get(MULTI)+1);
				}
			}
			else 
			{
				colors.put("Uncolor", colors.get("Uncolor")==null? 1 : colors.get("Uncolor")+1);
			}
		});
		return colors;
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
		TreeMap<String, Integer> rarity = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		cards.forEach(card->rarity.put(card.getCurrentSet().getRarity(), rarity.get(card.getCurrentSet().getRarity())==null? 1 : rarity.get(card.getCurrentSet().getRarity())+1));
		return rarity;

	}

}
