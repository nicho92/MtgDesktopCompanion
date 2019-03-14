package test.cardanalyse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.game.model.factories.AbilitiesFactory;
import org.magic.services.MTGControler;

public class CardAnalyzeTest {

	
	@Test
	public void test() throws IOException
	{
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).init();
		
		String[] test = new String[] {"Rix Maadi Reveler"};
		
		List<MagicCard> list = new ArrayList<>();
		
		for(String s : test)
			list.add(MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByCriteria("name", s, null, false).get(0));
		
		
		for(int index=0;index<list.size();index++) {
			System.out.println("----------------------------------------------------"+list.get(index));
			System.out.println(list.get(index).getText());
			System.out.println("----------------------------------------------------");
			System.out.println(AbilitiesFactory.getInstance().getAbilities(list.get(index)));
			
			
		}	
	}
	
	
}
