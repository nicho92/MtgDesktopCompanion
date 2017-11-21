package org.magic.services;

import java.awt.Dimension;
import java.awt.SystemTray;
import java.awt.TrayIcon.MessageType;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.tree.xpath.XPathExpressionEngine;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.RSSBean;
import org.magic.api.interfaces.CardExporter;
import org.magic.api.interfaces.DashBoard;
import org.magic.api.interfaces.DeckSniffer;
import org.magic.api.interfaces.MTGPicturesCache;
import org.magic.api.interfaces.MTGServer;
import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.api.interfaces.MagicDAO;
import org.magic.api.interfaces.MagicPricesProvider;
import org.magic.api.interfaces.MagicShopper;
import org.magic.api.interfaces.PictureProvider;
import org.magic.game.model.Player;
import org.magic.gui.MagicGUI;

public class MTGControler {

	private static MTGControler inst;
	private List<MagicPricesProvider> pricers;
	private List<MagicCardsProvider> cardsProviders;
	private List<MagicDAO> daoProviders;
	private List<MagicShopper> cardsShoppers;
	private List<DeckSniffer> deckSniffers;
	private List<PictureProvider> picturesProviders;
	private List<DashBoard> dashboards;
	private List<CardExporter> exports;
	private List<MTGServer> servers;
	private List<MTGPicturesCache> caches;
	
	private KeyWordManager keyWordManager;
	
	public static File CONF_DIR = new File(System.getProperty("user.home")+"/.magicDeskCompanion/");
	private XMLConfiguration config;
	private ClassLoader classLoader ;
	private FileBasedConfigurationBuilder<XMLConfiguration> builder;
	
	
	
	
	static final Logger logger = LogManager.getLogger(MTGControler.class.getName());
	
	public void notify(String caption,String text,MessageType type)
	{
		if(SystemTray.isSupported())
			MagicGUI.trayNotifier.displayMessage(caption, text, type);
	}
	
	
	public static MTGControler getInstance()
	{
		if(inst == null)
			inst = new MTGControler();
		return inst;
	}
	
