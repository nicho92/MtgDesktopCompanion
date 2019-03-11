package test.providers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGCardsProvider;
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
import org.magic.api.pricers.impl.PriceMinisterPricer;
import org.magic.api.pricers.impl.StarCityGamesPricer;
import org.magic.api.pricers.impl.TCGPlayerPricer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.PluginRegistry;

import test.data.LoadingData;

public class PriceProviderTests {

	MagicCard mc;
	
	@Before
	public void initTest() throws IOException, URISyntaxException
	{
		MTGConstants.CONF_DIR = new File(System.getProperty("user.home") + "/.magicDeskCompanion-test/");
		MTGLogger.changeLevel(Level.OFF);
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).init();
		mc = new LoadingData().cardsTest().get(0);
	}
	
	@Test
	public void launch()
	{
		PluginRegistry.inst().listPlugins(MTGPricesProvider.class).forEach(p->{
			testPlugin(p);	
		});
	}
	
	public void testPlugin(MTGPricesProvider p)
	{
		
		System.out.println("*****************************"+p.getName());
		System.out.println("STAT "+p.getStatut());
		System.out.println("PROP "+p.getProperties());
		System.out.println("TYPE "+p.getType());
		System.out.println("ENAB "+p.isEnable());
		System.out.println("ICON "+p.getIcon());
		System.out.println("VERS "+p.getVersion());
		System.out.println("JMX NAME "+p.getObjectName());
		System.out.println("CONF FILE " + p.getConfFile());
						
			try {
				List<MagicPrice> prices = p.getPrice(mc.getCurrentSet(), mc);
				System.out.println(prices);
			} catch (Exception e) {
				e.printStackTrace();
			}
	
	
		
		
	}
	
	
}
