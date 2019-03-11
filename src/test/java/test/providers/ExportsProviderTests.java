package test.providers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractCardExport.MODS;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.PluginRegistry;

import test.data.LoadingData;

public class ExportsProviderTests {

	List<MagicCard> cards;
	
	@Before
	public void initTest() throws IOException, URISyntaxException
	{
		MTGConstants.CONF_DIR = new File(System.getProperty("user.home") + "/.magicDeskCompanion-test/");
		MTGLogger.changeLevel(Level.OFF);
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).init();
		cards = new LoadingData().cardsTest();
	}
	
	@Test
	public void testPlugins()
	{
		PluginRegistry.inst().listPlugins(MTGCardsExport.class).forEach(p->{
			testExports(p);	
		});
	}
	
	
	
	public void testExports(MTGCardsExport p)
	{
		
			MagicDeck d = MagicDeck.toDeck(cards);
					  d.getMapSideBoard().put(cards.get(0), 3);
			
			System.out.println("*****************************"+p.getName());
			System.out.println("EXT  "+p.getFileExtension());
			System.out.println("STAT "+p.getStatut());
			System.out.println("PROP "+p.getProperties());
			System.out.println("TYPE "+p.getType());
			System.out.println("ENAB "+p.isEnable());
			System.out.println("ICON "+p.getIcon());
			System.out.println("VERS "+p.getVersion());
			System.out.println("NEED UI "+p.needDialogGUI());
			System.out.println("JMX NAME "+p.getObjectName());
			System.out.println("CONF FILE " + p.getConfFile());
			
			
			File destD = new File("target","TEST-"+p.getName()+"-DECK"+p.getFileExtension());
			File destL = new File("target","TEST-"+p.getName()+"-LIST"+p.getFileExtension());
			File destS = new File("target","TEST-"+p.getName()+"-STOCK"+p.getFileExtension());
			
			
			
			if(p.getMods()==MODS.BOTH || p.getMods()==MODS.EXPORT)
			{
					
					try {
						p.export(d, destD);
						System.out.println(p + " export Deck OK");
					} catch (Exception e) {
						System.out.println(p + " export Deck ERROR " +e);
					}
					try {
						p.export(cards, destL);
						System.out.println(p + " export List OK");
					} catch (Exception e) {
						System.out.println(p + " export List ERROR "+e);
					}
					
					List<MagicCardStock> stocks = new ArrayList<>();
					
					for(MagicCard mc : cards)
					{ 
						MagicCardStock s = MTGControler.getInstance().getDefaultStock();
									s.setMagicCard(mc);
									s.setMagicCollection(new MagicCollection("TEST"));
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
				MagicDeck d2;
				try {
					d2 = p.importDeck(destD);
					System.out.println(d2 + " " + " import deck OK");
				} catch (Exception e) {
					System.out.println(p + " import deck ERROR "+e);
					e.printStackTrace();
				}
				
				try {
					System.out.println(p.importStock(destS));
					System.out.println(p + " " + " import stock OK");
				} catch (Exception e) {
					System.out.println(p + " " + " import stock ERROR "+ e);
				}
			}
	
		
		
	}
	
	
}
