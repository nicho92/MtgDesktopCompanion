package unit.providers;

import java.io.IOException;

import org.apache.log4j.Level;
import org.junit.Test;
import org.magic.api.beans.MagicEdition;
import org.magic.services.MTGLogger;
import org.magic.services.extra.BoosterPicturesProvider;
import org.magic.tools.URLTools;

public class BoosterProviderTests {

	
	@Test
	public void test()
	{
		MTGLogger.changeLevel(Level.OFF);
		BoosterPicturesProvider prov = new BoosterPicturesProvider();
		for(String id : prov.listEditionsID())
		{
			prov.getItemsFor(new MagicEdition(id)).forEach(e->{
				
				System.out.println("===================="+id);
				try {
					URLTools.extractImage(e.getUrl());
					System.out.println(e.getEdition()+";OK;"+e.getType());
				} catch (IOException e1) {
					System.out.println(e.getType() + " " + e+";"+e1);
				}
			});
			
			
			
			
		}
		
	}
	
		
	
}
