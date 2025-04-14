package test.providers;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.technical.RetrievableDeck;
import org.magic.api.interfaces.MTGDeckSniffer;
import org.magic.services.PluginRegistry;

import test.TestTools;

public class DeckSnifferProviderTests {
	
	@Before
	public void initTest() 
	{
		TestTools.initTest();
	}
	
	@Test
	public void launch()
	{
		PluginRegistry.inst().listPlugins(MTGDeckSniffer.class).forEach(this::testPlugin);
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
				List<RetrievableDeck> decks = p.getDeckList(p.listFilter()[0],null);
				System.out.println("Retrieve decklist OK");
				RetrievableDeck d = decks.get(0);
				MTGDeck deck = p.getDeck(d);
				System.out.println("Retrieve " + deck.getName() +" ok");
			} catch (Exception e) {
				e.printStackTrace();
			}
	
	
		
		
	}
	
	
}
