package org.magic.tools;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import org.magic.api.interfaces.MagicPricesProvider;
import org.magic.api.pricers.impl.ChannelFireballPricer;
import org.magic.api.pricers.impl.EbayPricer;
import org.magic.api.pricers.impl.MagicTradersPricer;
import org.magic.api.pricers.impl.MagicVillePricer;
import org.magic.api.pricers.impl.TCGPlayerPricer;

public class MagicPricerFactory {

	private static MagicPricerFactory inst;
	private ArrayList<MagicPricesProvider> classes;
	
	public static MagicPricerFactory getInstance()
	{
		if(inst == null)
			inst = new MagicPricerFactory();
		
		return inst;
	}
	
	private MagicPricerFactory()
	{
		try {
			//init("org.magic.api.pricers.impl");
			classes = new ArrayList<MagicPricesProvider>();
			classes.add(new ChannelFireballPricer() );
			classes.add(new EbayPricer() );
			classes.add(new MagicTradersPricer() );
			classes.add(new MagicVillePricer() );
			classes.add(new TCGPlayerPricer() );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public List<MagicPricesProvider> getListPricers()
	{
		  return classes;
	}
	
	
	
}
