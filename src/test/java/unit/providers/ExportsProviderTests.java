package unit.providers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Level;
import org.api.mkm.exceptions.MkmException;
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
import org.magic.api.exports.impl.MkmOnlineExport;
import org.magic.api.exports.impl.OCTGNDeckExport;
import org.magic.api.exports.impl.PDFExport;
import org.magic.api.exports.impl.XMageDeckExport;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class ExportsProviderTests {

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
		
		
		testExports(new Apprentice2DeckExport());
		testExports(new CocatriceDeckExport());
		testExports(new CSVExport());
		testExports(new DCIDeckSheetExport());
		testExports(new JsonExport());
		testExports(new MKMFileWantListExport());
		testExports(new MTGDesktopCompanionExport());
		testExports(new MTGODeckExport());
		testExports(new OCTGNDeckExport());
		testExports(new PDFExport());
		testExports(new XMageDeckExport());
		try {
			testExports(new MkmOnlineExport());
		} catch (MkmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public void testExports(MTGCardsExport p)
	{
		
			System.out.println("*****************************"+p.getName());
			System.out.println("EXT  "+p.getFileExtension());
			System.out.println("STAT "+p.getStatut());
			System.out.println("PROP "+p.getProperties());
			System.out.println("TYPE "+p.getType());
			System.out.println("ENAB "+p.isEnable());
			System.out.println("ICON "+p.getIcon());
			MagicDeck d = new MagicDeck();
				d.setDateCreation(new Date());
				d.setDescription("Test of " + p);
				d.setTags(Arrays.asList("test",p.getName(),"mtgdesktopcompanion"));
				d.setName("TEST " + p);
				d.getMap().put(mc, 40);
				d.getMapSideBoard().put(mc, 10);
				
			File destD = new File("target",d.getName()+" DECK "+p.getFileExtension());
			File destL = new File("target",d.getName()+" LIST "+p.getFileExtension());
			try {
				p.export(d, destD);
				System.out.println(p + " export Deck OK");
			} catch (Exception e) {
				System.out.println(p + " export Deck ERROR " +e);
			}
			try {
				p.export(d.getAsList(), destL);
				System.out.println(p + " export List OK");
			} catch (Exception e) {
				System.out.println(p + " export List ERROR "+e);
			}
			
			
			File destS = new File("target",d.getName()+" STOCK "+p.getFileExtension());
			List<MagicCardStock> stocks = new ArrayList<>();
			
			MagicCardStock s = new MagicCardStock();
							s.setMagicCard(mc);
							s.setAltered(false);
							s.setFoil(false);
							s.setSigned(false);
							s.setCondition(EnumCondition.LIGHTLY_PLAYED);
							s.setLanguage("English");
							s.setMagicCollection(new MagicCollection("TEST"));
							s.setQte(8);
							s.setPrice(9999.0);
							s.setComment("Test");
							stocks.add(s);
			try {
				p.exportStock(stocks, destS);
				System.out.println(p + " export Stock OK");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(p + " export Stock ERROR"+e);
			}
	
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
