package test.providers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.services.PluginRegistry;

import test.TestTools;

public class PriceProviderTests {

	MagicCard mc;
	
	@Before
	public void initTest() throws IOException, URISyntaxException
	{
		TestTools.initTest();
		mc = TestTools.loadData().get(0);
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
