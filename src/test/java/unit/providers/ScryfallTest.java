package unit.providers;

import java.util.ArrayList;
import java.util.List;

import org.asciitable.impl.ASCIITableImpl;
import org.asciitable.impl.CollectionASCIITableAware;
import org.asciitable.spec.IASCIITableAware;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.providers.impl.ScryFallProvider;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.decksniffer.impl.TappedOutDeckSniffer;

public class ScryfallTest {
	
	ScryFallProvider prov;
	
	
	@Before
	public void connexion()
	{
		prov = new ScryFallProvider();
		prov.init();
		
	}
	
	@Test
	public void testSearch()
	{
		try {
			List<MagicEdition> res = prov.loadEditions();
			
			for(MagicEdition ed : res)
			{
				System.out.println(ed.getId());
			}
		} catch (Exception e) {
			fail("error" + e);
		}
	}
	
}
