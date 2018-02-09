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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;

import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.tree.xpath.XPathExpressionEngine;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.log4j.Logger;
import org.magic.api.beans.RSSBean;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.MTGDeckSniffer;
import org.magic.api.interfaces.MTGPicturesCache;
import org.magic.api.interfaces.MTGServer;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.api.interfaces.MTGShopper;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.game.model.Player;
import org.magic.gui.MagicGUI;
import org.magic.gui.abstracts.AbstractJDashlet;

public class MTGControler {

	private static MTGControler inst;
	private List<MTGPricesProvider> pricers;
	private List<MTGCardsProvider> cardsProviders;
	private List<MTGDao> daoProviders;
	private List<MTGShopper> cardsShoppers;
	private List<MTGDeckSniffer> deckSniffers;
	private List<MTGPictureProvider> picturesProviders;
	private List<MTGDashBoard> dashboards;
	private List<MTGCardsExport> exports;
	private List<MTGServer> servers;
	private List<AbstractJDashlet> dashlets;
	private List<MTGPicturesCache> caches;
	private KeyWordManager keyWordManager;
	public static final File CONF_DIR = new File(System.getProperty("user.home")+"/.magicDeskCompanion/");
	private XMLConfiguration config;
	private ClassLoader classLoader ;
	private FileBasedConfigurationBuilder<XMLConfiguration> builder;
	private LanguageService langService;
	
	private Logger logger = MTGLogger.getLogger(this.getClass());
	
	
	public void notify(String caption,String text,MessageType type)
	{
		if(SystemTray.isSupported())
			MagicGUI.getTrayNotifier().displayMessage(caption, text, type);
	}
	
