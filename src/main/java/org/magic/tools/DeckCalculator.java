package org.magic.tools;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.distribution.HypergeometricDistribution;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.services.MTGLogger;

public class DeckCalculator {

	private Logger logger = MTGLogger.getLogger(this.getClass());
	private MagicDeck deck;
	private DecimalFormat format;

	
	
	
	public DeckCalculator(MagicDeck d) {
		setDeck(d);
		format = new DecimalFormat("#0.0");
	}

	public List<MagicCard> getUniqueCards() {
		return new ArrayList<>(deck.getMap().keySet());
	}

	public MagicDeck getDeck() {
		return deck;
	}

	public void setDeck(MagicDeck d) {
		this.deck = d;
	}

	public void setFormat(String f) {
		format = new DecimalFormat(f);
	}

	public String format(Double d) {
		return format.format(d * 100) + "%";
	}

	public double getProbability(int turn, MagicCard mc) {
		
		if(mc==null)
			return  0;
		
		int drawedCards = 7;

		if (turn <= 0)
			drawedCards = 7;
		else
			drawedCards = drawedCards + turn;
		int numberInDeck = 0;
		try {
			numberInDeck = deck.getMap().get(mc);
		}catch(NullPointerException e)
		{
			logger.error(mc + " is not found in main deck");
		}
		int numberCardsInDeck = deck.getNbCards();
		try {
			return new HypergeometricDistribution(numberCardsInDeck, numberInDeck, drawedCards).upperCumulativeProbability(1);
		} catch (Exception e) {
			return 0;
		}

	}
}
