package org.magic.services;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.lang3.ClassUtils;
import org.apache.logging.log4j.Logger;
import org.magic.api.beans.technical.PluginEntry;
import org.magic.api.interfaces.MTGCardRecognition;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGCardsIndexer;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGComboProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.MTGDeckSniffer;
import org.magic.api.interfaces.MTGExternalShop;
import org.magic.api.interfaces.MTGGedStorage;
import org.magic.api.interfaces.MTGGraders;
import org.magic.api.interfaces.MTGIA;
import org.magic.api.interfaces.MTGNetworkClient;
import org.magic.api.interfaces.MTGNewsProvider;
import org.magic.api.interfaces.MTGNotifier;
import org.magic.api.interfaces.MTGPictureCache;
import org.magic.api.interfaces.MTGPictureEditor;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.api.interfaces.MTGPlugin.PLUGINS;
import org.magic.api.interfaces.MTGPool;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.api.interfaces.MTGScript;
import org.magic.api.interfaces.MTGSealedProvider;
import org.magic.api.interfaces.MTGServer;
import org.magic.api.interfaces.MTGShopper;
import org.magic.api.interfaces.MTGTextGenerator;
import org.magic.api.interfaces.MTGTokensProvider;
import org.magic.api.interfaces.MTGTrackingService;
import org.magic.api.interfaces.MTGWallpaperProvider;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.services.logging.MTGLogger;
import org.reflections.Reflections;

public class PluginRegistry {

	private Map<Class,PluginEntry> registry;
	private static PluginRegistry instance;
	private Logger logger = MTGLogger.getLogger(this.getClass());
	private ClassLoader classLoader;
	private boolean hasUpdated = false;
	private XMLConfiguration config;
	private Map<String,String> pluginsToDelete;
	private boolean needUpdate;

	public Map<String,String> getPluginsToDelete() {
		return pluginsToDelete;
	}


	public List<String> getStringMethod(Class<?> classe)
	{
		List<String> meths = new ArrayList<>();
		Method[] mths = classe.getMethods();
		for(Method m : mths)
		{
			var sb = new StringBuilder();
			sb.append(m.getName()).append('(');
            Class<?>[] params = m.getParameterTypes();
            for (var j = 0; j < params.length; j++) {
                sb.append(params[j].getSimpleName());
                if (j < (params.length - 1))
                    sb.append(',');
            }
            sb.append(')');
            meths.add(sb.toString());
 		}
		return meths;
	}


	public static PluginRegistry inst() {
		if(instance==null)
			instance = new PluginRegistry();

		return instance;
	}

	public void setConfig(XMLConfiguration config) {
		this.config = config;
	}

	private PluginRegistry() {
			classLoader = PluginRegistry.class.getClassLoader();
			registry=new HashMap<>();
			pluginsToDelete=new TreeMap<>();
			init();
	}

	public <T> T newInstance(Class<T> classname) throws ClassNotFoundException {

		return newInstance(classname.getName());

	}


