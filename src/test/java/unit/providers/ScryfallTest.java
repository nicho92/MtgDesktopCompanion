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
	public static String[] attributes ={"name","fullType", "editions", "colors", "cost"};

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
			List<MagicCard> list = prov.searchCardByCriteria("name", "Jace, Telepath Unbound", null, true);
			IASCIITableAware asciiTableAware = new CollectionASCIITableAware<MagicCard>(list,attributes);
	    	new ASCIITableImpl(System.out).printTable(asciiTableAware);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
