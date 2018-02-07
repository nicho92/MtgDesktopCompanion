package unit.providers;

import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.api.providers.impl.DeckbrewProvider;
import org.magic.api.providers.impl.MagicTheGatheringIOProvider;
import org.magic.api.providers.impl.MtgjsonProvider;
import org.magic.api.providers.impl.PrivateMTGSetProvider;
import org.magic.api.providers.impl.ScryFallProvider;
import org.magic.services.MTGLogger;

public class CardsProviderTests {

	MagicCardsProvider p;
	MagicCard mc;
	MagicEdition ed;
	
	@Before
	public void removeCache()
	{
		MTGLogger.changeLevel(Level.DEBUG);
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
		mc.setMciNumber("232");
					 ed = new MagicEdition();
					 ed.setId("lea");
					 ed.setSet("Limited Edition Alpha");
					 ed.setBorder("Black");
					 ed.setRarity("Rare");
					 ed.setArtist("Christopher Rush");
					 ed.setMultiverse_id("3");
					 ed.setNumber("232");
		
		mc.getEditions().add(ed);
	}
	
	@Test
	public void initTests()
	{
		testProviders(new ScryFallProvider(),"c944c7dc960c4832604973844edee2a1fdc82d98");
		testProviders(new MagicTheGatheringIOProvider(),"c944c7dc960c4832604973844edee2a1fdc82d98");
		testProviders(new DeckbrewProvider(),"c944c7dc960c4832604973844edee2a1fdc82d98");
		testProviders(new MtgjsonProvider(),"c944c7dc960c4832604973844edee2a1fdc82d98");
		testProviders(new PrivateMTGSetProvider(),"c944c7dc960c4832604973844edee2a1fdc82d98");
	}
	
	
	
	public void testProviders(MagicCardsProvider p,String id)
	{
		
		try {
			p.init();
			System.out.println("***********"+p);
			System.out.println("NAME "+p.getName());
			System.out.println("VERS "+p.getVersion());
			System.out.println("STAT "+p.getStatut());
			System.out.println("WEB  "+p.getWebSite());
			System.out.println("LANG "+p.getLanguages());
			System.out.println("QUER "+p.getQueryableAttributs());
			System.out.println("PROP "+p.getProperties());
			System.out.println("TYPE "+p.getType());
			System.out.println("ENAB "+p.isEnable());
			
			
			System.out.println("***********SEARCH");
			mc.setId(id);
			p.loadEditions();
			p.searchCardByCriteria("name", mc.getName(), ed, true);
			p.searchCardByCriteria("name", mc.getName(), null, false);
			p.getSetById(ed.getId());
			p.getCardById(mc.getId());
			
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	
		
		
	}
	
	
}
