package org.magic.services;

import static org.magic.services.tools.MTG.getPlugin;

import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Locale;

import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.tree.xpath.XPathExpressionEngine;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.MTGWallpaper;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.game.Player;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.technical.MTGNotification;
import org.magic.api.beans.technical.WebShopConfig;
import org.magic.api.beans.technical.audit.FileAccessInfo;
import org.magic.api.beans.technical.audit.FileAccessInfo.ACCESSTYPE;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGNotifier;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.api.interfaces.abstracts.AbstractTechnicalServiceManager;
import org.magic.services.logging.MTGLogger;
import org.magic.services.providers.ApilayerCurrencyConverter;
import org.magic.services.providers.LookAndFeelProvider;
import org.magic.services.threads.ThreadManager;
import org.magic.services.threads.ThreadPoolConfig;
import org.magic.services.threads.ThreadPoolConfig.THREADPOOL;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.MTG;


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

	
	private MTGControler() {
		
		System.setProperty("org.jooq.no-tips", "true");
		System.setProperty("org.jooq.no-logo", "true");
		
		xmlConfigFile = new File(MTGConstants.CONF_DIR, MTGConstants.CONF_FILENAME);

		if (!xmlConfigFile.exists())
			try {
				logger.info("{} file doesn't exist. creating one from default file",xmlConfigFile);
				FileTools.copyURLToFile(getClass().getResource("/data/default-conf.xml"),xmlConfigFile);
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
			logger.info(bottom);

			
			PluginRegistry.inst().setConfig(config);
			
			langService = new LanguageService();
			langService.changeLocal(getLocale());

			ThreadManager.getInstance().initThreadPoolConfig(getThreadPoolConfig());
			
		
			AbstractTechnicalServiceManager.inst().enable(get("technical-log").equals("true"));
			AbstractTechnicalServiceManager.inst().restore();
			
		
		} catch (Exception e) {
			logger.error("error init", e);
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
		PluginRegistry.inst().listPlugins().forEach(MTGPlugin::unload);
		ThreadManager.getInstance().stop();
		AbstractTechnicalServiceManager.inst().persist();
		System.exit(0);
	}



	private Font f;
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
		catch(Exception e)
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

	public void setDefaultStock(MTGCardStock st) {
		setProperty("collections/defaultStock/signed",st.isSigned());
		setProperty("collections/defaultStock/altered",st.isAltered());
		setProperty("collections/defaultStock/foil",st.isFoil());
		setProperty("collections/defaultStock/language",st.getLanguage());
		setProperty("collections/defaultStock/condition",st.getCondition().name());
		setProperty("collections/defaultStock/qty",st.getQte());
		setProperty("collections/defaultStock/etched",st.isEtched());
	}

	public WebShopConfig getWebConfig()
	{
		var conf = new WebShopConfig();
			conf.setAboutText(get("/shopSite/config/aboutText",""));
			conf.setBannerText(get("/shopSite/config/bannerText",""));
			conf.setBannerTitle(get("/shopSite/config/bannerTitle",""));
			conf.setSiteTitle(get("/shopSite/config/siteTitle",""));
			conf.setCurrency(getCurrencyService().getCurrentCurrency());
			conf.setMaxLastProduct(Integer.parseInt(get("/shopSite/config/maxLastProductSlide","4")));
			conf.setProductPagination(Integer.parseInt(get("/shopSite/config/productPaginationSlide","12")));
			conf.setPercentReduction(Double.parseDouble(get("/shopSite/config/percentReduction","0")));
			conf.setGoogleAnalyticsId(get("/shopSite/config/ganalyticsId",""));
			conf.setEnableGed(Boolean.parseBoolean(get("/shopSite/config/enableGed",FALSE)));
			conf.setExtraCss(get("/shopSite/config/extracss",""));

			conf.setPaypalClientId(get("/shopSite/payments/paypalclientId",""));
			try {
				conf.setPaypalSendMoneyUri(new URI(get("/shopSite/payments/paypalSendMoneyUri","")));
			} catch (URISyntaxException e1) {
				logger.error(e1);
				conf.setPaypalSendMoneyUri(null);
			}

			conf.setBic(get("/shopSite/payments/banqAccount/bic",""));
			conf.setIban(get("/shopSite/payments/banqAccount/iban",""));
			conf.setAverageDeliveryTime(Integer.parseInt(get("/shopSite/delivery/deliveryDay","2")));
			conf.setShippingRules(get("/shopSite/delivery/shippingRules",MTGConstants.DEFAULT_SHIPPING_RULES));
			conf.setAutomaticValidation(get("/shopSite/config/autoValidation",FALSE).equalsIgnoreCase("true"));
			conf.setAutomaticProduct(get("/shopSite/config/products/autoSelection",FALSE).equalsIgnoreCase("true"));
			conf.setWebsiteUrl(get("/shopSite/config/websiteUrl","http://localhost"));
			conf.setSealedEnabled(get("/shopSite/config/sealedEnabled",FALSE).equalsIgnoreCase("true"));
			try {

				if(conf.isAutomaticProduct())
					conf.setTopProduct(TransactionService.getBestProduct());
				else
					conf.setTopProduct(new JsonExport().fromJson(get("/shopSite/config/products/top",""), MTGCardStock.class));
			}
			catch(Exception e)
			{
				//do nothing
			}

			for(String s : get("/shopSite/config/collections","").split(";"))
			{
				if(!s.isEmpty())
					conf.getCollections().add(new MTGCollection(s));
			}

			for(String s : get("/shopSite/config/needCollections","").split(";"))
			{
				if(!s.isEmpty())
					conf.getNeedcollections().add(new MTGCollection(s));
			}


			for(String s : get("/shopSite/config/slides","").split(";"))
		       conf.getSlidesLinksImage().add(s);


			var id = get("/shopSite/config/contact","");

			Contact contact = new Contact();
			try {
				contact = MTG.getEnabledPlugin(MTGDao.class).getContactById(Integer.parseInt(id));
			} catch (NumberFormatException | SQLException e) {
				logger.error("No contact found with id = {}",id);
			}



			conf.setContact(contact);

		return conf;
	}


	public void saveWebConfig(WebShopConfig wsc) {

		setProperty("/shopSite/config/siteTitle",wsc.getSiteTitle());
		setProperty("/shopSite/config/bannerTitle",wsc.getBannerTitle());
		setProperty("/shopSite/config/bannerText",wsc.getBannerText());
		setProperty("/shopSite/config/aboutText",wsc.getAboutText());
		setProperty("/shopSite/config/websiteUrl",wsc.getWebsiteUrl());
		setProperty("/shopSite/config/enableGed",wsc.isEnableGed());
		setProperty("/shopSite/config/extracss",wsc.getExtraCss());


		setProperty("/shopSite/config/slides",StringUtils.join(wsc.getSlidesLinksImage(),";"));
		setProperty("/shopSite/config/products/top",new JsonExport().toJsonElement(wsc.getTopProduct()));
		setProperty("/shopSite/config/products/autoSelection",wsc.isAutomaticProduct());
		setProperty("/shopSite/config/maxLastProductSlide",wsc.getMaxLastProduct());
		setProperty("/shopSite/config/productPaginationSlide",wsc.getProductPagination());


		setProperty("/shopSite/config/autoValidation",wsc.isAutomaticValidation());
		setProperty("/shopSite/config/needCollections",StringUtils.join(wsc.getNeedcollections(),";"));
		setProperty("/shopSite/config/sealedEnabled",wsc.isSealedEnabled());
		setProperty("/shopSite/config/ganalyticsId",wsc.getGoogleAnalyticsId());
		setProperty("/shopSite/config/percentReduction",wsc.getPercentReduction());
		setProperty("/shopSite/config/collections",StringUtils.join(wsc.getCollections(),";"));
		setProperty("/shopSite/config/contact",wsc.getContact().getId());
		setProperty("/shopSite/delivery/shippingRules", wsc.getShippingRules());
		setProperty("/shopSite/delivery/deliveryDay",wsc.getAverageDeliveryTime());

		setProperty("/shopSite/payments/banqAccount/bic",wsc.getBic());
		setProperty("/shopSite/payments/banqAccount/iban",wsc.getIban());
		setProperty("/shopSite/payments/paypalclientId",wsc.getPaypalClientId());
		setProperty("/shopSite/payments/paypalSendMoneyUri",wsc.getSetPaypalSendMoneyUri().toString());
	}

	public MTGCardStock getDefaultStock() {
		var defaultBool = FALSE;
		var st = new MTGCardStock();
					   st.setSigned(Boolean.parseBoolean(get("collections/defaultStock/signed",defaultBool)));
					   st.setAltered(Boolean.parseBoolean(get("collections/defaultStock/altered",defaultBool)));
					   st.setFoil(Boolean.parseBoolean(get("collections/defaultStock/foil",defaultBool)));
					   st.setEtched(Boolean.parseBoolean(get("collections/defaultStock/etched",defaultBool)));
					   st.setLanguage(get("collections/defaultStock/language","English"));
					   st.setCondition(EnumCondition.valueOf(get("collections/defaultStock/condition","NEAR_MINT")));
					   st.setQte(Integer.parseInt(get("collections/defaultStock/qty","1")));

		return st;
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



	public Dimension getPictureProviderDimension() {
		var w = Integer.parseInt(get("/card-pictures-dimension/width"));
		var h = Integer.parseInt(get("/card-pictures-dimension/height"));
		return new Dimension(w, h);
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
		} catch (Exception e) {
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
		var p = new Player();
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



	public void saveWallpaper(MTGWallpaper p) throws IOException {
		if (!MTGConstants.MTG_WALLPAPER_DIRECTORY.exists())
			MTGConstants.MTG_WALLPAPER_DIRECTORY.mkdir();

		ImageTools.saveImage(p.getPicture(),new File(MTGConstants.MTG_WALLPAPER_DIRECTORY, p.getName() + "." + p.getFormat()), p.getFormat());

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
		} catch (Exception e) {
			logger.error(notif.getMessage());
		}
	}

	public void cleaning() {
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


	public void init() throws SQLException {
		MTG.getEnabledPlugin(MTGCardsProvider.class).init();
		MTG.getEnabledPlugin(MTGDao.class).init();
		loadAccountsConfiguration();

	}




}
