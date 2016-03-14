package org.magic.tools;

import java.util.ArrayList;
import java.util.List;

import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.api.interfaces.MagicPricesProvider;
import org.magic.api.pricers.impl.ChannelFireballPricer;
import org.magic.api.pricers.impl.EbayPricer;
import org.magic.api.pricers.impl.MagicCardMarketPricer;
import org.magic.api.pricers.impl.MagicTradersPricer;
import org.magic.api.pricers.impl.MagicVillePricer;
import org.magic.api.pricers.impl.TCGPlayerPricer;
import org.magic.api.providers.impl.DeckbrewProvider;
import org.magic.api.providers.impl.MtgapiProvider;
import org.magic.api.providers.impl.MtgjsonProvider;

public class MagicFactory {

	private static MagicFactory inst;
	private List<MagicPricesProvider> pricers;
	private List<MagicCardsProvider> cardsProviders;
	
	
	public static MagicFactory getInstance()
	{
		if(inst == null)
			inst = new MagicFactory();
		
		return inst;
	}
	
	private MagicFactory()
	{
		try {
			//init("org.magic.api.pricers.impl");
			pricers = new ArrayList<MagicPricesProvider>();
			cardsProviders = new ArrayList<MagicCardsProvider>();
			
			
			pricers.add(new ChannelFireballPricer() );
			pricers.add(new EbayPricer() );
			pricers.add(new MagicTradersPricer() );
			pricers.add(new MagicVillePricer() );
			pricers.add(new TCGPlayerPricer() );
			pricers.add(new MagicCardMarketPricer());
			cardsProviders.add(new MtgjsonProvider());
			cardsProviders.add(new DeckbrewProvider());
			cardsProviders.add(new MtgapiProvider());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public List<MagicPricesProvider> getListPricers()
	{
		  return pricers;
	}
	
	public List<MagicCardsProvider> getListProviders()
	{
		  return cardsProviders;
	}
	
}
