package org.beta;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.sorters.CardsEditionSorter;

import com.google.common.collect.Lists;

import de.vandermeer.asciitable.AsciiTable;

public class SleeveCalculator {
	
	protected static Logger logger = MTGLogger.getLogger(SleeveCalculator.class);
	
	
	public static void main(String[] args) throws IOException {
	
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).init();
		
		MagicEdition ed = new MagicEdition("GRN");
		List<MagicCard> cards = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByEdition(ed);
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
		
		AsciiTable table = new AsciiTable();
		
		table.addRule();
		table.addRow("N°","Name","Sheet","column","row","recto/verso");
		table.addRule();
		table.getContext().setWidth(200);
		for(List<MagicCard> currentPage : pages)
		{	
			for(MagicCard card : currentPage) 
			{
				
				table.addRow(currentCard,card.getName(),currentSheet,column,line,rv);
				
				column++;
				if(column>c)
				{
					column=1;
					line++;
				}
				
				if((currentCard%(page))==0)
				{
					line=1;
					rv=rv.equals("recto")?"R":"V";
				}
				currentCard++;
				
			}
			table.addRule();
			currentSheet++;
		}
		
		
		logger.debug("\n"+table.render());
		
		
		
		
		
	}
}
