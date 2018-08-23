package unit.providers;

import java.util.List;

import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.cache.impl.NoCache;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.MTGPicturesCache;
import org.magic.api.pictures.impl.DeckMasterPicturesProvider;
import org.magic.api.pictures.impl.GathererPicturesProvider;
import org.magic.api.pictures.impl.MagicCardInfoPicturesProvider;
import org.magic.api.pictures.impl.MagidexPicturesProvider;
import org.magic.api.pictures.impl.MythicSpoilerPicturesProvider;
import org.magic.api.pictures.impl.ScryFallPicturesProvider;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class PicturesProviderTests {

	MagicCard mc;
	MagicEdition ed;
	
	@Before
	public void removeCache()
	{
		MTGLogger.changeLevel(Level.ERROR);
		
		List<MTGPicturesCache> caches = MTGControler.getInstance().getCachesProviders();
		MTGControler.getInstance().getCachesProviders().removeAll(caches);
		
		MTGPicturesCache cache = new NoCache();
		cache.enable(true);
		
		MTGControler.getInstance().getCachesProviders().add(cache);
	}

	
	@Before
	public void createCards()
	{
		mc = new MagicCard();
		mc.setName("Black Lotus");
		mc.setLayout("normal");
		mc.setCost("{0}");
		mc.setCmc(0);
		mc.getTypes().add("Artifact");
		mc.setReserved(true);
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
					 ed.setMultiverseid("3");
					 ed.setNumber("232");
		
		mc.getEditions().add(ed);
	}
	
	
	@Test
	public void test()
	{
		testProviders(new ScryFallPicturesProvider());
		testProviders(new MagicCardInfoPicturesProvider());
		testProviders(new GathererPicturesProvider());
		testProviders(new ScryFallPicturesProvider());
		testProviders(new MagidexPicturesProvider());
		testProviders(new MythicSpoilerPicturesProvider());
		testProviders(new DeckMasterPicturesProvider());
		
	}
	
	public void testProviders(MTGPictureProvider p)
	{
		
		
		System.out.println("****************"+p);
		System.out.println(p.getStatut());
		System.out.println(p.getType());
		System.out.println("VERS "+p.getVersion());
		
		
		try {
			p.getPicture(mc, ed);
			System.out.println("getPictures OK" );
		} catch (Exception e) {
			System.out.println("getPictures ERROR "+e );
		}

		try {
			p.extractPicture(mc);
			System.out.println("extractPicture OK" );
		} catch (Exception e) {
			System.out.println("getPictures ERROR " +e);
		}

		try {
			p.getSetLogo(ed.getId(), "Rare");
			System.out.println("getLogo OK" );
		} catch (Exception e) {
			System.out.println("getLogo ERROR "+e );
		}
		
		try {
			p.getPicture(mc, ed);
			System.out.println("getPictures cache OK" );
		} catch (Exception e) {
			System.out.println("getPictures ERROR "+e );
		}
		
		p.getBackPicture();

		
		
	}
	
	
}
