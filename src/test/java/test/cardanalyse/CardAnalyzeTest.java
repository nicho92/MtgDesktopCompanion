package test.cardanalyse;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;
import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.game.model.factories.AbilitiesFactory;

import test.TestTools;

public class CardAnalyzeTest {

	
	@Test
	public void test() throws IOException, URISyntaxException
	{
		getEnabledPlugin(MTGCardsProvider.class).init();
		
		for(MagicCard mc : TestTools.loadData()) {
			System.out.println("----------------------------------------------------"+mc);
			System.out.println(mc.getText());
			System.out.println("----------------------------------------------------");
			System.out.println(AbilitiesFactory.getInstance().getAbilities(mc));
			
			
		}	
	}
	
	
}