	public Dimension getCardsDimension()
	{
		int w = Integer.parseInt(MTGControler.getInstance().get("/game/cards/card-width"));
		int h = Integer.parseInt(MTGControler.getInstance().get("/game/cards/card-height"));
		return new Dimension(w, h);
	}
	
	
	public void addProperty(String path, Class classname)
	{
		String[] k = path.split("/");
		
		String root = k[1];
		String elem = k[2];
		try {
			config.addProperty("/"+root +" "+ elem+"/class", classname.getName());
			logger.debug("add module " + path + " " + classname.getName());
			setProperty(classname.newInstance(),new Boolean(false));
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	
	public void setProperty(Object k, Object c)
	{
		try {
			String path ="";
			
			if (k instanceof MagicPricesProvider) {
				path = "pricers/pricer[class='"+k.getClass().getName()+"']/enable";
			}else if (k instanceof MagicCardsProvider) {
				path = "providers/provider[class='"+k.getClass().getName()+"']/enable";
			}else if (k instanceof MagicDAO) {
				path = "daos/dao[class='"+k.getClass().getName()+"']/enable";
			}else if (k instanceof MagicShopper) {
				path = "shoppers/shopper[class='"+k.getClass().getName()+"']/enable";
			}else if (k instanceof DashBoard) {
				path = "dashboards/dashboard[class='"+k.getClass().getName()+"']/enable";
			}else if (k instanceof CardExporter) {
				path = "deckexports/export[class='"+k.getClass().getName()+"']/enable";
			}else if (k instanceof DeckSniffer) {
				path = "decksniffer/sniffer[class='"+k.getClass().getName()+"']/enable";
			}else if (k instanceof PictureProvider) {
				path = "pictures/picture[class='"+k.getClass().getName()+"']/enable";
			}else if (k instanceof MTGServer) {
				path = "servers/server[class='"+k.getClass().getName()+"']/enable";
			}else if (k instanceof MTGPicturesCache) {
				path = "caches/cache[class='"+k.getClass().getName()+"']/enable";
			}else if (k instanceof RSSBean) {
				path = "rss";
			}else{
				path=k.toString();
			}
			logger.debug("set " + k + " to " + c);
			
			config.setProperty(path, c);
			builder.save();
		} catch (Exception e) {
			logger.error("Error saving " +c, e);
		}
	}
	
	public String getVersion()
	{
		InputStream input = getClass().getResourceAsStream("/res/data/version");
		BufferedReader read = new BufferedReader(new InputStreamReader(input));
		try {
			return read.readLine();
		} catch (IOException e) {
			return "";
		}
		
	}
	
	public String get(String prop,String defaut)
	{
		return config.getString(prop,defaut);
	}
	
	public String get(String prop)
	{
		return config.getString(prop,"");
	}
	
	public void reload() throws Exception
	{
		logger.debug("Reload Controler");
		inst=new MTGControler();
		inst.getEnabledProviders().init();
		inst.getEnabledDAO().init();
	}
	
	public Player getProfilPlayer()
	{
		Player p = new Player();
		p.setName(config.getString("/game/player-profil/name"));
		
		String url = config.getString("/game/player-profil/avatar");
		try{
			p.setIcon(ImageIO.read(new URL(url)));	
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
		return p;
		
	}
	
	
		
	private MTGControler()
	{
		File conf = new File(CONF_DIR,"mtgcompanion-conf.xml");
		if(!conf.exists())
		try {
			logger.info("conf file doesn't exist. creating one from default file");
			FileUtils.copyURLToFile(getClass().getResource("/default-conf.xml"), new File(CONF_DIR,"mtgcompanion-conf.xml"));
			logger.info("conf file created");
		}
		catch (IOException e1) 
		{
			logger.error(e1);
		}
		
		Parameters params = new Parameters();
		builder = new FileBasedConfigurationBuilder<XMLConfiguration>(XMLConfiguration.class)
		    	.configure(params.xml()
		        .setFile(new File(CONF_DIR,"mtgcompanion-conf.xml"))
		        .setSchemaValidation(false)
		        .setValidating(false)
		        .setEncoding("ISO-8859-15")
		        .setExpressionEngine(new XPathExpressionEngine())
		        );
		
		classLoader = MTGControler.class.getClassLoader();
		
		try {
			
		    config = builder.getConfiguration();
			logger.info("loading pricers");
			pricers=new ArrayList<>();
			
			for(int i=1;i<=config.getList("//pricer/class").size();i++)
			{
				String s = config.getString("pricers/pricer["+i+"]/class");
				MagicPricesProvider prov = loadItem(MagicPricesProvider.class, s);
				if(prov!=null){
					prov.enable(config.getBoolean("pricers/pricer["+i+"]/enable"));
					pricers.add(prov);
				}
			}
			
			logger.info("loading cards provider");
			cardsProviders= new ArrayList<MagicCardsProvider>();

			for(int i=1;i<=config.getList("//provider/class").size();i++)
			{
				String s = config.getString("providers/provider["+i+"]/class");
				MagicCardsProvider prov = loadItem(MagicCardsProvider.class, s.toString());
				if(prov!=null){
					prov.enable(config.getBoolean("providers/provider["+i+"]/enable"));
					cardsProviders.add(prov);
				}
			}
			
			
			logger.info("loading DAOs");
			daoProviders=new ArrayList<MagicDAO>();
			for(int i=1;i<=config.getList("//dao/class").size();i++)
			{
				String s = config.getString("daos/dao["+i+"]/class");
				MagicDAO prov = loadItem(MagicDAO.class, s.toString());
				if(prov!=null){
					prov.enable(config.getBoolean("daos/dao["+i+"]/enable"));
					daoProviders.add(prov);
				}
			}
			
			logger.info("loading Shoppers");
			cardsShoppers=new ArrayList<MagicShopper>();
			for(int i=1;i<=config.getList("//shopper/class").size();i++)
			{
				String s = config.getString("shoppers/shopper["+i+"]/class");
				MagicShopper prov = loadItem(MagicShopper.class, s.toString());
				if(prov!=null){
					prov.enable(config.getBoolean("shoppers/shopper["+i+"]/enable"));
					cardsShoppers.add(prov);
				}
			}
			
			logger.info("loading DashBoard");
			dashboards=new ArrayList<DashBoard>();
			for(int i=1;i<=config.getList("//dashboard/class").size();i++)
			{
				String s = config.getString("dashboards/dashboard["+i+"]/class");
				DashBoard prov = loadItem(DashBoard.class, s.toString());
				if(prov!=null){
					prov.enable(config.getBoolean("dashboards/dashboard["+i+"]/enable"));
					dashboards.add(prov);
				}
			}
			
			logger.info("loading Deck Exports");
			exports=new ArrayList<CardExporter>();
			for(int i=1;i<=config.getList("//export/class").size();i++)
			{
				String s = config.getString("deckexports/export["+i+"]/class");
				CardExporter prov = loadItem(CardExporter.class, s.toString());
				if(prov!=null){
					prov.enable(config.getBoolean("deckexports/export["+i+"]/enable"));
					exports.add(prov);
				}
			}
			
			logger.info("loading Deck Sniffer");
			deckSniffers=new ArrayList<DeckSniffer>();
			for(int i=1;i<=config.getList("//sniffer/class").size();i++)
			{
				String s = config.getString("decksniffer/sniffer["+i+"]/class");
				DeckSniffer prov = loadItem(DeckSniffer.class, s.toString());
				if(prov!=null){	
					prov.enable(config.getBoolean("decksniffer/sniffer["+i+"]/enable"));
					deckSniffers.add(prov);
				}
			}
			
			logger.info("loading Pictures provider");
			picturesProviders=new ArrayList<PictureProvider>();
			for(int i=1;i<=config.getList("//picture/class").size();i++)
			{
				String s = config.getString("pictures/picture["+i+"]/class");
				PictureProvider prov = loadItem(PictureProvider.class, s.toString());
				if(prov!=null){
					prov.enable(config.getBoolean("pictures/picture["+i+"]/enable"));
					picturesProviders.add(prov);
				}	
			}
			
			logger.info("loading Servers");
			servers=new ArrayList<MTGServer>();
			for(int i=1;i<=config.getList("//server/class").size();i++)
			{
				String s = config.getString("servers/server["+i+"]/class");
				MTGServer prov = loadItem(MTGServer.class, s.toString());
						 
				if(prov!=null){
					prov.enable(config.getBoolean("servers/server["+i+"]/enable"));
					servers.add(prov);
				}
			}
			
			logger.info("loading Caches");
			caches=new ArrayList<MTGPicturesCache>();
			for(int i=1;i<=config.getList("//cache/class").size();i++)
			{
				String s = config.getString("caches/cache["+i+"]/class");
				MTGPicturesCache prov = loadItem(MTGPicturesCache.class, s.toString());
						 
				if(prov!=null){
					prov.enable(config.getBoolean("caches/cache["+i+"]/enable"));
					caches.add(prov);
				}
			}
			
			//logger.debug("Check for new modules");
			keyWordManager = new KeyWordManager();
			
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}
	
	
	
	
	public KeyWordManager getKeyWordManager() {
		return keyWordManager;
	}


	public boolean updateConfigMods() throws ClassNotFoundException, IOException
	{
		return new ModuleInstaller().updateConfigWithNewModule();
	}

	
	public <T> T loadItem(Class <T> cls, String classname) 
	{
		try{
		logger.debug("-load module :  " + classname );
			return (T)classLoader.loadClass(classname).newInstance();
		}catch(Exception e)
		{
			logger.error(e);
			return null;
		}
	}
	
	
	public MTGPicturesCache getEnabledCache()
	{
		for(MTGPicturesCache p : getListCaches())
			if(p.isEnable())
				return p;
		
		return null;
	}
	
	public List<MTGPicturesCache> getListCaches()
	{
		  return caches;
	}
	
	public List<MagicCardsProvider> getListProviders()
	{
		  return cardsProviders;
	}
	
	public List<MagicDAO> getDaoProviders() {
		return daoProviders;
	}
	
	public List<PictureProvider> getPicturesProviders()
	{
		return picturesProviders;
	}
	
	public List<MagicPricesProvider> getPricers()
	{
		  return pricers;
	}

	public List<MagicPricesProvider> getEnabledPricers()
	{
		List<MagicPricesProvider> pricersE= new ArrayList<MagicPricesProvider>();
		
		for(MagicPricesProvider p : getPricers())
			if(p.isEnable())
				pricersE.add(p);
		
		return pricersE;
	}
	
	public MagicCardsProvider getEnabledProviders()
	{
		for(MagicCardsProvider p : getListProviders())
			if(p.isEnable())
				return p;
		
		return null;
	}
	
	
	public PictureProvider getEnabledPicturesProvider()
	{
		for(PictureProvider p : getPicturesProviders())
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
	
	public List<DeckSniffer> getEnabledDeckSniffer() {
		List<DeckSniffer> prov= new ArrayList<DeckSniffer>();
		
		for(DeckSniffer p : getDeckSniffers())
			if(p.isEnable())
				prov.add(p);
		
		return prov;
	}

	public List<DeckSniffer> getDeckSniffers() {
		return deckSniffers;
	}

	public List<MagicShopper> getShoppers() {
		return cardsShoppers;
	}
	
	public List<MagicShopper> getEnabledShoppers() {
		List<MagicShopper> enable = new ArrayList<MagicShopper>();
		for(MagicShopper p : getShoppers())
			if(p.isEnable())
				enable.add(p);
		
		return enable;
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

	public List<MTGServer> getServers()
	{
		return servers;
	}
	
	public List<MTGServer> getEnabledServers() {
		List<MTGServer> enable = new ArrayList<MTGServer>();
		for(MTGServer p : getServers())
			if(p.isEnable())
				enable.add(p);
		return enable;
	}
	
	public List<CardExporter> getDeckExports()
	{
		return exports;
	}
	
	public List<CardExporter> getEnabledDeckExports() {
		List<CardExporter> enable = new ArrayList<CardExporter>();
		for(CardExporter p : getDeckExports())
			if(p.isEnable())
				enable.add(p);
		
		return enable;
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
	
	public boolean isRunning(MTGServer server)
	{
		for(MTGServer serv : getEnabledServers())
			if(serv.getName().equals(server.getName()))
				return serv.isAlive();
				
		
		return false;
	}


	public void saveConfig(File f) {
		
		//TODO : export config
	}
}
