package org.magic.services;

import static org.magic.services.tools.MTG.getPlugin;

import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Locale;

import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.tree.xpath.XPathExpressionEngine;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.MTGWallpaper;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.game.Player;
import org.magic.api.beans.technical.MTGNotification;
import org.magic.api.beans.technical.PictureDimension;
import org.magic.api.beans.technical.audit.FileAccessInfo;
import org.magic.api.beans.technical.audit.FileAccessInfo.ACCESSTYPE;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGNotifier;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.api.interfaces.abstracts.AbstractTechnicalServiceManager;
import org.magic.services.logging.MTGLogger;
import org.magic.services.network.URLTools;
import org.magic.services.providers.ApilayerCurrencyConverter;
import org.magic.services.providers.LookAndFeelProvider;
import org.magic.services.threads.ThreadManager;
import org.magic.services.threads.ThreadPoolConfig;
import org.magic.services.threads.ThreadPoolConfig.THREADPOOL;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;


public class MTGControler {

	private static final String FALSE = "false";
	private static MTGControler inst;
	private XMLConfiguration config;
	private FileBasedConfigurationBuilder<XMLConfiguration> builder;
	private LanguageService langService;
	private ApilayerCurrencyConverter currencyService;
	private LookAndFeelProvider lafService;
	private Logger logger = MTGLogger.getLogger(this.getClass());
	private File xmlConfigFile;
	private MTGNotifier notifier;
	private VersionChecker versionChecker;
	private WebShopService webshopService;
	
	private MTGControler() {
		
		System.setProperty("org.jooq.no-tips", "true");
		System.setProperty("org.jooq.no-logo", "true");
		
		xmlConfigFile = new File(MTGConstants.CONF_DIR, MTGConstants.CONF_FILENAME);
		
		if (!xmlConfigFile.exists())
			try {
				logger.info("{} file doesn't exist. creating one from default file",xmlConfigFile);
				FileTools.copyURLToFile(getClass().getResource("/data/default-conf.xml"),xmlConfigFile);
				var content = FileTools.readFile(xmlConfigFile);
				content = content.replace("$username", System.getProperty("user.name"));
				FileTools.saveFile(xmlConfigFile, content);
				
				logger.info("conf file created");
			} catch (IOException e1) {
				logger.error(e1);
			}

		if(!MTGConstants.DATA_DIR.exists())
			try {
				FileTools.forceMkdir(MTGConstants.DATA_DIR);
			} catch (IOException e1) {
				logger.error("error creating {}",MTGConstants.DATA_DIR,e1);
			}



		var params = new Parameters();
		builder = new FileBasedConfigurationBuilder<>(XMLConfiguration.class)
				.configure(params.xml()
				.setFile(xmlConfigFile)
				.setSchemaValidation(false)
				.setValidating(false)
				.setEncoding(MTGConstants.DEFAULT_ENCODING.displayName())
				.setExpressionEngine(new XPathExpressionEngine()));

		
	
		try {
			config = builder.getConfiguration();
			versionChecker = new VersionChecker(Boolean.parseBoolean(get("notifyPrerelease",FALSE)));
			
			var head ="***************"+MTGConstants.MTG_APP_NAME+ " - "  +versionChecker.getVersion() +"**********";
			var bottom =StringUtils.repeat("*", head.length());
			logger.info(head);
			logger.info("Java {}. Vendor: {}",Runtime.version(),SystemUtils.JAVA_VENDOR);
			logger.info("OS {}, Version: {}",SystemUtils.OS_NAME,SystemUtils.OS_VERSION);
			logger.info("Local directory : {}",MTGConstants.CONF_DIR);
			logger.debug("Logger File : {}",MTGLogger.getContext().getConfiguration());
			logger.info(bottom);

			
			PluginRegistry.inst().setConfig(config);
			
			langService = new LanguageService();
			langService.changeLocal(getLocale());

			ThreadManager.getInstance().initThreadPoolConfig(getThreadPoolConfig());
		
			AbstractTechnicalServiceManager.inst().enable(get("technical-log").equals("true"));
		
		} catch (Exception e) {
			logger.error("error init", e);
		}
		
		
		try {
			webshopService =  new WebShopService();
		} catch (IOException e) {
			logger.error("error init webshopconfig",  e);
		}
		

		currencyService = new ApilayerCurrencyConverter(get("currencylayer-access-api"));
		try {
			currencyService.init();
		}
		catch(Exception e)
		{
			logger.warn("error init currency services {}",e.getMessage());
			setProperty("/currencylayer-converter-enable", FALSE);

		}
	}

	
	public VersionChecker getVersionChecker() {
		return versionChecker;
	}


