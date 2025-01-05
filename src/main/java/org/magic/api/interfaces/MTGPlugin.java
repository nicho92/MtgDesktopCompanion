package org.magic.api.interfaces;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.management.ObjectName;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.technical.AccountAuthenticator;
import org.magic.api.beans.technical.MTGDocumentation;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.extra.MTGIconable;
import org.magic.services.AccountsManager;
import org.magic.services.logging.MTGLogger;
import org.utils.patterns.observer.Observer;

import com.google.gson.JsonObject;

public interface MTGPlugin extends Comparable<MTGPlugin>, MTGIconable {


	Logger loggerMain = MTGLogger.getLogger(MTGPlugin.class);


	public enum PLUGINS {
		PROVIDER, DASHBOARD, PRICER, SERVER, EXPORT, DECKSNIFFER, DAO, TOKEN, CACHE, NEWS, WALLPAPER, NOTIFIER,DASHLET,COMMAND,EDITOR, INDEXER,GENERATOR, SCRIPT, POOL,COMBO, GRADING, GED, STRATEGY, PICTURE, SHOPPER, TRACKING, EXTERNAL_SHOP, IA, SEALED, NETWORK;
	}

	public enum STATUT {
		DEV, BETA, STABLE, DEPRECATED, BUGGED
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

	public ObjectName getObjectName();

	public String termsAndCondition();

	public MTGDocumentation getDocumentation();

	public boolean isPartner();

	public Map<String,MTGProperty> getDefaultAttributes();

	public List<String> listAuthenticationAttributes();

	default AccountAuthenticator getAuthenticator() {
			return AccountsManager.inst().getAuthenticator(this);
	}


	default String getId() {
		return getType()+getName();
	}

	@Override
	default int compareTo(MTGPlugin o) {

		if(o==null)
			return -1;

		return getId().compareTo(o.getId());
	}
	
	public default JsonObject toJson()
	{
		var obj = new JsonObject();
		obj.addProperty("name", getName());
		obj.addProperty("type", getType().toString());
		obj.addProperty("enabled", isEnable());
		obj.addProperty("version", getVersion());
		obj.addProperty("status", getStatut().name());
		obj.addProperty("id", getId());
		obj.addProperty("loaded", isLoaded());
		return obj;

	}

	public boolean needAuthenticator();
}
