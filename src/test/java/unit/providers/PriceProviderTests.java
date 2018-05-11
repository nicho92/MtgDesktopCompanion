package unit.providers;

import java.util.List;

import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.api.pricers.impl.CardKingdomPricer;
import org.magic.api.pricers.impl.ChannelFireballPricer;
import org.magic.api.pricers.impl.DeckTutorPricer;
import org.magic.api.pricers.impl.EbayPricer;
import org.magic.api.pricers.impl.MTGPricePricer;
import org.magic.api.pricers.impl.MagicBazarPricer;
import org.magic.api.pricers.impl.MagicCardMarketPricer2;
import org.magic.api.pricers.impl.MagicTradersPricer;
import org.magic.api.pricers.impl.MagicVillePricer;
import org.magic.api.pricers.impl.TCGPlayerPricer;
import org.magic.services.MTGLogger;

public class PriceProviderTests {

	MagicCard mc;
	MagicEdition ed;
	
	
	@Before
	public void initLogger()
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
					 ed = new MagicEdition();
					 ed.setId("LEA");
					 ed.setSet("Limited Edition Alpha");
					 ed.setBorder("Black");
					 ed.setRarity("Rare");
					 ed.setArtist("Christopher Rush");
					 ed.setMultiverseid("3");
					 ed.setNumber("232");
					 ed.setMkmid(1);
					 ed.setMkmName("Alpha");
		mc.getEditions().add(ed);
	}
	
	@Test
	public void initTests()
	{
		
		test(new CardKingdomPricer());
		test(new ChannelFireballPricer());
		test(new DeckTutorPricer());
		test(new EbayPricer());
		test(new MagicBazarPricer());
		test(new MagicCardMarketPricer2());
		test(new MagicTradersPricer());
		test(new MagicVillePricer());
		test(new MTGPricePricer());
		test(new TCGPlayerPricer());
		
	}
	
	
	
	public void test(MTGPricesProvider p)
	{
		
			System.out.println("*****************************"+p.getName());
			System.out.println("STAT "+p.getStatut());
			System.out.println("PROP "+p.getProperties());
			System.out.println("TYPE "+p.getType());
			System.out.println("ENAB "+p.isEnable());
			System.out.println("VERS "+p.getVersion());
						
			try {
				List<MagicPrice> prices = p.getPrice(ed, mc);
				System.out.println(prices);
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
	
	
		
		
	}
	
	
}
