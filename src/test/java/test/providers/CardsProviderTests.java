package test.providers;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumExtra;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.services.MTGControler;
import org.magic.services.PluginRegistry;

import test.TestTools;

public class CardsProviderTests {

	@Before
	public void initTest() throws IOException, URISyntaxException
	{
		TestTools.initTest();
		MTGControler.getInstance();
	}
	
	@Test
	public void launch()
	{
		PluginRegistry.inst().listPlugins(MTGCardsProvider.class).forEach(this::testPlugin);
	}
	
	
	
	public void testPlugin(MTGCardsProvider p)
	{
		
			p.init();
			System.out.println("*****************************"+p.getName());
			System.out.println("STAT "+p.getStatut());
			System.out.println("PROP "+p.getProperties());
			System.out.println("TYPE "+p.getType());
			System.out.println("ENAB "+p.isEnable());
			System.out.println("ICON "+p.getIcon());
			System.out.println("VERS "+p.getVersion());
			System.out.println("JMX NAME "+p.getObjectName());
			System.out.println("CONF FILE " + p.getConfFile());
			System.out.println("ATTS " + p.getQueryableAttributs());
			System.out.println("LANG " + p.getLanguages());
		
			try {
				p.listEditions();
				System.out.println("LOAD EDITION :OK");
			} catch (Exception e) {
				System.out.println("LOAD EDITION :ERROR " + e);
				e.printStackTrace();
			}
			try {
				p.searchCardByName( "Black Lotus", new MTGEdition("LEA"), true);
				System.out.println("SEARCH CARD :OK");
			} catch (Exception e) {
				System.out.println("SEARCH CARD :ERROR " + e);
			}
			try {
				p.searchCardByName( "Black Lotus", null, false);
				System.out.println("SEARCH CARD :OK");
			} catch (Exception e) {
				System.out.println("SEARCH CARD :ERROR " + e);
			}
			try {
				p.getSetById("LEA");
				System.out.println("SET BY ID :OK");
			} catch (Exception e) {
				System.out.println("SET BY ID :ERROR " + e);
			}
			
			try {
				p.getCardByNumber("124", new MTGEdition("LEA"));
				System.out.println("CARD BY NUMBER :OK");
			} catch (Exception e) {
				System.out.println("CARD BY NUMBER :ERROR " + e);
			}
			
			
			try {
				p.getCardByNumber("123", "LEA");
				System.out.println("CARD BY NUMBER :OK");
			} catch (Exception e) {
				System.out.println("CARD BY NUMBER :ERROR " + e);
			}

		
			try {
				p.generateBooster(new MTGEdition("LEA"),EnumExtra.DRAFT,1).get(0);
				System.out.println("BOOSTER GEN :OK");
			} catch (Exception e) {
				System.out.println("BOOSTER GEN :ERROR " + e);
			}
			
			try {
				p.getSetByName("Futur Sight");
				System.out.println("Search set by Name :OK");
			} catch (Exception e) {
				System.out.println("Search set by Name " + e);
			}
			
			
			
		
	}
	
	
}
