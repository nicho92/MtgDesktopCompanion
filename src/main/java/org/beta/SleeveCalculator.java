package org.beta;

import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.services.MTGControler;

import com.google.common.collect.Lists;

public class SleeveCalculator {
	public static void main(String[] args) throws IOException {
	
		MTGControler.getInstance().getEnabledCardsProviders().init();
		
		MagicEdition ed = new MagicEdition();
		ed.setId("TSP");
		ed.setSet("TSP");
		List<MagicCard> cards = MTGControler.getInstance().getEnabledCardsProviders().searchCardByEdition(ed);
		int l = 3;
		int c = 3;
		int page = l*c;
		int feuille = page*2;
		
		List<List<MagicCard>> partition = Lists.partition(cards, feuille);
		
		System.out.println("cards number :" + cards.size());
		System.out.println("Workbook sheets :" + partition.size());
		
		
	}
}
