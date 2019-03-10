package test.providers;

import java.net.MalformedURLException;

import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.providers.impl.MagicTheGatheringIOProvider;
import org.magic.api.providers.impl.Mtgjson4Provider;
import org.magic.api.providers.impl.PrivateMTGSetProvider;
import org.magic.api.providers.impl.ScryFallProvider;
import org.magic.services.MTGLogger;

public class CardsProviderTests {

	MagicCard mc;
	MagicEdition ed;
	
	@Before
	public void createCards()
	{
		MTGLogger.changeLevel(Level.DEBUG);
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
		mc.setNumber("232");
					 ed = new MagicEdition();
					 ed.setId("LEA");
					 ed.setSet("Limited Edition Alpha");
					 ed.setBorder("Black");
					 ed.setRarity("Rare");
					 ed.setArtist("Christopher Rush");
					 ed.setMultiverseid("3");
					 ed.setNumber(mc.getNumber());
		
		mc.getEditions().add(ed);
	}
	
	@Test
	public void initTests()
	{
		testProviders(new ScryFallProvider(),"b0faa7f2-b547-42c4-a810-839da50dadfe");
		testProviders(new MagicTheGatheringIOProvider(),"c944c7dc960c4832604973844edee2a1fdc82d98");
		testProviders(new PrivateMTGSetProvider(),"c944c7dc960c4832604973844edee2a1fdc82d98");
		testProviders(new Mtgjson4Provider(),"b0faa7f2-b547-42c4-a810-839da50dadfe");
	}
	
	
	
	public void testProviders(MTGCardsProvider p,String id)
	{
		
			p.init();
			System.out.println("***********"+p);
			System.out.println("NAME "+p.getName());
			System.out.println("VERS "+p.getVersion());
			System.out.println("STAT "+p.getStatut());
			System.out.println("LANG "+p.getLanguages());
			System.out.println("QUER "+p.getQueryableAttributs());
			System.out.println("PROP "+p.getProperties());
			System.out.println("TYPE "+p.getType());
			System.out.println("ENAB "+p.isEnable());
			System.out.println("VERS "+p.getVersion());
			
			mc.setId(id);
			try {
				p.loadEditions();
				System.out.println("LOAD EDITION :OK");
			} catch (Exception e) {
				System.out.println("LOAD EDITION :ERROR " + e);
			}
			try {
				p.searchCardByName( mc.getName(), ed, true);
				System.out.println("SEARCH CARD :OK");
			} catch (Exception e) {
				System.out.println("SEARCH CARD :ERROR " + e);
			}
			try {
				p.searchCardByName( mc.getName(), null, false);
				System.out.println("SEARCH CARD :OK");
			} catch (Exception e) {
				System.out.println("SEARCH CARD :ERROR " + e);
			}
			try {
				p.getSetById(ed.getId());
				System.out.println("SET BY ID :OK");
			} catch (Exception e) {
				System.out.println("SET BY ID :ERROR " + e);
			}
			try {
				p.getCardById(mc.getId());
				System.out.println("CARD BY ID :OK");
			} catch (Exception e) {
				System.out.println("CARD BY ID :ERROR " + e);
			}
			
			try {
				p.getCardByNumber(mc.getNumber(), ed);
				System.out.println("CARD BY NUMBER :OK");
			} catch (Exception e) {
				System.out.println("CARD BY NUMBER :ERROR " + e);
			}
		
			try {
				p.generateBooster(ed);
				System.out.println("BOOSTER GEN :OK");
			} catch (Exception e) {
				System.out.println("BOOSTER GEN :ERROR " + e);
			}
			
			try {
				System.out.println("WEB  "+p.getWebSite());
			} catch (MalformedURLException e) {
				System.out.println("WEB ERROR" + e);
			}
		
	}
	
	
}
