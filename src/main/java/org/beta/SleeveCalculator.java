package org.beta;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.sorters.CardsEditionSorter;

import com.google.common.collect.Lists;

public class SleeveCalculator {
	
	protected static Logger logger = MTGLogger.getLogger(SleeveCalculator.class);
	
	
	public static void main(String[] args) throws IOException {
	
		MTGControler.getInstance().getEnabledCardsProviders().init();
		
		MagicEdition ed = new MagicEdition();
		ed.setId("TMP");
		List<MagicCard> cards = MTGControler.getInstance().getEnabledCardsProviders().searchCardByEdition(ed);
		Collections.sort(cards, new CardsEditionSorter());
		
		int l = 3;
		int c = 3;
		int page = l*c;
		int feuille = page*2;
		
		List<List<MagicCard>> pages = Lists.partition(cards, feuille);
		
		logger.info("cards number :" + cards.size() +" Workbook sheets :" + pages.size());
		
		int currentSheet=1;
		int line=1;
		int column=1;
		int currentCard=1;
		
		String rv = "recto";
		for(List<MagicCard> currentPage : pages)
		{	
			for(MagicCard card : currentPage) 
			{
				System.out.println(currentCard+"\t"+card.getName() + "\tsheet="+currentSheet + "\tc="+column+ "\tl=" + line +"\t" + rv);
				column++;
				if(column>c)
				{
					column=1;
					line++;
				}
				
				if((currentCard%(page))==0)
				{
					line=1;
					rv=rv.equals("recto")?"verso":"recto";
				}
				currentCard++;
				
			}
			currentSheet++;
			System.out.println("");
			
			
			
		}
		
		
		
		
		
	}
}
