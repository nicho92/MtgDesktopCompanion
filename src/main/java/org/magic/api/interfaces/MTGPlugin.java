package org.magic.api.interfaces;

import java.util.Properties;

import org.magic.api.interfaces.MTGCardsProvider.STATUT;

public interface MTGPlugin {

	public Properties getProperties();
	public void setProperties(String k,Object value);
	public String getProperty(String k);
	public boolean isEnable();
	public void save();
	public void load();
	public void enable(boolean t);
	public String getName();
	public STATUT getStatut();
	public PLUGINS getType();
	public enum PLUGINS  { PROVIDER,DASHBOARD,PRICER,SERVER,PICTURES,SHOPPER,EXPORT,DECKS,DAO,TOKENS,CACHE, NEWS}
}
