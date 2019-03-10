package test.providers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;
import org.magic.api.beans.EnumCondition;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.exports.impl.Apprentice2DeckExport;
import org.magic.api.exports.impl.CSVExport;
import org.magic.api.exports.impl.CocatriceDeckExport;
import org.magic.api.exports.impl.DCIDeckSheetExport;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.exports.impl.MKMFileWantListExport;
import org.magic.api.exports.impl.MTGDesktopCompanionExport;
import org.magic.api.exports.impl.MTGODeckExport;
import org.magic.api.exports.impl.MTGStockExport;
import org.magic.api.exports.impl.MagicWorkStationDeckExport;
import org.magic.api.exports.impl.MkmOnlineExport;
import org.magic.api.exports.impl.OCTGNDeckExport;
import org.magic.api.exports.impl.PDFExport;
import org.magic.api.exports.impl.XMageDeckExport;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.api.interfaces.abstracts.AbstractCardExport.MODS;
import org.magic.gui.components.MagicCardDetailPanel;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.PluginRegistry;

import test.data.LoadingData;

public class ExportsProviderTests {

	List<MagicCard> cards;
	
	@Before
	public void initLogger()
	{
		//MTGLogger.changeLevel(Level.OFF);
	}

	
	@Before
	public void createCards() throws IOException, URISyntaxException
	{
		cards = new LoadingData().cardsTest();
	}
	
	@Test
	public void initTests()
	{
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).init();
		PluginRegistry.inst().listPlugins(MTGCardsExport.class).forEach(p->{
			testExports(p);	
		});
	}
	
	
	
	public void testExports(MTGCardsExport p)
	{
		
			MagicDeck d = AbstractCardExport.toDeck(cards);
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
			
			
			File destD = new File("target",d.getName()+" DECK "+p.getFileExtension());
			File destL = new File("target",d.getName()+" LIST "+p.getFileExtension());
			File destS = new File("target",d.getName()+" STOCK "+p.getFileExtension());
			
			
			
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
						MagicCardStock s = new MagicCardStock();
									s.setMagicCard(mc);
									s.setAltered(false);
									s.setFoil(false);
									s.setSigned(false);
									s.setCondition(EnumCondition.LIGHTLY_PLAYED);
									s.setLanguage("English");
									s.setMagicCollection(new MagicCollection("TEST"));
									s.setQte(1);
									s.setPrice(9999.0);
									s.setComment("Test");
									stocks.add(s);
					}
					try {
						p.exportStock(stocks, destS);
						System.out.println(p + " export Stock OK");
					} catch (Exception e) {
						e.printStackTrace();
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
