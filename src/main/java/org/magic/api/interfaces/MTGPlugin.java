package org.magic.api.interfaces;

import java.io.File;
import java.util.Properties;

import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.utils.patterns.observer.Observer;

public interface MTGPlugin {

	public Properties getProperties();
	public void setProperty(String k,Object value);
	public String getString(String k);
	public boolean isEnable();
	public void save();
	public void load();
	public void enable(boolean t);
	public String getName();
	public STATUT getStatut();
	public PLUGINS getType();
	public enum PLUGINS  { PROVIDER,DASHBOARD,PRICER,SERVER,PICTURES,SHOPPER,EXPORT,DECKS,DAO,TOKENS,CACHE, NEWS, WALLPAPER}
	public File getConfFile();
	public void initDefault();
	public String getVersion();
	public void addObserver(Observer o);
}
