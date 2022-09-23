package org.magic.services;

import static org.magic.tools.MTG.listEnabledPlugins;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import org.apache.commons.math3.distribution.HypergeometricDistribution;
import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicFormat;
import org.magic.api.beans.MagicFormat.FORMATS;
import org.magic.api.beans.enums.MTGColor;
import org.magic.api.beans.enums.MTGRarity;
import org.magic.api.beans.technical.RetrievableDeck;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGDeckSniffer;
import org.magic.services.logging.MTGLogger;
import org.magic.tools.MTG;
import org.utils.patterns.observer.Observable;

public class MTGDeckManager extends Observable {

	private MTGCardsExport serialis;
	private Logger logger = MTGLogger.getLogger(this.getClass());



	public MTGDeckManager() {
		serialis = MTG.getPlugin("Json", MTGCardsExport.class);
	}


	public static boolean isArenaDeck(MagicDeck d)
	{
		for(MagicCard mc : d.getUniqueCards())
		{
			if(!mc.isArenaCard())
				return false;
		}

		return true;
	}

	public static boolean isLegal(MagicDeck magicDeck, MagicFormat.FORMATS format) {

		if(format==FORMATS.COMMANDER)
			return isCommander(magicDeck);

		if(magicDeck.getMainAsList().size()<60)
			return false;

		var mf = new MagicFormat();
		mf.setFormat(MagicFormat.toString(format));
		return magicDeck.isCompatibleFormat(mf);

	}

	public static boolean isCommander(MagicDeck magicDeck) {

		if(magicDeck.getMainAsList().size()!=100)
			return false;

		for(Entry<MagicCard, Integer> entry : magicDeck.getMain().entrySet())
		{
			if(!entry.getKey().isBasicLand() && entry.getValue()>1)
				return false;
		}


		return true;
	}


	public MagicDeck getDeck(Integer id)  {
		try {
			return MTG.getEnabledPlugin(MTGDao.class).getDeckById(id);
		} catch (SQLException e) {
			return null;
		}
	}



	public List<MagicDeck> listDecks() {
		try {
			return MTG.getEnabledPlugin(MTGDao.class).listDecks();
		} catch (SQLException e) {
			logger.error(e);
			return new ArrayList<>();
		}
	}

	public List<MagicDeck> listLocalDecks()
	{
		List<MagicDeck> decks = new ArrayList<>();
		for (File f : new File(MTGConstants.DATA_DIR, "decks").listFiles((File dir, String name)->name.toLowerCase().endsWith(serialis.getFileExtension().toLowerCase())))
		{
			try {
				var deck = serialis.importDeckFromFile(f);
				decks.add(deck);
			} catch (Exception e) {
				logger.error("error import deck {}",f, e);
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

	public List<MagicDeck> listDecksWith(MagicCard mc,boolean strict)
	{
		List<MagicDeck> decks = new ArrayList<>();
		for (MagicDeck deck : listDecks())
		{
				if(deck.hasCard(mc,strict))
				{
					decks.add(deck);
					setChanged();
					notifyObservers(deck);
				}
		}
		return decks;
	}

	public void saveDeck(MagicDeck deck) throws IOException {
		try {
			MTG.getEnabledPlugin(MTGDao.class).saveOrUpdateDeck(deck);
		} catch (SQLException e) {
			logger.error(e);
			throw new IOException(e);
		}

	}

	public void remove(MagicDeck selectedDeck) throws IOException {
		try {
			MTG.getEnabledPlugin(MTGDao.class).deleteDeck(selectedDeck);
		} catch (SQLException e) {
			logger.error(e);
			throw new IOException(e);
		}
	}

	public Map<String, Boolean> analyseLegalities(MagicDeck d) {
		TreeMap<String, Boolean> temp = new TreeMap<>();

		for (MagicFormat.FORMATS s : MagicFormat.FORMATS.values()) {
			temp.put(s.name(), isLegal(d, s));
		}
		return temp;
	}

	public Map<Integer, Integer> analyseCMC(List<MagicCard> cards) {
		TreeMap<Integer, Integer> cmcs = new TreeMap<>();
		cards.forEach(card->{
			if ((card.getCmc() != null) && !card.isLand())
				cmcs.put(card.getCmc(), cmcs.get(card.getCmc())==null ? 1 : cmcs.get(card.getCmc())+1);
		});

		return cmcs;
	}

	public Map<String, Integer> analyseTypes(List<MagicCard> cards) {
		TreeMap<String, Integer> types = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		cards.forEach(card->types.put(card.getTypes().get(0), types.get(card.getTypes().get(0))==null ? 1 : types.get(card.getTypes().get(0))+1));
		return types;
	}

	public Map<MTGColor,Integer> analyseColors(List<MagicCard> cards)
	{
		TreeMap<MTGColor, Integer> colors = new TreeMap<>();

		if(cards==null)
			return colors;

		cards.forEach(card->colors.compute(MTGColor.determine(card.getColors()), (k,v)->(v==null)?1:v+1));

		return colors;
	}

	public Map<MagicCard, List<Double>> analyseDrawing(MagicDeck d) {
		Map<MagicCard, List<Double>> ret = new HashMap<>();

		for (MagicCard mc : d.getUniqueCards()) {
			List<Double> list = new ArrayList<>();
			for (var i = 0; i < 10; i++) {
				list.add(getProbability(d,i, mc));
			}

			ret.put(mc, list);
		}
		return ret;
	}

	public Map<MTGRarity, Integer> analyseRarities(List<MagicCard> cards) {
		Map<MTGRarity, Integer> rarity = new TreeMap<>();
		cards.forEach(card->{

			if(card.getRarity()!=null)
				rarity.put(card.getRarity(), rarity.get(card.getRarity())==null? 1 : rarity.get(card.getRarity())+1);

		});
		return rarity;

	}

	public double getProbability(MagicDeck deck, int turn, MagicCard mc) {
		if((deck==null) || (mc==null))
			return  0;

		var drawedCards = 7;

		if (turn <= 0)
			drawedCards = 7;
		else
			drawedCards = drawedCards + turn;

		var cardCount = deck.getCardCountByName(mc.getName());
		var deckSize = deck.getNbCards();
		try {
			return new HypergeometricDistribution(deckSize, cardCount, drawedCards).upperCumulativeProbability(1);
		} catch (Exception e) {
			return 0;
		}

	}


	public MagicDeck generateRandomDeck() throws IOException
	{
		try {
			Random random= SecureRandom.getInstanceStrong();

			List<MTGDeckSniffer> deckServices = listEnabledPlugins(MTGDeckSniffer.class);
			MTGDeckSniffer sniffer = deckServices.get(random.nextInt(deckServices.size()));
			String[] formats = sniffer.listFilter();
			List<RetrievableDeck> availableDecks = sniffer.getDeckList(formats[random.nextInt(formats.length)]);
			RetrievableDeck d = availableDecks.get(random.nextInt(availableDecks.size()));
			return sniffer.getDeck(d);

		} catch (NoSuchAlgorithmException e) {
			logger.error(e);
			return new MagicDeck();
		}
	}




}
