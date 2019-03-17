package test.cardanalyse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.Test;
import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.game.model.factories.AbilitiesFactory;
import org.magic.services.MTGControler;

import test.TestTools;

public class CardAnalyzeTest {

	
	@Test
	public void test() throws IOException, URISyntaxException
	{
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).init();
		
		List<MagicCard> list = TestTools.loadData();
		
		for(int index=0;index<list.size();index++) {
			System.out.println("----------------------------------------------------"+list.get(index));
			System.out.println(list.get(index).getText());
			System.out.println("----------------------------------------------------");
			System.out.println(AbilitiesFactory.getInstance().getAbilities(list.get(index)));
			
			
		}	
	}
	
	
}