	public void closeApp()
	{
		AbstractTechnicalServiceManager.inst().persist();
		PluginRegistry.inst().listPlugins().forEach(MTGPlugin::unload);
		ThreadManager.getInstance().stop();
		System.exit(0);
	}



	private Font f;
	private boolean loaded = false;
	public Font getFont()
	{
		if(f!=null)
			return f;


		try {
		String family = get("/ui/font/family");
		var style = Integer.parseInt(get("/ui/font/style"));
		var size = Integer.parseInt(get("/ui/font/size"));
		f = new Font(family,style,size);
		}
		catch(Exception _)
		{
			f = MTGConstants.DEFAULT_FONT;
		}

		return f;
	}

	public void saveAccounts()
	{
		setProperty("accounts",AccountsManager.inst().exportConfig());

		logger.debug("accounts saved");
	}

	public void loadAccountsConfiguration()
	{
		AccountsManager.inst().loadConfig(get("accounts"));
	}
	
	
	public WebShopService getWebshopService() {
		return webshopService;
	}

	public void setDefaultStock(MTGCardStock st) {
		setProperty("collections/defaultStock/signed",st.isSigned());
		setProperty("collections/defaultStock/altered",st.isAltered());
		setProperty("collections/defaultStock/foil",st.isFoil());
		setProperty("collections/defaultStock/language",st.getLanguage());
		setProperty("collections/defaultStock/condition",st.getCondition().name());
		setProperty("collections/defaultStock/qty",st.getQte());
		setProperty("collections/defaultStock/etched",st.isEtched());
		setProperty("collections/defaultStock/digital",st.isDigital());
	}

	

	public MTGCardStock getDefaultStock() {
		var st = new MTGCardStock();
					   st.setSigned(Boolean.parseBoolean(get("collections/defaultStock/signed",FALSE)));
					   st.setAltered(Boolean.parseBoolean(get("collections/defaultStock/altered",FALSE)));
					   st.setFoil(Boolean.parseBoolean(get("collections/defaultStock/foil",FALSE)));
					   st.setEtched(Boolean.parseBoolean(get("collections/defaultStock/etched",FALSE)));
					   st.setDigital(Boolean.parseBoolean(get("collections/defaultStock/digital",FALSE)));
					   st.setLanguage(get("collections/defaultStock/language","English"));
					   st.setCondition(EnumCondition.valueOf(get("collections/defaultStock/condition","NEAR_MINT")));
					   st.setQte(Integer.parseInt(get("collections/defaultStock/qty","1")));
					   
					   st.setMagicCollection(new MTGCollection(get("default-library")));
		return st;
	}
	
	public MTGCardStock getDefaultStock(MTGCollection c)
	{
		var mcs = getDefaultStock();
		mcs.setMagicCollection(c);
		return mcs;
	}
	
	

	private ThreadPoolConfig getThreadPoolConfig() {
		var tpc = new ThreadPoolConfig();

		tpc.setThreadPool(THREADPOOL.valueOf(get("threadsExecutor/threadPool","FIXED")));
		tpc.setDaemon(Boolean.parseBoolean(get("threadsExecutor/daemon","true")));
		tpc.setNameFormat(get("threadsExecutor/nameFormat","mtg-threadpool-%d"));

		if(get("threadsExecutor/value","AUTO").equals("AUTO"))
			tpc.setCorePool(Runtime.getRuntime().availableProcessors());
		else
			tpc.setCorePool(Integer.parseInt(get("threadsExecutor/value","-1")));

		return tpc;
	}



	public ApilayerCurrencyConverter getCurrencyService() {
		return currencyService;
	}


	public LookAndFeelProvider getLafService() {
		if (lafService != null) {
			return lafService;
		} else {
			lafService = new LookAndFeelProvider();
			return lafService;
		}
	}

	public LanguageService getLangService() {
		if (langService != null) {
			return langService;
		} else {
			langService = new LanguageService(Locale.ENGLISH);
			return langService;
		}
	}

