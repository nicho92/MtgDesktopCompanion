package test.providers;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.IOException;

import org.apache.logging.log4j.Level;
import org.junit.Test;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGSealedProvider;
import org.magic.services.MTGControler;
import org.magic.services.logging.MTGLogger;
import org.magic.services.network.URLTools;
import org.magic.services.tools.MTG;

public class BoosterProviderTests {

	
	@Test
	public void test()
	{
		MTGControler.getInstance();
		getEnabledPlugin(MTGCardsProvider.class).init();
		MTGLogger.changeLevel(Level.OFF);
		
		for(MagicEdition id : MTG.getEnabledPlugin(MTGSealedProvider.class).listAvailableEditions())
		{
			MTG.getEnabledPlugin(MTGSealedProvider.class).getItemsFor(id).forEach(e->{
				try {
					URLTools.extractAsImage(e.getUrl());
					System.out.println(e+";"+e.getTypeProduct()+";"+e.getEdition().getId()+";OK");
				} catch (IOException e1) {
					System.out.println(e+";"+e.getTypeProduct()+";"+e.getEdition().getId()+";KO;"+e1);
				}
			});
			
			
			
			
		}
		
	}
	
		
	
}
