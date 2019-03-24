package org.magic.services;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.lang3.ClassUtils;
import org.apache.log4j.Logger;
import org.magic.api.beans.PluginEntry;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGCardsIndexer;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.MTGDeckSniffer;
import org.magic.api.interfaces.MTGNewsProvider;
import org.magic.api.interfaces.MTGNotifier;
import org.magic.api.interfaces.MTGPictureEditor;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.MTGPicturesCache;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.api.interfaces.MTGServer;
import org.magic.api.interfaces.MTGShopper;
import org.magic.api.interfaces.MTGTextGenerator;
import org.magic.api.interfaces.MTGTokensProvider;
import org.magic.api.interfaces.MTGWallpaperProvider;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
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
	
	public <T> T newInstance(String classname) throws ClassNotFoundException {
		try {
			logger.debug("\tload plugin :  " + classname);
			return (T) classLoader.loadClass(classname).getDeclaredConstructor().newInstance();
		}  catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException| NoSuchMethodException | SecurityException e) {
			logger.error("error loading " + classname, e);
			return null;
		}
	}

	public PluginEntry getEntryFor(Object k)
	{
		return getEntry(ClassUtils.getAllInterfaces(k.getClass()).get(0));
	}
	
	public PluginEntry getEntry(Class p)
	{
		return registry.get(p);
	}
	
	private void init()
	{
		registry.put(MTGNotifier.class, new PluginEntry<MTGNotifier>(true,"/notifiers","/notifier", "org.magic.api.notifiers.impl"));
		registry.put(MTGDao.class, new PluginEntry<MTGDao>(false,"/daos","/dao", "org.magic.api.dao.impl"));
		registry.put(MTGDashBoard.class, new PluginEntry<MTGDashBoard>(false,"/dashboards","/dashboard", "org.magic.api.dashboard.impl"));
		registry.put(MTGDeckSniffer.class, new PluginEntry<MTGDeckSniffer>(true,"/decksniffer","/sniffer", "org.magic.api.decksniffer.impl"));
		registry.put(MTGCardsExport.class, new PluginEntry<MTGCardsExport>(true,"/deckexports","/export", "org.magic.api.exports.impl"));
		registry.put(MTGNewsProvider.class, new PluginEntry<MTGNewsProvider>(true,"/newsProvider","/news", "org.magic.api.news.impl"));
		registry.put(MTGPicturesCache.class, new PluginEntry<MTGPicturesCache>(false,"/caches","/cache", "org.magic.api.cache.impl"));
		registry.put(MTGPictureProvider.class, new PluginEntry<MTGPictureProvider>(false,"/pictures","/picture", "org.magic.api.pictures.impl"));
		registry.put(MTGPricesProvider.class, new PluginEntry<MTGPricesProvider>(true,"/pricers","/pricer", "org.magic.api.pricers.impl"));
		registry.put(MTGCardsProvider.class, new PluginEntry<MTGCardsProvider>(false,"/providers","/provider", "org.magic.api.providers.impl"));
		registry.put(MTGServer.class, new PluginEntry<MTGServer>(true,"/servers","/server", "org.magic.servers.impl"));
		registry.put(MTGShopper.class, new PluginEntry<MTGShopper>(true,"/shoppers","/shopper", "org.magic.api.shopping.impl"));
		registry.put(MTGTokensProvider.class, new PluginEntry<MTGTokensProvider>(false,"/tokens","/token", "org.magic.api.tokens.impl"));
		registry.put(MTGWallpaperProvider.class, new PluginEntry<MTGWallpaperProvider>(true,"/wallpapers","/wallpaper", "org.magic.api.wallpaper.impl"));
		registry.put(AbstractJDashlet.class, new PluginEntry<AbstractJDashlet>(true,"/dashlets", "/dashlet", "org.magic.gui.dashlet"));
		registry.put(MTGPictureEditor.class, new PluginEntry<MTGPictureEditor>(false,"/editors", "/editor", "org.magic.api.pictureseditor.impl"));
		registry.put(MTGCardsIndexer.class, new PluginEntry<MTGCardsIndexer>(false, "/indexers", "/index", "org.magic.api.indexer.impl"));
		registry.put(MTGTextGenerator.class, new PluginEntry<MTGTextGenerator>(false, "/textGenerators", "/textGenerator", "org.magic.api.generators.impl"));
	}
	
	public Class loadClass(String name) throws ClassNotFoundException
	{
		return classLoader.loadClass(name);
	}
	
	
	
	public synchronized <T extends MTGPlugin> List<T> listPlugins(Class<T> classe)
	{
		PluginEntry<T> entry = registry.get(classe);
		
		if(!entry.getPlugins().isEmpty())
			return entry.getPlugins();
		
		logger.debug("loading " + classe.getSimpleName());
		for (int i = 1; i <= config.getList("/"+entry.getElement()+"/class").size(); i++) {
			String s = config.getString(entry.getXpath()+"[" + i + "]/class");
			T prov = null;
			try{
				prov = newInstance(s);
			}
			catch (ClassNotFoundException e) {
				logger.error("\t"+s + " is not found");
				config.clearTree(entry.getXpath()+"[class='"+s+"']");
				needUpdate=true;
			}
			if (prov != null) {
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
		return entry.getPlugins();
	}
	
	public boolean needUpdate()
	{
		return needUpdate;
	}
	
	public Set<Entry<Class, PluginEntry>> entrySet() {
		return registry.entrySet();
	}

	public List<Class> getClasses(String packageName) {
		ArrayList<Class> classes = new ArrayList<>();
		Reflections classReflections = new Reflections(packageName);
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
		for (Class c : getClasses(packages)) {
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
		logger.trace("searching for " + name +" plugin");
		Optional<T> r = listPlugins(type).stream().filter(s->s.getName().equalsIgnoreCase(name)).findFirst();
		
		if(r.isPresent())
			return r.get();
		
		logger.error(name + " doesn't exist or is not enabled");
		return null;
	}

	public <T extends MTGPlugin> List<T> listEnabledPlugins(Class<T> t) {
		return listPlugins(t).stream().filter(MTGPlugin::isEnable).collect(Collectors.toList());
	}
	
	public <T extends MTGPlugin> T getEnabledPlugins(Class<T> t) {
		
		Optional<T> r = listPlugins(t).stream().filter(MTGPlugin::isEnable).findFirst();
		
		if(r.isPresent())
			return r.get();
		else
			return null;
	}
	
}



