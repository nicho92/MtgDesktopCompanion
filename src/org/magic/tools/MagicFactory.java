package org.magic.tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.magic.api.dao.impl.HsqlDAO;
import org.magic.api.dao.impl.MysqlDAO;
import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.api.interfaces.MagicDAO;
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
	private List<MagicDAO> daoProviders;
	
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
			daoProviders=new ArrayList<MagicDAO>();
			
			pricers.add(new ChannelFireballPricer() );
			pricers.add(new EbayPricer() );
			pricers.add(new MagicTradersPricer() );
			pricers.add(new MagicCardMarketPricer());
			pricers.add(new MagicVillePricer() );
			pricers.add(new TCGPlayerPricer() );
			
			cardsProviders.add(new MtgjsonProvider());
			cardsProviders.add(new DeckbrewProvider());
			cardsProviders.add(new MtgapiProvider());
			
			//daoProviders.add(new HsqlDAO());
			daoProviders.add(new MysqlDAO());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<MagicPricesProvider> getEnabledPricers()
	{
		List<MagicPricesProvider> pricersE= new ArrayList<MagicPricesProvider>();
		
		for(MagicPricesProvider p : getSetPricers())
			if(p.isEnable())
				pricersE.add(p);
		
		return pricersE;
	}
	
	
	public List<MagicDAO> getDaoProviders() {
		return daoProviders;
	}

	public void setDaoProviders(List<MagicDAO> daoProviders) {
		this.daoProviders = daoProviders;
	}

	public Set<MagicPricesProvider> getSetPricers()
	{
		  return new HashSet<MagicPricesProvider>(pricers);
	}
	
	public List<MagicCardsProvider> getListProviders()
	{
		  return cardsProviders;
	}
	
}
