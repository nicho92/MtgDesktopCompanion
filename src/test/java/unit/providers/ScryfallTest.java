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
			String[] attributes ={"name","fullType", "editions", "rotatedCardName","multiverseid"};
			
			List<MagicCard> list = prov.searchCardByCriteria("name", "Liliana, Heretical Healer", null, true);
			IASCIITableAware asciiTableAware = new CollectionASCIITableAware<MagicCard>(list,attributes);
	    	new ASCIITableImpl(System.out).printTable(asciiTableAware);
	    	
	    	MagicCard cardLiliana = list.get(1);
	    	System.out.println("rotation of " + cardLiliana);
	    	
	    	list= prov.searchCardByCriteria("name", cardLiliana.getRotatedCardName(), cardLiliana.getEditions().get(0), true);
	    	asciiTableAware = new CollectionASCIITableAware<MagicCard>(list,attributes);
	    	new ASCIITableImpl(System.out).printTable(asciiTableAware);
	    	
	    	
	    	
	    	
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
