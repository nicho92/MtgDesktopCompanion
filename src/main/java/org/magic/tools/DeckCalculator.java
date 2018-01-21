package org.magic.tools;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.distribution.HypergeometricDistribution;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.exports.impl.MTGDesktopCompanionExport;

public class DeckCalculator {

	MagicDeck deck;
	HypergeometricDistribution hypergeoVar;
	
	public DeckCalculator(MagicDeck d) {
		setDeck(d);
	}
	
	public void setDeck(MagicDeck d)
	{
		this.deck=d;
		
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
	
	public static void main(String[] args) throws Exception {
		MagicDeck deck = new MTGDesktopCompanionExport().importDeck(new File("C:\\Users\\Nicolas\\.magicDeskCompanion\\decks\\grixis death shadow.deck"));
		DeckCalculator calc = new DeckCalculator(deck);
		
		for(MagicCard mc : deck.getMap().keySet())
		{
			for(int i=0;i<10+1;i++)
			{
				System.out.print("T"+(i)+"="+new DecimalFormat("#0.0").format(calc.getProbability(i, mc)*100)+"%\t");
			}
			System.out.println("\n");
		}
		
		
	}
	
}
