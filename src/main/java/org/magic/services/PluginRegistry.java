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
import org.apache.log4j.Logger;
import org.magic.api.beans.PluginEntry;
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
import org.magic.api.interfaces.MTGServer;
import org.magic.api.interfaces.MTGShopper;
import org.magic.api.interfaces.MTGTextGenerator;
import org.magic.api.interfaces.MTGTokensProvider;
import org.magic.api.interfaces.MTGTrackingService;
import org.magic.api.interfaces.MTGWallpaperProvider;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.reflections.Reflections;

@SuppressWarnings({"rawtypes","unchecked"})
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
	
	
	public List<String> getStringMethod(Class classe)
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
	
	
	public <T> T newInstance(String classname) throws ClassNotFoundException {
		try {
			logger.trace("\tload plugin :  " + classname);
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
		registry.put(MTGNotifier.class, new PluginEntry<>(MTGNotifier.class,true,"/notifiers","/notifier", "org.magic.api.notifiers.impl",PLUGINS.NOTIFIER));
		registry.put(MTGDao.class, new PluginEntry<>(MTGDao.class,false,"/daos","/dao", "org.magic.api.dao.impl",PLUGINS.DAO));
		registry.put(MTGDashBoard.class, new PluginEntry<>(MTGDashBoard.class,false,"/dashboards","/dashboard", "org.magic.api.dashboard.impl",PLUGINS.DASHBOARD));
		registry.put(MTGDeckSniffer.class, new PluginEntry<>(MTGDeckSniffer.class,true,"/decksniffer","/sniffer", "org.magic.api.decksniffer.impl",PLUGINS.DECKSNIFFER));
		registry.put(MTGCardsExport.class, new PluginEntry<>(MTGCardsExport.class,true,"/deckexports","/export", "org.magic.api.exports.impl",PLUGINS.EXPORT));
		registry.put(MTGNewsProvider.class, new PluginEntry<>(MTGNewsProvider.class,true,"/newsProvider","/news", "org.magic.api.news.impl",PLUGINS.NEWS));
		registry.put(MTGPictureCache.class, new PluginEntry<>(MTGPictureCache.class,false,"/caches","/cache", "org.magic.api.cache.impl",PLUGINS.CACHE));
		registry.put(MTGPictureProvider.class, new PluginEntry<>(MTGPictureProvider.class,false,"/pictures","/picture", "org.magic.api.pictures.impl",PLUGINS.PICTURE));
		registry.put(MTGPricesProvider.class, new PluginEntry<>(MTGPricesProvider.class,true,"/pricers","/pricer", "org.magic.api.pricers.impl",PLUGINS.PRICER));
		registry.put(MTGCardsProvider.class, new PluginEntry<>(MTGCardsProvider.class,false,"/providers","/provider", "org.magic.api.providers.impl",PLUGINS.PROVIDER));
		registry.put(MTGServer.class, new PluginEntry<>(MTGServer.class,true,"/servers","/server", "org.magic.servers.impl",PLUGINS.SERVER));
		registry.put(MTGShopper.class, new PluginEntry<>(MTGShopper.class,true,"/shoppers","/shopper", "org.magic.api.shopping.impl",PLUGINS.SHOPPER));
		registry.put(MTGTokensProvider.class, new PluginEntry<>(MTGTokensProvider.class,false,"/tokens","/token", "org.magic.api.tokens.impl",PLUGINS.TOKEN));
		registry.put(MTGWallpaperProvider.class, new PluginEntry<>(MTGWallpaperProvider.class,true,"/wallpapers","/wallpaper", "org.magic.api.wallpaper.impl",PLUGINS.WALLPAPER));
		registry.put(MTGPictureEditor.class, new PluginEntry<>(MTGPictureEditor.class,false,"/editors", "/editor", "org.magic.api.pictureseditor.impl",PLUGINS.EDITOR));
		registry.put(MTGCardsIndexer.class, new PluginEntry<>(MTGCardsIndexer.class,false, "/indexers", "/index", "org.magic.api.indexer.impl",PLUGINS.INDEXER));
		registry.put(MTGTextGenerator.class, new PluginEntry<>(MTGTextGenerator.class,false, "/textGenerators", "/textGenerator", "org.magic.api.generators.impl",PLUGINS.GENERATOR));
		registry.put(MTGScript.class, new PluginEntry<>(MTGScript.class,true, "/scripts", "/script", "org.magic.api.scripts.impl",PLUGINS.SCRIPT));
		registry.put(MTGPool.class, new PluginEntry<>(MTGPool.class,false, "/pools", "/pool", "org.magic.api.pool.impl",PLUGINS.POOL));
		registry.put(MTGComboProvider.class, new PluginEntry<>(MTGComboProvider.class,true, "/combos", "/combo", "org.magic.api.combo.impl",PLUGINS.COMBO));
		registry.put(MTGGraders.class, new PluginEntry<>(MTGGraders.class,true, "/graders", "/grader", "org.magic.api.graders.impl",PLUGINS.GRADING));
		registry.put(MTGGedStorage.class, new PluginEntry<>(MTGGedStorage.class,false, "/storages", "/storage", "org.magic.api.fs.impl",PLUGINS.GED));
		registry.put(MTGCardRecognition.class, new PluginEntry<>(MTGCardRecognition.class,false, "/strategies", "/strategy", "org.magic.api.recognition.impl",PLUGINS.STRATEGY));
		registry.put(MTGTrackingService.class, new PluginEntry<>(MTGTrackingService.class,true, "/trackings", "/tracker", "org.magic.api.tracking.impl",PLUGINS.TRACKING));
		registry.put(MTGExternalShop.class, new PluginEntry<>(MTGExternalShop.class,false, "/externalsShops", "/extshop", "org.magic.api.externalshop.impl",PLUGINS.EXTERNAL_SHOP));
		
		
		registry.put(AbstractJDashlet.class, new PluginEntry<>(AbstractJDashlet.class,true,"/dashlets", "/dashlet", "org.magic.gui.dashlet",PLUGINS.DASHLET));

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
	
	public synchronized <T extends MTGPlugin> List<T> listPlugins(Class<T> classe)
	{
		PluginEntry<T> entry = registry.get(classe);
		
		if(!entry.getPlugins().isEmpty())
			return entry.getPlugins();
		
		var listRemoved = new ArrayList<String>();
		
		logger.debug("loading " + classe.getSimpleName());
		for (var i = 1; i <= config.getList("/"+entry.getElement()+"/class").size(); i++) 
		{
			var s = config.getString(entry.getXpath()+"[" + i + "]/class");
			T prov = null;
			try{
				prov = newInstance(s);
			}
			catch (ClassNotFoundException e) {
				logger.error("\t"+s + " is not found");
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
		logger.trace("searching for " + name +" plugin");
		Optional<T> r = listPlugins(type).stream().filter(s->s.getName().equalsIgnoreCase(name)).findFirst();
		
		if(r.isPresent())
			return r.get();
		
		logger.error(name + " doesn't exist or is not enabled");
		return null;
	}
	

	public MTGPlugin getPlugin(String name) {
		Optional<MTGPlugin> r = listPlugins().stream().filter(p->p.getName().equalsIgnoreCase(name)).findFirst();
		
		if(r.isPresent())
			return r.get();
		
		
		logger.error(name + " doesn't exist");
		return null;
		
	}
	

	public <T extends MTGPlugin> List<T> listEnabledPlugins(Class<T> t) {
		return listPlugins(t).stream().filter(MTGPlugin::isEnable).sorted().toList();
	}
	
	public <T extends MTGPlugin> T getEnabledPlugins(Class<T> t) {
		return listPlugins(t).stream().filter(MTGPlugin::isEnable).findFirst().orElse(null);
	}


	
}



