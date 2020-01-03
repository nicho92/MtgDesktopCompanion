package org.magic.api.interfaces;

import java.io.File;
import java.util.Properties;

import javax.management.ObjectName;
import javax.swing.Icon;

import org.magic.api.beans.MTGDocumentation;
import org.utils.patterns.observer.Observer;

public interface MTGPlugin {
	
	public enum PLUGINS {
		PROVIDER, DASHBOARD, PRICER, SERVER, PICTURES, SHOPPER, EXPORT, DECKS, DAO, TOKENS, CACHE, NEWS, WALLPAPER, NOTIFIER,DASHLET,COMMAND,EDITOR, INDEXER,GENERATOR, SCRIPT, POOL,COMBO;
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
	
	public void unload();

	public void enable(boolean t);

	public String getName();

	public STATUT getStatut();

	public PLUGINS getType();

	public File getConfFile();

	public void initDefault();

	public String getVersion();

	public void addObserver(Observer o);

	public void removeObservers();

	public void removeObserver(Observer o);
	
	public Icon getIcon() ;
	
	public ObjectName getObjectName();

	public String termsAndCondition();
	
	public MTGDocumentation getDocumentation();
	
}
