package unit.providers;

import static org.junit.Assert.fail;

import java.util.List;

import org.asciitable.impl.ASCIITableImpl;
import org.asciitable.impl.CollectionASCIITableAware;
import org.asciitable.spec.IASCIITableAware;
import org.junit.Before;
import org.junit.Test;
import org.magic.api.beans.MagicCard;
import org.magic.api.providers.impl.ScryFallProvider;
import org.magic.services.MTGLogger;

public class ScryfallTest {

	ScryFallProvider prov ;
	
	@Before
	public void init()
	{
		prov = new ScryFallProvider();
		prov.init();
		
	}
	
	@Test
	public void searchCard()
	{
		
	
		try {
			String[] attributes ={"id","name","fullType", "editions", "rotatedCardName","multiverseid","colors","imageName"};
			
			List<MagicCard> list = prov.searchCardByCriteria("name", "Liliana, Heretical Healer", null, false);
			IASCIITableAware asciiTableAware = new CollectionASCIITableAware<MagicCard>(list,attributes);
	    	new ASCIITableImpl(System.out).printTable(asciiTableAware);
	    	
	    	MagicCard cardLiliana = list.get(1);
	    	System.out.println("rotation of " + cardLiliana);
	    	
	    	list= prov.searchCardByCriteria("name", cardLiliana.getRotatedCardName(), cardLiliana.getEditions().get(0), true);
	    	asciiTableAware = new CollectionASCIITableAware<MagicCard>(list,attributes);
	    	new ASCIITableImpl(System.out).printTable(asciiTableAware);
	    	
	    	
	    	
		} catch (Exception e) {
			MTGLogger.printStackTrace(e);
			fail(e.getMessage());
		}
	}
}
