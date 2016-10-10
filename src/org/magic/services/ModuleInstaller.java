package org.magic.services;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ModuleInstaller {

	
	private boolean hasUpdated=false;
	
	
	 private List<Class> getClasses(String packageName) throws ClassNotFoundException, IOException {
	        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	        assert classLoader != null;
	        String path = packageName.replace('.', '/');
	        Enumeration<URL> resources = classLoader.getResources(path);
	        List<File> dirs = new ArrayList<File>();
	        while (resources.hasMoreElements()) {
	            URL resource = resources.nextElement();
	            dirs.add(new File(resource.getFile()));
	        }
	        ArrayList<Class> classes = new ArrayList<Class>();
	        for (File directory : dirs) {
	            classes.addAll(findClasses(directory, packageName));
	        }
	        return classes;
	    }
	
	 
	 private List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
	        List<Class> classes = new ArrayList();
	        if (!directory.exists()) {
	            return classes;
	        }
	        File[] files = directory.listFiles();
	        for (File file : files) {
	            if (file.isDirectory()) {
	                assert !file.getName().contains(".");
	                classes.addAll(findClasses(file, packageName + "." + file.getName()));
	            } else if (file.getName().endsWith(".class")) {
	                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
	            }
	        }
	        return classes;
	    }
	
	 public boolean updateConfigWithNewModule() throws ClassNotFoundException, IOException {
		
		for(Class c : extractMissing("org.magic.api.dao.impl", "/daos/dao"))
			 MagicFactory.getInstance().addProperty("/daos/dao", c);
		
		for(Class c : extractMissing("org.magic.api.pricers.impl", "/pricers/pricer"))
			 MagicFactory.getInstance().addProperty("/pricers/pricer", c);
		
		for(Class c : extractMissing("org.magic.api.providers.impl", "/providers/provider"))
			 MagicFactory.getInstance().addProperty("/providers/provider", c);
		
		for(Class c : extractMissing("org.magic.api.shopping.impl", "/shoppers/shopper"))
			 MagicFactory.getInstance().addProperty("/shoppers/shopper", c);
	
		for(Class c : extractMissing("org.magic.api.dashboard.impl", "/dashboards/dashboard"))
			 MagicFactory.getInstance().addProperty("/dashboards/dashboard", c);
	
		for(Class c : extractMissing("org.magic.api.pictures.impl", "/pictures/picture"))
			 MagicFactory.getInstance().addProperty("/pictures/picture", c);
	
		for(Class c : extractMissing("org.magic.api.decksniffer.impl", "/decksniffer/sniffer"))
			 MagicFactory.getInstance().addProperty("/decksniffer/sniffer", c);
	
		for(Class c : extractMissing("org.magic.api.exports.impl", "/deckexports/export"))
			 MagicFactory.getInstance().addProperty("/deckexports/export", c);

		for(Class c : extractMissing("org.magic.api.servers.impl", "/servers/server"))
			 MagicFactory.getInstance().addProperty("/servers/server", c);
		
		return hasUpdated;
		
	 }
	 
	 
	private List<Class> extractMissing(String packages,String k) throws ClassNotFoundException, IOException {
	
		List<Class> retour = new ArrayList<Class>();
		for(Class c : getClasses(packages))
		{
			if(!c.isAnonymousClass())
			{
				String path = k+"[class='"+c.getName()+"']/class";
				String s = MagicFactory.getInstance().get(path);
				if(s==null)
				{
					hasUpdated=true;
					retour.add(c);
				}
			}
		}
		return retour;
	}

}
