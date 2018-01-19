package unit.apps;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.magic.api.beans.MagicEdition;
import org.magic.services.BoosterPicturesProvider;
import org.magic.services.ModuleInstaller;

public class BoostersTest {

	BoosterPicturesProvider installer;
	
	@Before
	public void init()
	{
		installer = new BoosterPicturesProvider();
	}
	
	
	
	@Test
	public void testURL()
	{
		for(String ed : installer.listEditionsID())
		{
			MagicEdition set = new MagicEdition();
			set.setId(ed);
			installer.getBoosterFor(set);
			
		}
		 
	}
}
