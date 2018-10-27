package unit.providers;

import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.cache.impl.FileCache;
import org.magic.api.cache.impl.JCSCache;
import org.magic.api.cache.impl.MemoryCache;
import org.magic.api.cache.impl.NoCache;
import org.magic.api.interfaces.MTGPicturesCache;
import org.magic.api.pictures.impl.ScryFallPicturesProvider;
import org.magic.services.MTGLogger;

public class CacheProviderTests {

	MagicCard mc;
	MagicEdition ed;
	
	@Before
	public void removeCache()
	{
		MTGLogger.changeLevel(Level.ERROR);
		
		
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
		test(new NoCache());
		test(new MemoryCache());
		test(new FileCache());
		test(new JCSCache());
		
	}
	
	public void test(MTGPicturesCache p)
	{
		
		
		System.out.println("****************"+p);
		System.out.println(p.getStatut());
		System.out.println(p.getType());
		System.out.println("VERS "+p.getVersion());
		
		try {
			p.put(new ScryFallPicturesProvider().getPicture(mc, ed), mc, ed);
			System.out.println("putPictures OK" );
		}
		catch(Exception e)
		{
			System.out.println("putPictures ERROR "+e );
		}
		
		
		try {
			
			p.getPic(mc, ed);
			System.out.println("getPictures OK" );
		} catch (Exception e) {
			System.out.println("getPictures ERROR "+e );
		}

	}
	
	
}
