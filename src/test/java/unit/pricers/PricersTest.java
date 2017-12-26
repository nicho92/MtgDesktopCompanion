package unit.pricers;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MagicPricesProvider;
import org.magic.services.MTGControler;

public class PricersTest {

	MagicEdition me;
	MagicCard card;
	
	
	@Before
	public void init()
	{
		me = new MagicEdition();
		me.setId("3ED");
		me.setSet("Revised Edition");
		card = new MagicCard();
		card.setName("Volcanic Island");
	}
	
	
	
	@Test
	public void tryCards()
	{
		for(MagicPricesProvider pricer : MTGControler.getInstance().getPricers())
		{
			try {
				assertNotEquals(null, pricer.getPrice(me, card));
			} catch (Exception e) {
				fail(pricer.getName());
			}
		}
	}
}
