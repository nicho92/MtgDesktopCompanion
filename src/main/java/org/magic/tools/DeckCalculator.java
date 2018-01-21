package org.magic.tools;

import java.text.DecimalFormat;

import org.apache.commons.math3.distribution.HypergeometricDistribution;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;

public class DeckCalculator {

	private MagicDeck deck;
	private HypergeometricDistribution hypergeoVar;
	private DecimalFormat format;
	
	public DeckCalculator(MagicDeck d) {
		setDeck(d);
		format =new DecimalFormat("#0.0");
	}
	
	public void setDeck(MagicDeck d)
	{
		this.deck=d;
	}
	
	public void setFormat(String f)
	{
		format = new DecimalFormat(f);
	}
	
	public String format(Double d)
	{
		return format.format(d*100)+"%";
	}
	
	public double getProbability(int turn,MagicCard mc)
	{
		int drawedCards=7;
		
		if(turn<=0)
			drawedCards=7;
		else
			drawedCards=drawedCards+turn;
		
		int numberInDeck=deck.getMap().get(mc);
		int numberCardsInDeck=deck.getNbCards();
		hypergeoVar= new HypergeometricDistribution(numberCardsInDeck, numberInDeck, drawedCards);
		return hypergeoVar.upperCumulativeProbability(1);
	}
}
