package test.magic.api.beans;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.junit.Before;
import org.junit.Test;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.services.logging.MTGLogger;

import test.TestTools;

public class MagicDeckTests {

	private List<MTGCard> lMC;

	@Before
	public void initTest() throws IOException, URISyntaxException
	{
		TestTools.initTest();
		MTGLogger.changeLevel(Level.DEBUG);
		lMC = TestTools.loadData();
	}
	
	
	

  @Test
  public void mergeDeckTest()
  {
    MTGDeck d = MTGDeck.toDeck(lMC);
		System.out.println(d );
    MTGDeck md = d.getMergedDeck();
		System.out.println(md );
    
  }
}
