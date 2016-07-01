package org.magic.services;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.tree.xpath.XPathExpressionEngine;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.RSSBean;
import org.magic.api.interfaces.DashBoard;
import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.api.interfaces.MagicDAO;
import org.magic.api.interfaces.MagicPricesProvider;
import org.magic.api.interfaces.MagicShopper;

public class MagicFactory {

	private static MagicFactory inst;
	private List<MagicPricesProvider> pricers;
	private List<MagicCardsProvider> cardsProviders;
	private List<MagicDAO> daoProviders;
	private List<MagicShopper> cardsShoppers;
	private File confdir = new File(System.getProperty("user.home")+"/magicDeskCompanion/");
	private XMLConfiguration config;
	private ClassLoader classLoader ;
	private FileBasedConfigurationBuilder<XMLConfiguration> builder;
	private List<DashBoard> dashboards;
	
	static final Logger logger = LogManager.getLogger(MagicFactory.class.getName());
	
	public static MagicFactory getInstance()
	{
		if(inst == null)
			inst = new MagicFactory();
		
		return inst;
	}
	
	public void setProperty(Object k, Object c)
	{
		try {
			String path ="";
			
			if (k instanceof MagicPricesProvider) {
				path = "pricers/pricer[class='"+k.getClass().getName()+"']/enable";
			}
			else if (k instanceof MagicCardsProvider) {
				path = "providers/provider[class='"+k.getClass().getName()+"']/enable";
			}else if (k instanceof MagicDAO) {
				path = "daos/dao[class='"+k.getClass().getName()+"']/enable";
			}else if (k instanceof MagicShopper) {
				path = "shoppers/shopper[class='"+k.getClass().getName()+"']/enable";
			
			}else if (k instanceof DashBoard) {
				path = "dashboards/dashboard[class='"+k.getClass().getName()+"']/enable";
			}
			else if (k instanceof RSSBean) {
				path = "rss";
			}
			else{
				path=k.toString();
			}
			logger.info("set " + k + " to " + c);
			
			config.setProperty(path, c);
			builder.save();
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	public String get(String prop)
	{
		return config.getString(prop);
	}
	
		
	private MagicFactory()
	{
		File conf = new File(confdir,"mtgcompanion-conf.xml");
		if(!conf.exists())
		try {
			logger.info("conf file doesn't exist. creating one from default file");
			FileUtils.copyURLToFile(getClass().getResource("/default-conf.xml"), new File(confdir,"mtgcompanion-conf.xml"));
			logger.info("conf file created");
		}
		catch (IOException e1) 
		{
			logger.error(e1);
		}
		
		Parameters params = new Parameters();
		builder = new FileBasedConfigurationBuilder<XMLConfiguration>(XMLConfiguration.class)
		    	.configure(params.xml()
		        .setFile(new File(confdir,"mtgcompanion-conf.xml"))
		        .setSchemaValidation(false)
		        .setValidating(false)
		        .setEncoding("ISO-8859-15")
		        .setExpressionEngine(new XPathExpressionEngine())
		        );
		
		classLoader = MagicFactory.class.getClassLoader();
		
		try {
			
		    config = builder.getConfiguration();
			logger.info("loading pricers");
			pricers=new ArrayList<>();
			
			for(int i=1;i<=config.getList("//pricer/class").size();i++)
			{
				String s = config.getString("pricers/pricer["+i+"]/class");
				MagicPricesProvider prov = loadItem(MagicPricesProvider.class, s);
				prov.enable(config.getBoolean("pricers/pricer["+i+"]/enable"));
				pricers.add(prov);
			}
			
			logger.info("loading cards provider");
			cardsProviders= new ArrayList<MagicCardsProvider>();

			for(int i=1;i<=config.getList("//provider/class").size();i++)
			{
				String s = config.getString("providers/provider["+i+"]/class");
				MagicCardsProvider prov = loadItem(MagicCardsProvider.class, s.toString());
								   prov.enable(config.getBoolean("providers/provider["+i+"]/enable"));
				cardsProviders.add(prov);
			}
			
			
			logger.info("loading DAOs");
			daoProviders=new ArrayList<>();
			for(int i=1;i<=config.getList("//dao/class").size();i++)
			{
				String s = config.getString("daos/dao["+i+"]/class");
				MagicDAO prov = loadItem(MagicDAO.class, s.toString());
						 prov.enable(config.getBoolean("daos/dao["+i+"]/enable"));
				daoProviders.add(prov);
			}
			
			logger.info("loading Shoppers");
			cardsShoppers=new ArrayList<>();
			for(int i=1;i<=config.getList("//shopper/class").size();i++)
			{
				String s = config.getString("shoppers/shopper["+i+"]/class");
				MagicShopper prov = loadItem(MagicShopper.class, s.toString());
						 prov.enable(config.getBoolean("shoppers/shopper["+i+"]/enable"));
				cardsShoppers.add(prov);
			}
			
			logger.info("loading DashBoard");
			dashboards=new ArrayList<DashBoard>();
			for(int i=1;i<=config.getList("//dashboard/class").size();i++)
			{
				String s = config.getString("dashboards/dashboard["+i+"]/class");
				DashBoard prov = loadItem(DashBoard.class, s.toString());
						 prov.enable(config.getBoolean("dashboards/dashboard["+i+"]/enable"));
				dashboards.add(prov);
			}
			
		} catch (Exception e) {
		logger.error(e);
		}
		
	}

	
	public <T> T loadItem(Class <T> cls, String classname) throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		logger.debug("-load module :  " + classname );
		return (T)classLoader.loadClass(classname).newInstance();
	}
	
	public List<MagicCardsProvider> getListProviders()
	{
		  return cardsProviders;
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
	
	public MagicCardsProvider getEnabledProviders()
	{
		List<MagicCardsProvider> prov= new ArrayList<MagicCardsProvider>();
		
		for(MagicCardsProvider p : getListProviders())
			if(p.isEnable())
				return p;
		
		return null;
	}
	
	
	public MagicDAO getEnabledDAO() {
		for(MagicDAO p : getDaoProviders())
			if(p.isEnable())
				return p;
		
		return null;
	}

	public List<MagicShopper> getShoppers() {
		return cardsShoppers;
	}

	public DashBoard getEnabledDashBoard() {
		for(DashBoard p : getDashBoards())
			if(p.isEnable())
				return p;
		
		return null;
	}

	public List<DashBoard> getDashBoards() {
		return dashboards;
	}

	public List<RSSBean> getRss() {
		List<RSSBean> list = new ArrayList<>();
		
		for(int i=1;i<=config.getList("//flux/url").size();i++)
		{
			RSSBean r = new RSSBean();
					r.setName(config.getString("rss/flux["+i+"]/name"));
					r.setCategorie(config.getString("rss/flux["+i+"]/category"));
			try {
				r.setUrl(new URL(config.getString("rss/flux["+i+"]/url")));
			} catch (MalformedURLException e) {
				logger.error(e);
			}
			list.add(r);
		}
		return list;
	}

	
}
