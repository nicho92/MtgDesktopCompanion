package unit.providers;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.cache.impl.FileCache;
import org.magic.api.cache.impl.MemoryCache;
import org.magic.api.cache.impl.NoCache;
import org.magic.api.interfaces.MTGPicturesCache;
import org.magic.api.interfaces.PictureProvider;
import org.magic.api.pictures.impl.DeckMasterPicturesProvider;
import org.magic.api.pictures.impl.GathererPicturesProvider;
import org.magic.api.pictures.impl.MagicCardInfoPicturesProvider;
import org.magic.api.pictures.impl.MagidexPicturesProvider;
import org.magic.api.pictures.impl.MythicSpoilerPicturesProvider;
import org.magic.api.pictures.impl.ScryFallPicturesProvider;
import org.magic.services.MTGControler;

public class PicturesProviderTests {

	PictureProvider p;
	MagicCard mc;
	MagicEdition ed;
	
	@Before
	public void removeCache()
	{
		
		List<MTGPicturesCache> caches = MTGControler.getInstance().getListCaches();
		MTGControler.getInstance().getListCaches().removeAll(caches);
		
		MTGPicturesCache cache = new NoCache();
		cache.enable(true);
		
		MTGControler.getInstance().getListCaches().add(cache);
		
		
		
		
		
	}

	
	@Before
	public void createCards()
	{
		System.out.print("create cards....");
		mc = new MagicCard();
		mc.setName("Black Lotus");
		mc.setLayout("normal");
		mc.setCost("{0}");
		mc.setCmc(0);
		mc.getTypes().add("Artifact");
		mc.setReserved(true);
		//mc.setMultiverseid(3);
		mc.setText("{T}, Sacrifice Black Lotus: Add three mana of any one color to your mana pool.");
		mc.setRarity("Rare");
		mc.setArtist("Christopher Rush");
		mc.setId("c944c7dc960c4832604973844edee2a1fdc82d98");
		mc.setMciNumber("232");
		MagicEdition ed = new MagicEdition();
					 ed.setId("lea");
					 ed.setSet("Limited Edition Alpha");
					 ed.setBorder("Black");
					 ed.setRarity("Rare");
					 ed.setArtist("Christopher Rush");
					 ed.setMultiverse_id("3");
					 ed.setNumber("232");
		
		mc.getEditions().add(ed);
		System.out.println("done");
		
		
	}
	
	@Test
	public void testProviders()
	{
		
		try {
			p = new ScryFallPicturesProvider();
			p.getPicture(mc, ed);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		try {
			p = new MagicCardInfoPicturesProvider();
			p.getPicture(mc, ed);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		try {
			p = new GathererPicturesProvider();
			p.getPicture(mc, ed);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		try {
			p = new MagidexPicturesProvider();
			p.getPicture(mc, ed);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		try {
			p = new MythicSpoilerPicturesProvider();
			p.getPicture(mc, ed);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		try {
			p = new DeckMasterPicturesProvider();
			p.getPicture(mc, ed);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
	}
	
	
}
