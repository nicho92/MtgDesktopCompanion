package org.beta;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

import com.google.common.collect.Lists;

public class SleeveCalculator {
	
	protected static Logger logger = MTGLogger.getLogger(SleeveCalculator.class);
	
	
	public static void main(String[] args) throws IOException {
	
		MTGControler.getInstance().getEnabledCardsProviders().init();
		
		MagicEdition ed = new MagicEdition();
		ed.setId("TSP");
		List<MagicCard> cards = MTGControler.getInstance().getEnabledCardsProviders().searchCardByEdition(ed);
		int l = 3;
		int c = 3;
		int page = l*c;
		int feuille = page*2;
		
		List<List<MagicCard>> partition = Lists.partition(cards, feuille);
		
		logger.info("cards number :" + cards.size() +" Workbook sheets :" + partition.size());
		
		
	}
}
