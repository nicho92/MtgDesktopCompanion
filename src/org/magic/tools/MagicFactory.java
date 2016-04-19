package org.magic.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.api.interfaces.MagicDAO;
import org.magic.api.interfaces.MagicPricesProvider;

public class MagicFactory {

	private static MagicFactory inst;
	private List<MagicPricesProvider> pricers;
	private List<MagicCardsProvider> cardsProviders;
	private List<MagicDAO> daoProviders;
	
	private File confdir = new File(System.getProperty("user.home")+"/magicDeskCompanion/");
	XMLConfiguration config;
	private ClassLoader classLoader ;
	static final Logger logger = LogManager.getLogger(MagicFactory.class.getName());


	public static MagicFactory getInstance()
	{
		if(inst == null)
			inst = new MagicFactory();
		
		return inst;
	}
	
	
	public XMLConfiguration getConfig()
	{
		return config;
	}
	
	
	private MagicFactory()
	{
		File conf = new File(confdir,"mtgcompanion-conf.xml");
		if(!conf.exists())
			try {
				logger.info("conf file doesn't exist. creating one from default file");
				FileUtils.copyURLToFile(getClass().getResource("/default-conf.xml"), new File(confdir,"mtgcompanion-conf.xml"));
				logger.info("conf file created");
			} catch (IOException e1) {
				logger.error(e1);
			}
		
		Parameters params = new Parameters();
		FileBasedConfigurationBuilder<XMLConfiguration> builder = new FileBasedConfigurationBuilder<XMLConfiguration>(XMLConfiguration.class)
		    	.configure(params.xml()
		        .setFile(new File(confdir,"mtgcompanion-conf.xml")));
		    
		classLoader = MagicFactory.class.getClassLoader();
		
		try {
			
		    config = builder.getConfiguration();
			
			
			logger.info("loading pricers");
			pricers=new ArrayList<>();
			
			for(int i=0;i<config.getList("pricers.pricer.name").size();i++)
			{
				String s = config.getString("pricers.pricer("+i+").name");
				MagicPricesProvider prov = loadItem(MagicPricesProvider.class, s.toString());
				prov.enable(config.getBoolean("pricers.pricer("+i+").enable"));
				pricers.add(prov);
			}
			
			
			logger.info("loading cards provider");
			cardsProviders= new ArrayList<MagicCardsProvider>();

			for(int i=0;i<config.getList("providers.provider.name").size();i++)
			{
				String s = config.getString("providers.provider("+i+").name");
				MagicCardsProvider prov = loadItem(MagicCardsProvider.class, s.toString());
				prov.enable(config.getBoolean("providers.provider("+i+").enable"));
				cardsProviders.add(prov);
			}
			
			
			logger.info("loading DAOs");
			daoProviders=new ArrayList<>();
			for(int i=0;i<config.getList("daos.dao.name").size();i++)
			{
				String s = config.getString("daos.dao("+i+").name");
				MagicDAO prov = loadItem(MagicDAO.class, s.toString());
						 prov.enable(config.getBoolean("daos.dao("+i+").enable"));
				daoProviders.add(prov);
			}
			
			
		} catch (Exception e) {
			logger.error(e);
		}
		
	}

	
	public <T> T loadItem(Class <T> cls, String classname) throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		logger.debug("loading " + classname );
		return (T)classLoader.loadClass(classname).newInstance();
	}
	
	public MagicDAO getEnabledDAO() {
		return daoProviders.get(0);
	}
	
	public List<MagicDAO> getDaoProviders() {
		return daoProviders;
	}
	
	public Set<MagicPricesProvider> getSetPricers()
	{
		  return new HashSet<MagicPricesProvider>(pricers);
	}

	public List<MagicPricesProvider> getEnabledPricers()
	{
		List<MagicPricesProvider> pricersE= new ArrayList<MagicPricesProvider>();
		
		for(MagicPricesProvider p : getSetPricers())
			if(p.isEnable())
				pricersE.add(p);
		
		return pricersE;
	}
	
	public List<MagicCardsProvider> getListProviders()
	{
		  return cardsProviders;
	}
	

	
}
