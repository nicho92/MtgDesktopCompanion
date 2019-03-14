package test.providers;

import java.io.IOException;
import java.net.URISyntaxException;
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
import org.magic.services.PluginRegistry;

import test.TestTools;

public class DeckSnifferProviderTests {
	
	@Before
	public void initTest() throws IOException, URISyntaxException
	{
		TestTools.initTest();
	}
	
	@Test
	public void launch()
	{
		PluginRegistry.inst().listPlugins(MTGDeckSniffer.class).forEach(p->{
			testPlugin(p);	
		});
	}
	
	public void testPlugin(MTGDeckSniffer p)
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
		System.out.println("FILTERS" + p.listFilter());

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
