package test.providers;

import java.util.List;

import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.decksniffer.impl.DeckstatsDeckSniffer;
import org.magic.api.decksniffer.impl.LotusNoirDecks;
import org.magic.api.decksniffer.impl.MTGDecksSniffer;
import org.magic.api.decksniffer.impl.MTGSalvationDeckSniffer;
import org.magic.api.decksniffer.impl.MTGTop8DeckSniffer;
import org.magic.api.decksniffer.impl.MTGoldFishDeck;
import org.magic.api.decksniffer.impl.MagicCorporationDecks;
import org.magic.api.decksniffer.impl.TCGPlayerDeckSniffer;
import org.magic.api.decksniffer.impl.TappedOutDeckSniffer;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDeckSniffer;
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
					 ed = new MagicEdition();
					 ed.setId("LEA");
					 ed.setSet("Limited Edition Alpha");
					 ed.setBorder("Black");
					 ed.setRarity("Rare");
					 ed.setArtist("Christopher Rush");
					 ed.setMultiverseid("3");
					 ed.setNumber("232");
					 ed.setMkmid(1);
					 ed.setMkmName("Alpha");
		mc.getEditions().add(ed);
	}
	
	@Test
	public void initTests()
	{
		
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).init();
		test(new DeckstatsDeckSniffer());
		test(new LotusNoirDecks());
		test(new MagicCorporationDecks());
		test(new MTGoldFishDeck());
		test(new MTGSalvationDeckSniffer());
		test(new MTGTop8DeckSniffer());
		test(new TCGPlayerDeckSniffer());
		test(new MTGDecksSniffer());
		test(new TappedOutDeckSniffer());
		
		
	}
	
	
	
	public void test(MTGDeckSniffer p)
	{
		
			System.out.println("*****************************"+p.getName());
			System.out.println("STAT "+p.getStatut());
			System.out.println("PROP "+p.getProperties());
			System.out.println("TYPE "+p.getType());
			System.out.println("ENAB "+p.isEnable());
			System.out.println("FILT "+p.listFilter());
			System.out.println("VERS "+p.getVersion());
						
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
