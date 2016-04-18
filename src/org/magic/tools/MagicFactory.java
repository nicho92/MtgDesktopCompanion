package org.magic.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
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
	
	private File confdir = new File(System.getProperty("user.home")+"/magicDeskCompanion/");
	private Properties props;

	
	
	public static MagicFactory getInstance()
	{
		if(inst == null)
			inst = new MagicFactory();
		
		return inst;
	}
	
	private MagicFactory()
	{
		try {
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
			
			daoProviders.add(new HsqlDAO());
			daoProviders.add(new MysqlDAO());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		props=new Properties();
		try {
			File f = new File(confdir, "mtgcompanion.conf");
			
			if(f.exists())
			{	
				FileInputStream fis = new FileInputStream(f);
				props.load(fis);
				fis.close();
			}
			else
			{
				
				//default;
				
				save();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}
	
	public void save()
	{
		try {
			File f = new File(confdir, "mtgcompanion.conf");
		
			FileOutputStream fos = new FileOutputStream(f);
			props.store(fos,"");
			fos.close();
		} catch (Exception e) {
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

	public MagicDAO getEnabledDAO() {
		return daoProviders.get(0);
	}
	
}
