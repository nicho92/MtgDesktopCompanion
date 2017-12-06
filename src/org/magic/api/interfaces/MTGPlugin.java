package org.magic.api.interfaces;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.magic.api.interfaces.MagicCardsProvider.STATUT;
import org.magic.services.MTGLogger;

public interface MTGPlugin {

	public Properties getProperties();
	public void setProperties(String k,Object value);
	public Object getProperty(String k);
	public boolean isEnable();
	public void save();
	public void load();
	public void enable(boolean t);
	public String getName();
	public STATUT getStatut();
	
}
