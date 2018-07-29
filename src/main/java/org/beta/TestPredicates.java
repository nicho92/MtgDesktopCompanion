package org.beta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.game.model.factories.AbilitiesFactory;
import org.magic.services.MTGControler;

public class TestPredicates {

	
	public static void main(String[] args) throws IOException {
		MTGControler.getInstance().getEnabledCardsProviders().init();
		
		String[] test = new String[] {"Pawn of Ulamog","Ulamog, the Ceaseless Hunger","Liliana's Contract","Sorin, Grim Nemesis","Ring of Evos Isle","Tasigur, the Golden Fang","Wall of Air"};
		
		List<MagicCard> list = new ArrayList<>();
		
		for(String s : test)
			list.add(MTGControler.getInstance().getEnabledCardsProviders().searchCardByCriteria("name", s, null, false).get(0));
		
		
		for(int index=0;index<list.size();index++) {
			System.out.println("----------------------------------------------------"+list.get(index));
			System.out.println(list.get(index).getText());
			System.out.println("----------------------------------------------------");
			System.out.println(AbilitiesFactory.getInstance().getAbilities(list.get(index)));
		}
		
	}
}
