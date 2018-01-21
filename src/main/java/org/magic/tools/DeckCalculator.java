package org.magic.tools;

import java.io.File;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

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
		
		int L = deck.getNbCards();
		int d=7;
		int XL=4;
		int Xd=0;
		int maxTurn=10;
		double P = 0;
		for(MagicCard mc : deck.getMap().keySet())
		{
			System.out.println(mc);
			Xd=0;
			d=7;
			XL=deck.getMap().get(mc);
			
			for(int i=0;i<maxTurn;i++)
			{
				Xd=Xd+1;
				if(Xd>=XL)
					Xd=XL;
				
				d=d+1;
				double v1 = XL*d;
				double v2 = Xd*L;
				P=v1/v2;
				System.out.print("T"+(i+1)+"="+NumberFormat.getInstance().format(P*100)+"%\t");
			}
			System.out.println("\n");
		}
		
		
	}
	
}
