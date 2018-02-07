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
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.decksniffer.impl.TappedOutDeckSniffer;
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
import org.magic.api.interfaces.CardExporter;
import org.magic.api.interfaces.DeckSniffer;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class DeckSnifferProviderTests {

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
		test(new TappedOutDeckSniffer());
		
	}
	
	
	
	public void test(DeckSniffer p)
	{
		
			System.out.println("*****************************"+p.getName());
			System.out.println("STAT "+p.getStatut());
			System.out.println("PROP "+p.getProperties());
			System.out.println("TYPE "+p.getType());
			System.out.println("ENAB "+p.isEnable());

						
			try {
				List<RetrievableDeck> decks = p.getDeckList();
				System.out.println("Retrieve decklist OK");
				RetrievableDeck d = decks.get(0);
				MagicDeck deck = p.getDeck(d);
				System.out.println("Retrieve " + deck.getName() +" ok");
			} catch (Exception e) {
				e.printStackTrace();
			}
	
	
		
		
	}
	
	
}
