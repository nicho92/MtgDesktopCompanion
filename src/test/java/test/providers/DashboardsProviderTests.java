package test.providers;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MTGFormat;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.services.PluginRegistry;

import test.TestTools;

public class DashboardsProviderTests {

	private MagicCard mc;

	@Before
	public void initTest() throws IOException, URISyntaxException
	{
		TestTools.initTest();
		mc = TestTools.loadData().get(0);
	}
	
	
	

	@Test
	public void launch()
	{
		PluginRegistry.inst().listPlugins(MTGDashBoard.class).forEach(p->{
			testPlugin(p);	
		});
	}
	
	
	
	
	public void testPlugin(MTGDashBoard p)
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
		System.out.println("CURRENCY " + p.getCurrency());
		
		
			try {
				p.getShakesForEdition(mc.getCurrentSet());
				System.out.println("get Shakes for " + mc.getCurrentSet() + " OK");
			} catch (IOException e) {
				System.out.println("get Shakes for " + mc.getCurrentSet() + " ERROR "+e);
				e.printStackTrace();
			}
			
			
			try {
				p.getBestCards(MTGFormat.FORMATS.STANDARD, p.getDominanceFilters()[0]);
				System.out.println("get Best for " + MTGFormat.FORMATS.STANDARD + " OK");
			} catch (IOException e) {
				System.out.println("get Best for " + MTGFormat.FORMATS.STANDARD + " ERROR "+e);
			}
		
			
			try {
				p.getPriceVariation(mc, false);
				System.out.println("get Variation for " + mc + "("+mc.getCurrentSet()+") OK");
			} catch (IOException e) {
				System.out.println("get Variation for " + mc + "("+mc.getCurrentSet()+") ERROR "+e);
			}
			
			try {
				p.getPriceVariation(mc, true);
				System.out.println("get Variation for " + mc + "("+mc.getCurrentSet()+") OK");
			} catch (IOException e) {
				System.out.println("get Variation for " + mc + "("+mc.getCurrentSet()+") ERROR "+e);
			}

			
			
	}
	
	
}
