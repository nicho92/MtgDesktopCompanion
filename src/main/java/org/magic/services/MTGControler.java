package org.magic.services;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
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
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.Wallpaper;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.MTGDeckSniffer;
import org.magic.api.interfaces.MTGNewsProvider;
import org.magic.api.interfaces.MTGNotifier;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.MTGPicturesCache;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.api.interfaces.MTGServer;
import org.magic.api.interfaces.MTGShopper;
import org.magic.api.interfaces.MTGTokensProvider;
import org.magic.api.interfaces.MTGWallpaperProvider;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.game.model.Player;
import org.magic.services.extra.CurrencyConverter;
import org.magic.services.extra.KeyWordProvider;
import org.magic.services.extra.LookAndFeelProvider;
import org.magic.tools.ImageUtils;

public class MTGControler {

	private static MTGControler inst;
	private KeyWordProvider keyWordManager;
	private XMLConfiguration config;
	private FileBasedConfigurationBuilder<XMLConfiguration> builder;
	private LanguageService langService;
	private CurrencyConverter currencyService;
	private LookAndFeelProvider lafService;
	private Logger logger = MTGLogger.getLogger(this.getClass());
	
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

	public Dimension getCardsGameDimension() {
		int w = Integer.parseInt(get("/game/cards/card-width"));
		int h = Integer.parseInt(get("/game/cards/card-height"));
		return new Dimension(w, h);
	}

