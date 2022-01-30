package org.magic.api.interfaces;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.management.ObjectName;
import javax.swing.Icon;

import org.apache.log4j.Logger;
import org.magic.api.beans.AccountAuthenticator;
import org.magic.api.beans.MTGDocumentation;
import org.magic.services.AccountsManager;
import org.magic.services.MTGLogger;
import org.utils.patterns.observer.Observer;

public interface MTGPlugin extends Comparable<MTGPlugin> {
	
	
	Logger loggerMain = MTGLogger.getLogger(MTGPlugin.class);

	
	public enum PLUGINS {
		PROVIDER, DASHBOARD, PRICER, SERVER, HOPPER, EXPORT, DECKSNIFFER, DAO, TOKEN, CACHE, NEWS, WALLPAPER, NOTIFIER,DASHLET,COMMAND,EDITOR, INDEXER,GENERATOR, SCRIPT, POOL,COMBO, GRADING, GED, STRATEGY, PICTURE, SHOPPER, TRACKING, EXTERNAL_SHOP;
	}

	public enum STATUT {
		DEV, BETA, STABLE, DEPRECATED,BUGGED
	}
	
	public Properties getProperties();

	public void setProperty(String k, Object value);

	public String getString(String k);
	
	public boolean isEnable();

	public void save();

	public void load();
	
	public boolean isLoaded();
	
	public void unload();

	public void enable(boolean t);

	public String getName();

	public STATUT getStatut();

	public PLUGINS getType();

	public File getConfFile();

	public String getVersion();

	public void addObserver(Observer o);

	public void removeObservers();

	public void removeObserver(Observer o);
	
	public List<Observer> listObservers();
	
	public Icon getIcon() ;
	
	public ObjectName getObjectName();

	public String termsAndCondition();
	
	public MTGDocumentation getDocumentation();
	
	public boolean isPartner();
	
	public Map<String,String> getDefaultAttributes();
	
	public List<String> listAuthenticationAttributes();
		
	default AccountAuthenticator getAuthenticator() {
			return AccountsManager.inst().getAuthenticator(this);
	}
	
	
	@Override
	default int compareTo(MTGPlugin o) {
		
		if(o==null)
			return -1;
		
		return getName().compareTo(o.getName());
	}
}
