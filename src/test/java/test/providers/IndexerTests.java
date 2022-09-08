package test.providers;

import org.apache.logging.log4j.Level;
import org.junit.Before;
import org.junit.Test;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.MTGBorder;
import org.magic.api.beans.enums.MTGLayout;
import org.magic.api.beans.enums.MTGRarity;
import org.magic.api.indexer.impl.LuceneIndexer;
import org.magic.api.interfaces.MTGCardsIndexer;
import org.magic.services.logging.MTGLogger;

import com.google.common.collect.Lists;

public class IndexerTests {

	MagicCard mc;
	MagicEdition ed;
	
	@Before
	public void createCards()
	{
		
		MTGLogger.changeLevel(Level.TRACE);
		
		mc = new MagicCard();
		mc.setName("Black Lotus");
		mc.setLayout(MTGLayout.NORMAL);
		mc.setCost("{0}");
		mc.setCmc(0);
		mc.getTypes().add("Artifact");
		mc.setReserved(true);
		mc.setText("{T}, Sacrifice Black Lotus: Add three mana of any one color to your mana pool.");
		mc.setRarity(MTGRarity.RARE);
		mc.setBorder(MTGBorder.BLACK);
		mc.setArtist("Christopher Rush");
					 ed = new MagicEdition();
					 ed.setId("lea");
					 ed.setSet("Limited Edition Alpha");
					 ed.setMultiverseid("3");
		mc.getEditions().add(ed);
		mc.getCurrentSet().setNumber("232");
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
				System.out.println(Lists.asList(String.class,p.listFields()));
				
				System.out.println("DATE:" + p.getIndexDate());
				p.search(mc.getName()).forEach(c->{
					System.out.println(c.getName());
				});
				
				p.terms("name").entrySet().forEach(r->System.out.println(r.getKey() +" " + r.getValue()));
				
				p.similarity(mc).entrySet().forEach(entry->{
					System.out.println(entry.getValue() + "\t" + entry.getKey());
				});
				
				System.out.println(p.suggestCardName("emrakul"));
				
			} catch (Exception e) {
				System.out.println("ERROR " + e);
			}
					
	}
	
	
}
