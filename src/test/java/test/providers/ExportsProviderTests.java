package test.providers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.MTGDeck;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGCardsExport.MODS;
import org.magic.services.MTGControler;
import org.magic.services.PluginRegistry;

import test.TestTools;

public class ExportsProviderTests {

	List<MTGCard> cards;
	
	@Before
	public void initTest() throws IOException, URISyntaxException
	{
		TestTools.initTest();
		cards = TestTools.loadData();
	}
	
	
	@Test
	public void launch()
	{
		PluginRegistry.inst().listPlugins(MTGCardsExport.class).forEach(p->{
			testPlugin(p);	
		});
	}
	
	
	
	public void testPlugin(MTGCardsExport p)
	{
		
			MTGDeck d = MTGDeck.toDeck(cards);
					  d.getSideBoard().put(cards.get(0), 3);
			
			System.out.println("*****************************"+p.getName());
			System.out.println("EXT  "+p.getFileExtension());
			System.out.println("STAT "+p.getStatut());
			System.out.println("PROP "+p.getProperties());
			System.out.println("TYPE "+p.getType());
			System.out.println("ENAB "+p.isEnable());
			System.out.println("ICON "+p.getIcon());
			System.out.println("VERS "+p.getVersion());
			System.out.println("JMX NAME "+p.getObjectName());
			System.out.println("CONF FILE " + p.getConfFile());
			
			File destD = new File("target","TEST-"+p.getName()+"-DECK"+p.getFileExtension());
			File destS = new File("target","TEST-"+p.getName()+"-STOCK"+p.getFileExtension());
			
			
			
			if(p.getMods()==MODS.BOTH || p.getMods()==MODS.EXPORT)
			{
					
					try {
						p.exportDeck(d, destD);
						System.out.println(p + " export Deck OK");
					} catch (Exception e) {
						System.out.println(p + " export Deck ERROR " +e);
					}
					
					
					List<MTGCardStock> stocks = new ArrayList<>();
					
					for(MTGCard mc : cards)
					{ 
						MTGCardStock s = MTGControler.getInstance().getDefaultStock();
									s.setProduct(mc);
									s.setMagicCollection(new MTGCollection("TEST"));
									s.setPrice(9999.0);
									s.setComment("Test");
									stocks.add(s);
					}
					try {
						p.exportStock(stocks, destS);
						System.out.println(p + " export Stock OK");
					} catch (Exception e) {
						System.out.println(p + " export Stock ERROR"+e);
					}
			}
			
			if(p.getMods()==MODS.BOTH || p.getMods()==MODS.IMPORT)
			{
				MTGDeck d2;
				try {
					d2 = p.importDeckFromFile(destD);
					System.out.println(d2 + " " + " import deck OK");
				} catch (Exception e) {
					System.out.println(p + " import deck ERROR "+e);
					e.printStackTrace();
				}
				
				try {
					System.out.println(p.importStockFromFile(destS));
					System.out.println(p + " " + " import stock OK");
				} catch (Exception e) {
					System.out.println(p + " " + " import stock ERROR "+ e);
				}
			}
	
		
		
	}
	
	
}
