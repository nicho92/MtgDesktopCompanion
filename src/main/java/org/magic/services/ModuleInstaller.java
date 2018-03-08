package org.magic.services;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.magic.api.interfaces.MTGPlugin;
import org.reflections.Reflections;

public class ModuleInstaller {

	
	private boolean hasUpdated=false;
	Logger logger = MTGLogger.getLogger(this.getClass());
	Reflections reflections;
	
	 public List<Class> getClasses(String packageName)  {
		 ArrayList<Class> classes = new ArrayList<>();
		 Reflections classReflections = new Reflections(packageName);
		 for(Class<? extends MTGPlugin> c :classReflections.getSubTypesOf(MTGPlugin.class) )
		 {
			if(!c.isInterface() && !Modifier.isAbstract(c.getModifiers()))
				classes.add(c);
		 }
	      return classes;
	    }
	
	
	 public boolean updateConfigWithNewModule() {
		
		for(Class c : extractMissing("org.magic.api.dao.impl", "/daos/dao"))
			 MTGControler.getInstance().addProperty("/daos/dao", c);
		
		for(Class c : extractMissing("org.magic.api.pricers.impl", "/pricers/pricer"))
			 MTGControler.getInstance().addProperty("/pricers/pricer", c);
		
		for(Class c : extractMissing("org.magic.api.providers.impl", "/providers/provider"))
			 MTGControler.getInstance().addProperty("/providers/provider", c);
		
		for(Class c : extractMissing("org.magic.api.shopping.impl", "/shoppers/shopper"))
			 MTGControler.getInstance().addProperty("/shoppers/shopper", c);
	
		for(Class c : extractMissing("org.magic.api.dashboard.impl", "/dashboards/dashboard"))
			 MTGControler.getInstance().addProperty("/dashboards/dashboard", c);
	
		for(Class c : extractMissing("org.magic.api.pictures.impl", "/pictures/picture"))
			 MTGControler.getInstance().addProperty("/pictures/picture", c);
	
		for(Class c : extractMissing("org.magic.api.decksniffer.impl", "/decksniffer/sniffer"))
			 MTGControler.getInstance().addProperty("/decksniffer/sniffer", c);
	
		for(Class c : extractMissing("org.magic.api.exports.impl", "/deckexports/export"))
			 MTGControler.getInstance().addProperty("/deckexports/export", c);

		for(Class c : extractMissing("org.magic.servers.impl", "/servers/server"))
			 MTGControler.getInstance().addProperty("/servers/server", c);
		
		for(Class c : extractMissing("org.magic.api.wallpaper.impl", "/wallpapers/wallpaper"))
			 MTGControler.getInstance().addProperty("/wallpapers/wallpaper", c);
		
		for(Class c : extractMissing("org.magic.api.news.impl", "/newsProvider/news"))
			 MTGControler.getInstance().addProperty("/newsProvider/news", c);
		
		return hasUpdated;
		
	 }
	 
	 
	public List<Class> extractMissing(String packages,String k) {
	
		List<Class> retour = new ArrayList<>();
		for(Class c : getClasses(packages))
		{
			if(!c.isAnonymousClass()&&!c.getName().contains("$"))
			{
					String path = k+"[class='"+c.getName()+"']/class";
					String s = MTGControler.getInstance().get(path);
					if(s=="")
					{
						hasUpdated=true;
						retour.add(c);
					}
			}
		}
		return retour;
	}

}