	@SuppressWarnings("unchecked")
	public <T> T newInstance(String classname) throws ClassNotFoundException {
		try {
			logger.trace("\tload plugin :  {}",classname);
			return (T) classLoader.loadClass(classname).getDeclaredConstructor().newInstance();
		}  catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException| NoSuchMethodException | SecurityException e) {
			logger.error("error loading {}",classname, e);
			return null;
		}
	}

	public <T extends MTGPlugin> PluginEntry<T> getEntryFor(Object k)
	{
		return getEntry(ClassUtils.getAllInterfaces(k.getClass()).get(0));
	}


	@SuppressWarnings("unchecked")
	public  <T extends MTGPlugin> PluginEntry<T> getEntry(Class p)
	{
		return registry.get(p);
	}

	private void init()
	{
		registry.put(MTGNotifier.class, new PluginEntry<>(MTGNotifier.class,true,"/notifiers","/notifier", "org.magic.api.notifiers.impl",PLUGINS.NOTIFIER,"Notifier will allow you to forward any notifications to another plateform"));
		registry.put(MTGDao.class, new PluginEntry<>(MTGDao.class,false,"/daos","/dao", "org.magic.api.dao.impl",PLUGINS.DAO,"Database plugins select the database that will hold the information you enter into MTG Companion. Only one can be active at a time. Be very careful changing this setting. You will need to export your database to the new database in the Configuration tab if you want to change this"));
		registry.put(MTGDashBoard.class, new PluginEntry<>(MTGDashBoard.class,false,"/dashboards","/dashboard", "org.magic.api.dashboard.impl",PLUGINS.DASHBOARD,"Dashboard plugin will get card trading and prices history on online plateform"));
		registry.put(MTGDeckSniffer.class, new PluginEntry<>(MTGDeckSniffer.class,true,"/decksniffer","/sniffer", "org.magic.api.decksniffer.impl",PLUGINS.DECKSNIFFER,"DeckSniffer plugin imports decks provided in external website"));
		registry.put(MTGCardsExport.class, new PluginEntry<>(MTGCardsExport.class,true,"/deckexports","/export", "org.magic.api.exports.impl",PLUGINS.EXPORT,"Import/Export plugins give the ability to add cards from various sites and files and export your collection to various formats"));
		registry.put(MTGNewsProvider.class, new PluginEntry<>(MTGNewsProvider.class,true,"/newsProvider","/news", "org.magic.api.news.impl",PLUGINS.NEWS, "Retrieves news from external MTG online websites"));
		registry.put(MTGPictureCache.class, new PluginEntry<>(MTGPictureCache.class,false,"/caches","/cache", "org.magic.api.cache.impl",PLUGINS.CACHE,"Cache plugins will store picture in memory (or disk)."));
		registry.put(MTGPictureProvider.class, new PluginEntry<>(MTGPictureProvider.class,false,"/pictures","/picture", "org.magic.api.pictures.impl",PLUGINS.PICTURE,"Pictures plugins will get card's images"));
		registry.put(MTGPricesProvider.class, new PluginEntry<>(MTGPricesProvider.class,true,"/pricers","/pricer", "org.magic.api.pricers.impl",PLUGINS.PRICER,"Pricer plugins pull pricing data for cards from various sources"));
		registry.put(MTGCardsProvider.class, new PluginEntry<>(MTGCardsProvider.class,false,"/providers","/provider", "org.magic.api.providers.impl",PLUGINS.PROVIDER,"Cards plugins pull the card data for Magic cards"));
		registry.put(MTGServer.class, new PluginEntry<>(MTGServer.class,true,"/servers","/server", "org.magic.servers.impl",PLUGINS.SERVER,"Enable background service for your companion"));
		registry.put(MTGShopper.class, new PluginEntry<>(MTGShopper.class,true,"/shoppers","/shopper", "org.magic.api.shopping.impl",PLUGINS.SHOPPER,"Shoppers allow you to import transaction on external shop"));
		registry.put(MTGTokensProvider.class, new PluginEntry<>(MTGTokensProvider.class,false,"/tokens","/token", "org.magic.api.tokens.impl",PLUGINS.TOKEN,"Token plugins pull the token product data for Magic cards"));
		registry.put(MTGWallpaperProvider.class, new PluginEntry<>(MTGWallpaperProvider.class,true,"/wallpapers","/wallpaper", "org.magic.api.wallpaper.impl",PLUGINS.WALLPAPER," External wallpapers for your computer or your simulator background"));
		registry.put(MTGPictureEditor.class, new PluginEntry<>(MTGPictureEditor.class,false,"/editors", "/editor", "org.magic.api.pictureseditor.impl",PLUGINS.EDITOR, " Rendering your card's creation"));
		registry.put(MTGCardsIndexer.class, new PluginEntry<>(MTGCardsIndexer.class,false, "/indexers", "/index", "org.magic.api.indexer.impl",PLUGINS.INDEXER,"Indexer will index the whole selected cards provider database, to enable fast search and data indexation."));
		registry.put(MTGTextGenerator.class, new PluginEntry<>(MTGTextGenerator.class,false, "/textGenerators", "/textGenerator", "org.magic.api.generators.impl",PLUGINS.GENERATOR," Help your to have right syntax card's body for your builder"));
		registry.put(MTGScript.class, new PluginEntry<>(MTGScript.class,true, "/scripts", "/script", "org.magic.api.scripts.impl",PLUGINS.SCRIPT,"Scripts engines for scripting tasks module"));
		registry.put(MTGPool.class, new PluginEntry<>(MTGPool.class,false, "/pools", "/pool", "org.magic.api.pool.impl",PLUGINS.POOL,"Pool will allow you to cache and manage connections to the selected DAO"));
		registry.put(MTGComboProvider.class, new PluginEntry<>(MTGComboProvider.class,true, "/combos", "/combo", "org.magic.api.combo.impl",PLUGINS.COMBO," Pull combo for magic cards"));
		registry.put(MTGGraders.class, new PluginEntry<>(MTGGraders.class,true, "/graders", "/grader", "org.magic.api.graders.impl",PLUGINS.GRADING,"Graders plugins allow you to import grading information from the serial number"));
		registry.put(MTGGedStorage.class, new PluginEntry<>(MTGGedStorage.class,false, "/storages", "/storage", "org.magic.api.fs.impl",PLUGINS.GED,"Store files or pictures in the app"));
		registry.put(MTGCardRecognition.class, new PluginEntry<>(MTGCardRecognition.class,false, "/strategies", "/strategy", "org.magic.api.recognition.impl",PLUGINS.STRATEGY, "technical plugin for card recognition"));
		registry.put(MTGTrackingService.class, new PluginEntry<>(MTGTrackingService.class,true, "/trackings", "/tracker", "org.magic.api.tracking.impl",PLUGINS.TRACKING,"Tracker plugin get parcel's tracking for your transactions"));
		registry.put(MTGExternalShop.class, new PluginEntry<>(MTGExternalShop.class,false, "/externalsShops", "/extshop", "org.magic.api.externalshop.impl",PLUGINS.EXTERNAL_SHOP,"external shop can interact with your stock  and synchronisation from your onlines stores"));
		registry.put(MTGIA.class, new PluginEntry<>(MTGIA.class,false, "/artificialIntelligences", "/ia", "org.magic.api.ia.impl",PLUGINS.IA, "help you to get information about cards, or build decks"));
		registry.put(MTGSealedProvider.class, new PluginEntry<>(MTGSealedProvider.class,false, "/sealeds", "/sealedProvider", "org.magic.api.sealedprovider.impl",PLUGINS.SEALED,"Sealed plugins pull the sealed product data for Magic cards"));
		registry.put(MTGNetworkClient.class, new PluginEntry<>(MTGNetworkClient.class,false, "/networks", "/network", "org.magic.api.network.impl",PLUGINS.NETWORK,"Will connecte you to external MTGCompanion messaging service"));
		registry.put(AbstractJDashlet.class, new PluginEntry<>(AbstractJDashlet.class,true,"/dashlets", "/dashlet", "org.magic.gui.dashlet",PLUGINS.DASHLET ," Customize your dashboard"));
		
		logger.debug("MTG Plugins Registry loaded");
	}


	public Class loadClass(String name) throws ClassNotFoundException
	{
		return classLoader.loadClass(name);
	}


	public <T extends MTGPlugin> List<Class<T>> listClasses()
	{
		List<Class<T>> ret = new ArrayList<>();
		registry.keySet().forEach(ret::add);
		return ret;
	}

	@SuppressWarnings("unchecked")
	public synchronized <T extends MTGPlugin> List<T> listPlugins(Class<T> classe)
	{

		PluginEntry<T> entry = registry.get(classe);

		if(!entry.getPlugins().isEmpty())
			return entry.getPlugins();

		var listRemoved = new ArrayList<String>();

		logger.debug("loading {}",classe.getSimpleName());
		for (var i = 1; i <= config.getList("/"+entry.getElement()+"/class").size(); i++)
		{
			var s = config.getString(entry.getXpath()+"[" + i + "]/class");
			T prov = null;
			try{
				prov = newInstance(s);
			}
			catch (ClassNotFoundException _) {
				logger.error("\t{} is not found",s);
				listRemoved.add(entry.getXpath()+"[class='"+s+"']");
				needUpdate=true;
			}
			if (prov != null)
			{
				try {
					prov.enable(config.getBoolean(entry.getXpath()+"[" + i + "]/enable"));
				}
				catch(Exception e)
				{
					logger.trace(e);
					prov.enable(true);
				}
				entry.getPlugins().add(prov);
			}
		}


		if(needUpdate)
		{
			listRemoved.stream().forEach(config::clearTree);
		}

		return entry.getPlugins().stream().toList();
	}

	public boolean needUpdate()
	{
		return needUpdate;
	}

	public Set<Entry<Class, PluginEntry>> entrySet() {
		return registry.entrySet();
	}

	public List<MTGPlugin> listPlugins()
	{
		List<MTGPlugin> list = new ArrayList<>();
		PluginRegistry.inst().listClasses().stream().forEach(c->PluginRegistry.inst().listPlugins(c).forEach(list::add));
		return list;
	}


	public List<Class> getClasses(String packageName) {
		ArrayList<Class> classes = new ArrayList<>();
		var classReflections = new Reflections(packageName);
		for (Class<? extends MTGPlugin> c : classReflections.getSubTypesOf(MTGPlugin.class)) {
			if (!c.isInterface() && !Modifier.isAbstract(c.getModifiers()))
				classes.add(c);
		}
		return classes;
	}


	public boolean updateConfigWithNewModule() {
		entrySet().forEach(p->
		{
			for (Class<MTGPlugin> c : extractMissing(p.getValue().getClasspath(), p.getValue().getXpath()))
				MTGControler.getInstance().addProperty(p.getValue().getXpath(), c);

		});
		return hasUpdated;

	}

	private List<Class> extractMissing(String packages, String k) {

		List<Class> retour = new ArrayList<>();
		for (var c : getClasses(packages)) {
			if (!c.isAnonymousClass() && !c.getName().contains("$")) {
				String path = k + "[class='" + c.getName() + "']/class";
				String s = MTGControler.getInstance().get(path);
				if (s.isEmpty()) {
					hasUpdated = true;
					retour.add(c);
				}
			}
		}

		return retour;
	}

	public <T extends MTGPlugin> T getPlugin(String name,Class<T> type) {
		Optional<T> r = listPlugins(type).stream().filter(s->s.getName().equalsIgnoreCase(name)).findFirst();

		if(r.isPresent())
			return r.get();
		
		
		var cname = name.replaceAll("[\n\r\t]", "_");		
		logger.error("{} doesn't exist or is not enabled",cname);
		return null;
	}

	public <T extends MTGPlugin> List<T> listEnabledPlugins(Class<T> t) {
		return listPlugins(t).stream().filter(MTGPlugin::isEnable).sorted().toList();
	}

	public <T extends MTGPlugin> T getEnabledPlugins(Class<T> t) {
		return listPlugins(t).stream().filter(MTGPlugin::isEnable).findFirst().orElse(null);
	}


	public MTGPlugin getPluginById(String id) {
		return listPlugins().stream().filter(p->p.getId().equals(id)).findFirst().orElse(null);
	}



}