	public LanguageService getLangService() {
		if(langService!=null)
		{
			return langService;
		}
		else
		{
			langService = new LanguageService(Locale.ENGLISH);
			return langService;
		}
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
			setProperty(classname.newInstance(),false);
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	
	public void setProperty(Object k, Object c)
	{
		try {
			String path ="";
			
			if (k instanceof MTGPricesProvider) {
				path = "pricers/pricer[class='"+k.getClass().getName()+"']/enable";
			}else if (k instanceof MTGCardsProvider) {
				path = "providers/provider[class='"+k.getClass().getName()+"']/enable";
			}else if (k instanceof MTGDao) {
				path = "daos/dao[class='"+k.getClass().getName()+"']/enable";
			}else if (k instanceof MTGShopper) {
				path = "shoppers/shopper[class='"+k.getClass().getName()+"']/enable";
			}else if (k instanceof MTGDashBoard) {
				path = "dashboards/dashboard[class='"+k.getClass().getName()+"']/enable";
			}else if (k instanceof MTGCardsExport) {
				path = "deckexports/export[class='"+k.getClass().getName()+"']/enable";
			}else if (k instanceof MTGDeckSniffer) {
				path = "decksniffer/sniffer[class='"+k.getClass().getName()+"']/enable";
			}else if (k instanceof MTGPictureProvider) {
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
		InputStream input = getClass().getResourceAsStream(MTGConstants.MTG_DESKTOP_VERSION_FILE);
		BufferedReader read = new BufferedReader(new InputStreamReader(input));
		try {
			String version= read.readLine();
			
			if(version.startsWith("${"))
				return "0.0";
			else
				return version;
			
		} catch (IOException e) {
			return "";
		}
		
	}
	
	public Locale getLocale()
	{
		try {
			return LocaleUtils.toLocale(config.getString("locale"));
		}catch(Exception e)
		{
			logger.error("Could not load " + config.getString("locale"));
			return langService.getDefault();
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
	
	public void reload() throws ClassNotFoundException, SQLException
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
			MTGLogger.printStackTrace(e);
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
		        .setEncoding("UTF-8")
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
				MTGPricesProvider prov = loadItem(s);
				if(prov!=null){
					prov.enable(config.getBoolean("pricers/pricer["+i+"]/enable"));
					pricers.add(prov);
				}
			}
			
			logger.info("loading cards provider");
			cardsProviders= new ArrayList<>();

			for(int i=1;i<=config.getList("//provider/class").size();i++)
			{
				String s = config.getString("providers/provider["+i+"]/class");
				MTGCardsProvider prov = loadItem(s);
				if(prov!=null){
					prov.enable(config.getBoolean("providers/provider["+i+"]/enable"));
					cardsProviders.add(prov);
				}
			}
			
			
			logger.info("loading DAOs");
			daoProviders=new ArrayList<>();
			for(int i=1;i<=config.getList("//dao/class").size();i++)
			{
				String s = config.getString("daos/dao["+i+"]/class");
				MTGDao prov = loadItem(s);
				if(prov!=null){
					prov.enable(config.getBoolean("daos/dao["+i+"]/enable"));
					daoProviders.add(prov);
				}
			}
			
			logger.info("loading Caches");
			caches=new ArrayList<>();
			for(int i=1;i<=config.getList("//cache/class").size();i++)
			{
				String s = config.getString("caches/cache["+i+"]/class");
				MTGPicturesCache prov = loadItem(s);
						 
				if(prov!=null){
					prov.enable(config.getBoolean("caches/cache["+i+"]/enable"));
					caches.add(prov);
				}
			}
			
			
			logger.info("loading Shoppers");
			cardsShoppers=new ArrayList<>();
			for(int i=1;i<=config.getList("//shopper/class").size();i++)
			{
				String s = config.getString("shoppers/shopper["+i+"]/class");
				MTGShopper prov = loadItem(s);
				if(prov!=null){
					prov.enable(config.getBoolean("shoppers/shopper["+i+"]/enable"));
					cardsShoppers.add(prov);
				}
			}
			
			logger.info("loading DashBoard");
			dashboards=new ArrayList<>();
			for(int i=1;i<=config.getList("//dashboard/class").size();i++)
			{
				String s = config.getString("dashboards/dashboard["+i+"]/class");
				MTGDashBoard prov = loadItem(s);
				if(prov!=null){
					prov.enable(config.getBoolean("dashboards/dashboard["+i+"]/enable"));
					dashboards.add(prov);
				}
			}
			
			logger.info("loading Dashlets");
			dashlets=new ArrayList<>();
			for(int i=1;i<=config.getList("//dashlet/class").size();i++)
			{
				String s = config.getString("dashlets/dashlet["+i+"]/class");
				AbstractJDashlet prov = loadItem(s);
				dashlets.add(prov);		 
				
			}
			
			logger.info("loading Deck Exports");
			exports=new ArrayList<>();
			for(int i=1;i<=config.getList("//export/class").size();i++)
			{
				String s = config.getString("deckexports/export["+i+"]/class");
				AbstractCardExport prov = loadItem(s);
				if(prov!=null){
					prov.enable(config.getBoolean("deckexports/export["+i+"]/enable"));
					exports.add(prov);
				}
			}
			
			logger.info("loading Deck Sniffer");
			deckSniffers=new ArrayList<>();
			for(int i=1;i<=config.getList("//sniffer/class").size();i++)
			{
				String s = config.getString("decksniffer/sniffer["+i+"]/class");
				MTGDeckSniffer prov = loadItem(s);
				if(prov!=null){	
					prov.enable(config.getBoolean("decksniffer/sniffer["+i+"]/enable"));
					deckSniffers.add(prov);
				}
			}
			
			logger.info("loading Pictures provider");
			picturesProviders=new ArrayList<>();
			for(int i=1;i<=config.getList("//picture/class").size();i++)
			{
				String s = config.getString("pictures/picture["+i+"]/class");
				MTGPictureProvider prov = loadItem(s);
				if(prov!=null){
					prov.enable(config.getBoolean("pictures/picture["+i+"]/enable"));
					picturesProviders.add(prov);
				}	
			}
			
			logger.info("loading Servers");
			servers=new ArrayList<>();
			for(int i=1;i<=config.getList("//server/class").size();i++)
			{
				String s = config.getString("servers/server["+i+"]/class");
				MTGServer prov = loadItem(s);
						 
				if(prov!=null){
					prov.enable(config.getBoolean("servers/server["+i+"]/enable"));
					servers.add(prov);
				}
			}
			
			
		
			keyWordManager = new KeyWordManager();
			
			langService = new LanguageService();
			langService.changeLocal(getLocale());

			
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	public List<AbstractJDashlet> getDashlets() {
		return dashlets;
	}
	
	
	public KeyWordManager getKeyWordManager() {
		return keyWordManager;
	}


	public boolean updateConfigMods()
	{
		return new ModuleInstaller().updateConfigWithNewModule();
	}

	
	public <T> T loadItem(String classname) 
	{
		try{
			logger.debug("-load module :  " + classname );
			return (T)classLoader.loadClass(classname).newInstance();
		}
		catch(ClassNotFoundException e)
		{
			logger.error(classname + " is not found");
			remove(classname);
			return null;
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error("error loading "+ classname,e);
			return null;
		}
	}
	
	
	public void remove(String classname) {
		logger.debug("need to remove " + classname);
		
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
	
	public List<MTGCardsProvider> getListProviders()
	{
		  return cardsProviders;
	}
	
	public List<MTGDao> getDaoProviders() {
		return daoProviders;
	}
	
	public List<MTGPictureProvider> getPicturesProviders()
	{
		return picturesProviders;
	}
	
	public List<MTGPricesProvider> getPricers()
	{
		  return pricers;
	}

	public List<MTGPricesProvider> getEnabledPricers()
	{
		List<MTGPricesProvider> pricersE= new ArrayList<>();
		
		for(MTGPricesProvider p : getPricers())
			if(p.isEnable())
				pricersE.add(p);
		
		return pricersE;
	}
	
	public MTGCardsProvider getEnabledProviders()
	{
		for(MTGCardsProvider p : getListProviders())
			if(p.isEnable())
				return p;
		
		return null;
	}
	
	
	public MTGPictureProvider getEnabledPicturesProvider()
	{
		for(MTGPictureProvider p : getPicturesProviders())
			if(p.isEnable())
				return p;
		
		return null;
	}
	
	
	
	public MTGDao getEnabledDAO() {
		for(MTGDao p : getDaoProviders())
			if(p.isEnable())
				return p;
		return null;
	}
	
	public List<MTGDeckSniffer> getEnabledDeckSniffer() {
		List<MTGDeckSniffer> prov= new ArrayList<>();
		
		for(MTGDeckSniffer p : getDeckSniffers())
			if(p.isEnable())
				prov.add(p);
		
		return prov;
	}

	public List<MTGDeckSniffer> getDeckSniffers() {
		return deckSniffers;
	}

	public List<MTGShopper> getShoppers() {
		return cardsShoppers;
	}
	
	public List<MTGShopper> getEnabledShoppers() {
		List<MTGShopper> enable = new ArrayList<>();
		for(MTGShopper p : getShoppers())
			if(p.isEnable())
				enable.add(p);
		
		return enable;
	}

	public MTGDashBoard getEnabledDashBoard() {
		for(MTGDashBoard p : getDashBoards())
			if(p.isEnable())
				return p;
		
		return null;
	}
	
	
	
	

	public List<MTGDashBoard> getDashBoards() {
		return dashboards;
	}

	public List<MTGServer> getServers()
	{
		return servers;
	}
	
	public List<MTGServer> getEnabledServers() {
		List<MTGServer> enable = new ArrayList<>();
		for(MTGServer p : getServers())
			if(p.isEnable())
				enable.add(p);
		return enable;
	}
	
	public List<MTGCardsExport> getDeckExports()
	{
		return exports;
	}
	
	public List<MTGCardsExport> getEnabledDeckExports() {
		List<MTGCardsExport> enable = new ArrayList<>();
		for(MTGCardsExport p : getDeckExports())
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

	public MTGCardsExport getAbstractExporterFromExt(File f) {
		String ext = FilenameUtils.getExtension(f.getAbsolutePath());
		
		for(MTGCardsExport ace : exports)
		{
			if(ace.getFileExtension().endsWith(ext))
				return ace;
		}
		return null;
		
		
	}

}
