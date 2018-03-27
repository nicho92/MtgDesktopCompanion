package unit.providers;

import org.apache.log4j.Level;
import org.junit.Test;
import org.magic.services.MTGLogger;
import org.magic.services.extra.BoosterPicturesProvider;

public class BoosterProviderTests {

	
	@Test
	public void test()
	{
		MTGLogger.changeLevel(Level.OFF);
		BoosterPicturesProvider prov = new BoosterPicturesProvider();
		for(String id : prov.listEditionsID())
		{
			System.out.println(id+";"+(prov.getBoosterFor(id)!=null));
		}
		
	}
	
		
	
}
