package unit.providers;

import java.io.IOException;

import org.apache.log4j.Level;
import org.junit.Test;
import org.magic.api.beans.MagicEdition;
import org.magic.services.MTGLogger;
import org.magic.services.extra.PackagesProvider;
import org.magic.tools.URLTools;

public class BoosterProviderTests {

	
	@Test
	public void test()
	{
		MTGLogger.changeLevel(Level.OFF);
		PackagesProvider prov = PackagesProvider.inst();
		for(MagicEdition id : prov.listEditions())
		{
			prov.getItemsFor(id).forEach(e->{
				
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
