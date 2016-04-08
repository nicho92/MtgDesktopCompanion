package org.magic.test;

import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.MagicPricesProvider;
import org.magic.api.pricers.impl.MagicCardMarketPricer;
import org.magic.api.providers.impl.MtgjsonProvider;

public class TestLang {

	public static void main(String[] args) throws Exception {
		
		MtgjsonProvider prov = new MtgjsonProvider();
		
		//System.out.println(prov.searchCardByCriteria("foreignNames", "Prêtresse"));
		MagicPricesProvider pricer = new MagicCardMarketPricer();
		
		
		List<MagicCard> lists= prov.searchCardByCriteria("name", "black lotus");
		
		pricer.getPrice(null, lists.get(0));
		
		
	}
	
}
