package test;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.logging.log4j.Level;
import org.junit.Test;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGSealedProvider;
import org.magic.services.MTGControler;
import org.magic.services.logging.MTGLogger;
import org.magic.services.tools.MTG;

public class TestSealeds {

	
	
	@Test
	public void testUrls() throws SQLException, IOException
	{
		MTGControler.getInstance().init();
		
		MTGLogger.changeLevel(Level.OFF);
		
		var prov = MTG.getEnabledPlugin(MTGCardsProvider.class);
		var plug = MTG.getEnabledPlugin(MTGSealedProvider.class);
		
		
		for(var ed : prov.listEditions().stream().filter(ed->ed.getId().equals("XLN")).toList())
		{
			var items = plug.getItemsFor(ed);
			
			for(var s : items)
				System.out.println(ed.getId() + "\t" + s.getTypeProduct() + "\t"+s.getNum()+"\t : " +(plug.getPictureFor(s)!=null));
			
		}
		
		
	}
}