	public static MTGControler getInstance() {
		if (inst == null)
			inst = new MTGControler();
		return inst;
	}



	public PictureDimension getPictureProviderDimension() {
		return new PictureDimension(
				Integer.parseInt(get("/card-pictures-dimension/width")),
				Integer.parseInt(get("/card-pictures-dimension/height")),
				UITools.parseDouble(get("/card-pictures-dimension/zoom")),
				UITools.parseDouble(get("/card-pictures-dimension/x")),
				UITools.parseDouble(get("/card-pictures-dimension/y")));
	}

	public Dimension getCardsGameDimension() {
		var w = Integer.parseInt(get("/game/cards/card-width"));
		var h = Integer.parseInt(get("/game/cards/card-height"));
		return new Dimension(w, h);
	}


	public <T extends MTGPlugin> void addProperty(String path, Class<T> classname) {
		String[] k = path.split("/");

		String root = k[1];
		String elem = k[2];
		try {

			if(config.childConfigurationsAt("/"+root).isEmpty())
			{
				logger.debug("add config root: /{}",root);
				config.addProperty("/"+root,"");
			}

			config.addProperty("/" + root + " " + elem + "/class", classname.getName());
			setProperty(classname.getDeclaredConstructor().newInstance(), false);
			logger.debug("add module {} {}",path,classname.getName());

		} catch (IllegalArgumentException e ) {
			logger.error("Error inserting : {} for {}",path,classname ,e);
		}
		catch( InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e)
		{
			logger.error("Error loading : {}",classname ,e);
		}
	}

	public void setProperty(Object k, Object c) {
		try {
			var path = "";
			logger.trace("set {} to {}",k,c);

			if(k instanceof MTGPlugin){
				path = PluginRegistry.inst().getEntryFor(k).getXpath()+"[class='" + k.getClass().getName() + "']/enable";
				logger.trace(path);
			}
			else {
				path = k.toString();
			}

			config.setProperty(path, c);
			
			var info = new FileAccessInfo(xmlConfigFile);
			builder.save();
			info.setEnd(Instant.now());
			info.setAccesstype(ACCESSTYPE.WRITE);
			AbstractTechnicalServiceManager.inst().store(info);
			
		} catch (Exception e) {
			logger.error("Error saving {}={}",k,c, e);
		}
	}



	public Locale getLocale() {
		try {
			return LocaleUtils.toLocale(config.getString("locale"));
		} catch (Exception _) {
			logger.error("Could not load {}",config.getString("locale"));
			return langService.getDefault();
		}
	}



	public String get(String prop, String defaut) {
		return config.getString(prop, defaut);
	}


	public String get(String prop) {
		return get(prop, "");
	}

	public Player getProfilPlayer() {
		var p = new Player("Player");
		p.setName(config.getString("/game/player-profil/name"));

		var url = config.getString("/game/player-profil/avatar");
		try {
			if(!url.isEmpty())
				p.setAvatar(ImageTools.resize(ImageTools.read(new File(url)), 100,100));
		} catch (Exception e) {
			logger.error("error loading icon player {},{}",p,e.getMessage());
		}
		return p;
	}

	public boolean updateConfigMods() {
		return PluginRegistry.inst().updateConfigWithNewModule();
	}


	public void notify(Exception e)
	{
		logger.error("error",e);
		notify(new MTGNotification(getLangService().getCapitalize(getLangService().getError()),e));
	}


	public void notify(MTGNotification notif)
	{
		try {
			if(notifier==null)
				notifier=getPlugin(MTGConstants.DEFAULT_NOTIFIER_NAME, MTGNotifier.class);

			notifier.send(notif);
		} catch (Exception _) {
			logger.error(notif.getMessage());
		}
	}

	public void cleaning() {
		
		setLoaded(true);
		
		 if(PluginRegistry.inst().needUpdate())
		 {
			 try {
				builder.save();
				logger.info("cleaning " + MTGConstants.CONF_FILENAME +" done");
			} catch (ConfigurationException e) {
				logger.error(e);
			}
		 }
	}

	public boolean isLoaded() {
		return loaded;
	}
	
	
	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}
	

	public void init() throws SQLException {
		MTG.getEnabledPlugin(MTGCardsProvider.class).init();
		MTG.getEnabledPlugin(MTGDao.class).init();
		loadAccountsConfiguration();

	}




}
