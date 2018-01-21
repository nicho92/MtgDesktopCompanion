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

	
	public static void main(String[] args) throws Exception {
		MagicDeck deck = new MTGDesktopCompanionExport().importDeck(new File("C:\\Users\\Nicolas\\.magicDeskCompanion\\decks\\grixis death shadow.deck"));
		/*
				P = Probability
				L = the number of cards in your deck
				d = the total number of cards you've drawn so far in the game (so, 7 for opening hand, and one more for each extra card drawn so far)
				XL = the number of copies of card "X" you have in your deck
				Xd = the total number of copies of X that you've drawn so far
		*/
		
		/*
		 * H (n) = C (X, n) * C (Y - X, Z - n) / C (Y, Z)
			X standing for the number of a certain card that you have in the deck.
			Y is the number of cards in the deck.
			Z is the number of cards you are drawing.
			N is the number you are checking for.
	 * */
		
		int L = deck.getNbCards();
		int d=7;
		int XL=4;
		int Xd=0;
		int maxTurn=10;
		HypergeometricDistribution P;	
		for(MagicCard mc : deck.getMap().keySet())
		{
			System.out.println(mc + "(x"+deck.getMap().get(mc)+")");
			Xd=0;
			d=7;
			XL=deck.getMap().get(mc);
			
			
			for(int i=0;i<maxTurn+1;i++)
			{
				Xd=Xd+1;
				if(Xd>=XL)
					Xd=XL;
				
				P= new HypergeometricDistribution(L, XL, d);
				
				System.out.print("T"+(i)+"="+new DecimalFormat("#0.0").format(P.upperCumulativeProbability(1)*100)+"%\t");
				d=d+1;
			}
			System.out.println("\n");
		}
		
		
	}
	
}
