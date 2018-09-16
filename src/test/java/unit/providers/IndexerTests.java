package unit.providers;

import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.indexer.impl.LuceneIndexer;
import org.magic.api.interfaces.MTGCardsIndexer;
import org.magic.services.MTGLogger;

public class IndexerTests {

	MagicCard mc;
	MagicEdition ed;
	
	@Before
	public void createCards()
	{
		
		MTGLogger.changeLevel(Level.TRACE);
		
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
		mc.setNumber("232");
		mc.setMciNumber("232");
					 ed = new MagicEdition();
					 ed.setId("lea");
					 ed.setSet("Limited Edition Alpha");
					 ed.setBorder("Black");
					 ed.setRarity("Rare");
					 ed.setArtist("Christopher Rush");
					 ed.setMultiverseid("3");
					 ed.setNumber(mc.getNumber());
		
		mc.getEditions().add(ed);
	}
	
	@Test
	public void initTests()
	{
		testProviders(new LuceneIndexer());
	}
	
	
	
	public void testProviders(MTGCardsIndexer p)
	{
			try {
				
				System.out.println("####### TESTING "+ p.getName());
				System.out.println(p.getProperties());
				System.out.println(p.listFields());
				
				p.search(mc.getName()).forEach(c->{
					System.out.println(c.getName());
				});
				
				p.terms("name").entrySet().forEach(r->System.out.println(r.getKey() +" " + r.getValue()));
				
				p.similarity(mc).entrySet().forEach(entry->{
					System.out.println(entry.getValue() + "\t" + entry.getKey());
				});
			} catch (Exception e) {
				System.out.println("LOAD EDITION :ERROR " + e);
			}
					
	}
	
	
}
