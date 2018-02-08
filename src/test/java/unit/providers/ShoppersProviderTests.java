package unit.providers;

import java.io.IOException;

import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.dashboard.impl.MTGPriceDashBoard;
import org.magic.api.dashboard.impl.MTGStockDashBoard;
import org.magic.api.dashboard.impl.MTGoldFishDashBoard;
import org.magic.api.interfaces.DashBoard;
import org.magic.api.interfaces.MagicShopper;
import org.magic.api.interfaces.abstracts.AbstractDashBoard.FORMAT;
import org.magic.api.shopping.impl.EbayShopper;
import org.magic.api.shopping.impl.LeboncoinShopper;
import org.magic.api.shopping.impl.PriceMinisterShopper;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class ShoppersProviderTests {

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
					 ed.setMultiverse_id("3");
					 ed.setNumber("232");
					 ed.setMkm_id(1);
					 ed.setMkm_name("Alpha");
		mc.getEditions().add(ed);
	}
	
	@Test
	public void initTests()
	{
		
		MTGControler.getInstance().getEnabledProviders().init();
		
		
		test(new EbayShopper());
		test(new LeboncoinShopper());
		test(new PriceMinisterShopper());
	}
	
	
	
	public void test(MagicShopper p)
	{
		
			System.out.println("*****************************"+p.getName());
			System.out.println("STAT "+p.getStatut());
			System.out.println("PROP "+p.getProperties());
			System.out.println("TYPE "+p.getType());
			System.out.println("ENAB "+p.isEnable());
			
			p.search("cartes magic");
			
			
	}
	
	
}