	public void addProperty(String path, Class classname) {
		String[] k = path.split("/");

		String root = k[1];
		String elem = k[2];
		try {
			config.addProperty("/" + root + " " + elem + "/class", classname.getName());
			logger.debug("add module " + path + " " + classname.getName());
			setProperty(classname.getDeclaredConstructor().newInstance(), false);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public Dimension getPictureProviderDimension() {
		int w = Integer.parseInt(get("/card-pictures-dimension/width"));
		int h = Integer.parseInt(get("/card-pictures-dimension/height"));
		return new Dimension(w, h);
	}

	public void setProperty(Object k, Object c) {
		try {
			String path = "";
			logger.debug("set " + k + " to " + c);

			if(k instanceof MTGPlugin){
				path = PluginRegistry.inst().getEntryFor(k).getXpath()+"[class='" + k.getClass().getName() + "']/enable";
			}
			else {
				path = k.toString();
			}

			config.setProperty(path, c);
			builder.save();
		} catch (Exception e) {
			logger.error("Error saving " + c, e);
		}
	}
	
	public MagicCard switchEditions(MagicCard mc, MagicEdition ed)
	{
		try {
			return MTGControler.getInstance().getEnabledCardsProviders().searchCardByName(mc.getName(), ed, true).get(0);
		} catch (IOException e) {
			logger.error(mc +" is not found in " + ed);
			return mc;
		}
	}

	
	public Locale getLocale() {
		try {
			return LocaleUtils.toLocale(config.getString("locale"));
		} catch (Exception e) {
			logger.error("Could not load " + config.getString("locale"));
			return langService.getDefault();
		}
	}

	public String get(String prop, String defaut) {
		return config.getString(prop, defaut);
	}

	public String get(String prop) {
		return config.getString(prop, "");
	}

	public Player getProfilPlayer() {
		Player p = new Player();
		p.setName(config.getString("/game/player-profil/name"));

		String url = config.getString("/game/player-profil/avatar");
		try {
			p.setIcon(ImageIO.read(new File(url)));
		} catch (Exception e) {
			logger.error("error loading player" + p, e);
		}
		return p;
	}

	private MTGControler() {
		
		File conf = new File(MTGConstants.CONF_DIR, MTGConstants.CONF_FILENAME);
		if (!conf.exists())
			try {
				logger.info(conf+" file doesn't exist. creating one from default file");
				FileUtils.copyURLToFile(getClass().getResource("/default-conf.xml"),
						new File(MTGConstants.CONF_DIR, MTGConstants.CONF_FILENAME));
				logger.info("conf file created");
			} catch (IOException e1) {
				logger.error(e1);
			}

		Parameters params = new Parameters();
		builder = new FileBasedConfigurationBuilder<XMLConfiguration>(XMLConfiguration.class).configure(params.xml()
				.setFile(new File(MTGConstants.CONF_DIR, MTGConstants.CONF_FILENAME)).setSchemaValidation(false)
				.setValidating(false).setEncoding(MTGConstants.DEFAULT_ENCODING).setExpressionEngine(new XPathExpressionEngine()));

		try {
			config = builder.getConfiguration();
			
			PluginRegistry.inst().setConfig(config);
			
			keyWordManager = new KeyWordProvider();
			langService = new LanguageService();
			langService.changeLocal(getLocale());
			currencyService = new CurrencyConverter(get("currencylayer-access-api"));
			
		} catch (Exception e) {
			logger.error("error init", e);
		}
	}
	
	public CurrencyConverter getCurrencyService() {
		return currencyService;
	}

	public List<AbstractJDashlet> getDashlets() {
		return PluginRegistry.inst().listPlugins(AbstractJDashlet.class);
	}
	
	public List<MTGTokensProvider> getTokens() {
		return PluginRegistry.inst().listPlugins(MTGTokensProvider.class);
	}
	

	public KeyWordProvider getKeyWordManager() {
		return keyWordManager;
	}

	public boolean updateConfigMods() {
		return PluginRegistry.inst().updateConfigWithNewModule();
	}

	public MTGPicturesCache getEnabledCache() {
		for (MTGPicturesCache p : getCachesProviders())
			if (p.isEnable())
				return p;

		return null;
	}
	
	public MTGTokensProvider getEnabledTokensProvider() {
		for (MTGTokensProvider p : getTokens())
			if (p.isEnable())
				return p;

		return null;
	}
	
	
	public List<MTGNotifier> getNotifierProviders(){
		return PluginRegistry.inst().listPlugins(MTGNotifier.class);
	}
	
	public List<MTGNotifier> getEnabledNotifiers() {
		List<MTGNotifier> notifierE = new ArrayList<>();

		for (MTGNotifier p : getNotifierProviders())
			if (p.isEnable())
				notifierE.add(p);

		return notifierE;
	}
	

	public List<MTGPicturesCache> getCachesProviders() {
		return PluginRegistry.inst().listPlugins(MTGPicturesCache.class);
	}

	public List<MTGCardsProvider> getCardsProviders() {
		return PluginRegistry.inst().listPlugins(MTGCardsProvider.class);
	}

	public List<MTGDao> getDaoProviders() {
		return PluginRegistry.inst().listPlugins(MTGDao.class);
	}

	public List<MTGPictureProvider> getPicturesProviders() {
		return PluginRegistry.inst().listPlugins(MTGPictureProvider.class);
	}

	public List<MTGPricesProvider> getPricerProviders() {
		return PluginRegistry.inst().listPlugins(MTGPricesProvider.class);
	}

	public List<MTGPricesProvider> getEnabledPricers() {
		List<MTGPricesProvider> pricersE = new ArrayList<>();

		for (MTGPricesProvider p : getPricerProviders())
			if (p.isEnable())
				pricersE.add(p);

		return pricersE;
	}

	public MTGCardsProvider getEnabledCardsProviders() {
		for (MTGCardsProvider p : getCardsProviders())
			if (p.isEnable())
				return p;

		return null;
	}

	public MTGPictureProvider getEnabledPicturesProvider() {
		for (MTGPictureProvider p : getPicturesProviders())
			if (p.isEnable())
				return p;

		return null;
	}

	public MTGDao getEnabledDAO() {
		for (MTGDao p : getDaoProviders())
			if (p.isEnable())
				return p;
		return null;
	}

	public List<MTGDeckSniffer> getEnabledDeckSniffer() {
		List<MTGDeckSniffer> prov = new ArrayList<>();

		for (MTGDeckSniffer p : getDeckSnifferProviders())
			if (p.isEnable())
				prov.add(p);

		return prov;
	}

	public List<MTGDeckSniffer> getDeckSnifferProviders() {
		return PluginRegistry.inst().listPlugins(MTGDeckSniffer.class);
	}

	public List<MTGShopper> getShoppersProviders() {
		return PluginRegistry.inst().listPlugins(MTGShopper.class);
	}

	public List<MTGShopper> getEnabledShoppers() {
		List<MTGShopper> enable = new ArrayList<>();
		for (MTGShopper p : getShoppersProviders())
			if (p.isEnable())
				enable.add(p);

		return enable;
	}

	public MTGDashBoard getEnabledDashBoard() {
		for (MTGDashBoard p : getDashboardsProviders())
			if (p.isEnable())
				return p;

		return null;
	}

	public List<MTGDashBoard> getDashboardsProviders() {
		return  PluginRegistry.inst().listPlugins(MTGDashBoard.class);
	}

	public List<MTGServer> getServers() {
		return PluginRegistry.inst().listPlugins(MTGServer.class);
	}

	public List<MTGServer> getEnabledServers() {
		List<MTGServer> enable = new ArrayList<>();
		for (MTGServer p : getServers())
			if (p.isEnable())
				enable.add(p);
		return enable;
	}

	public List<MTGCardsExport> getImportExportProviders() {
		return PluginRegistry.inst().listPlugins(MTGCardsExport.class);
	}

	public List<MTGCardsExport> getEnabledDeckExports() {
		List<MTGCardsExport> enable = new ArrayList<>();
		for (MTGCardsExport p : getImportExportProviders())
			if (p.isEnable())
				enable.add(p);

		return enable;
	}

	public boolean isRunning(MTGServer server) {
		for (MTGServer serv : getEnabledServers())
			if (serv.getName().equals(server.getName()))
				return serv.isAlive();

		return false;
	}

	public MTGCardsExport getAbstractExporterFromExt(File f) {
		String ext = FilenameUtils.getExtension(f.getAbsolutePath());

		for (MTGCardsExport ace : getImportExportProviders()) {
			if (ace.getFileExtension().endsWith(ext))
				return ace;
		}
		return null;
	}

	public List<MTGNewsProvider> getEnabledNewsProviders() {
		List<MTGNewsProvider> enable = new ArrayList<>();
		for (MTGNewsProvider p : getNewsProviders())
			if (p.isEnable())
				enable.add(p);

		return enable;
	}

	public List<MTGNewsProvider> getNewsProviders() {
		return PluginRegistry.inst().listPlugins(MTGNewsProvider.class);
	}

	public MTGNewsProvider getNewsProvider(String string) {
		for (MTGNewsProvider p : getNewsProviders())
			if (p.getName().equalsIgnoreCase(string))
				return p;

		return null;
	}

	public void saveWallpaper(Wallpaper p) throws IOException {
		if (!MTGConstants.MTG_WALLPAPER_DIRECTORY.exists())
			MTGConstants.MTG_WALLPAPER_DIRECTORY.mkdir();

		ImageUtils.saveImage(p.getPicture(),
				new File(MTGConstants.MTG_WALLPAPER_DIRECTORY, p.getName() + "." + p.getFormat()), p.getFormat());

	}

	public List<MTGWallpaperProvider> getWallpaperProviders() {
		return PluginRegistry.inst().listPlugins(MTGWallpaperProvider.class);
	}

	public List<MTGWallpaperProvider> getEnabledWallpaper() {
		List<MTGWallpaperProvider> enable = new ArrayList<>();
		for (MTGWallpaperProvider p : getWallpaperProviders())
			if (p.isEnable())
				enable.add(p);

		return enable;
	}

	@SuppressWarnings("unchecked")
	public <T extends MTGPlugin> T getPlugin(String name,Class<T> type) {
		return PluginRegistry.inst().getPlugin(name,type);
	}
	
	
	private MTGNotifier notifier;
	public void notify(MTGNotification notif)
	{
		try {
			if(notifier==null)
				notifier=getPlugin(MTGConstants.DEFAULT_NOTIFIER_NAME, MTGNotifier.class);
			
			notifier.send(notif);
		} catch (IOException e) {
			logger.error(notif.getMessage());
		}
	}
	
}
