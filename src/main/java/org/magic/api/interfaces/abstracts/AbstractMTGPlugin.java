package org.magic.api.interfaces.abstracts;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.magic.api.beans.technical.MTGDocumentation;
import org.magic.api.beans.technical.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.services.MTGConstants;
import org.magic.services.logging.MTGLogger;
import org.magic.services.providers.PluginsAliasesProvider;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.UITools;
import org.utils.patterns.observer.Observable;

public abstract class AbstractMTGPlugin extends Observable implements MTGPlugin {
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	private boolean enable;
	protected Properties props;
	private File confdir;
	private File confFile;

	protected static final String TRUE = "true";
	protected static final String FALSE = "false";
	private boolean loaded = false;

	protected PluginsAliasesProvider aliases = PluginsAliasesProvider.inst();
	
	
	@Override
	public boolean needAuthenticator() {
		return !listAuthenticationAttributes().isEmpty();
	}
	
	@Override
	public boolean isPartner()
	{
		return false;
	}

	@Override
	public List<String> listAuthenticationAttributes() {
		return new ArrayList<>();
	}


	@Override
	public ObjectName getObjectName() {
		try {
			return new ObjectName("org.magic.api:type="+getType().name().toLowerCase()+",name="+getName());
		} catch (MalformedObjectNameException e) {
			return null;
		}
	}

	@Override
	public MTGDocumentation getDocumentation() {
		try {
			return new MTGDocumentation(URI.create(MTGConstants.MTG_DESKTOP_WIKI_RAW_URL+"/plugins/"+getType()+"-"+getName().replace(" ", "_")+".md").toURL(),FORMAT_NOTIFICATION.MARKDOWN);
		}
		catch(Exception e)
		{
			return null;
		}
	}

	@Override
	public boolean isLoaded() {
		return loaded;
	}

	public void notify(Object obj) {
		setChanged();
		notifyObservers(obj);
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	protected AbstractMTGPlugin() {
		props = new Properties();
		load();


		confdir = new File(MTGConstants.CONF_DIR, getType().name().toLowerCase());
		if (!confdir.exists())
			confdir.mkdir();
		load();

		if (!getName().isEmpty() && !new File(confdir, getName() + ".conf").exists()) {
			initDefault();
			save();

		}

	}

	public String getProperty(String k, String defaultVal) {

		if(props.getProperty(k)==null || props.getProperty(k).isEmpty())
			return defaultVal;


		return props.getProperty(k, defaultVal);
	}

	@Override
	public File getConfFile() {
		return confFile;
	}

	public File getConfdir() {
		return confdir;
	}

	@Override
	public void load() {
			confFile = new File(confdir, getName() + ".conf");
			if (confFile.exists())
			{
				try {
					FileTools.loadProperties(confFile, props);
				} catch (IOException e) {
					logger.error("error loading file {} : {}",confFile, e);
				}
			}
		loaded=true;
	}

	@Override
	public void save() {
		try {
			FileTools.saveProperties(confFile, props);
		} catch (Exception e) {
			logger.error("error writing file {} : {}",confFile, e.getMessage());
		}
	}

	@Override
	public Properties getProperties() {
		return props;
	}

	@Override
	public void setProperty(String k, Object value) {
		logger.debug("setProperty {}={}",k,value);
		if (value == null)
			value = "";

		props.put(k, value);
		save();
	}

	public Integer getInt(String k) {
		try {
			return Integer.parseInt(getString(k));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public Long getLong(String k) {
		try {
			return Long.parseLong(getString(k));
		} catch (NumberFormatException e) {
			return null;
		}
	}


	public double getDouble(String k) {
		return UITools.parseDouble(getString(k));
	}

	public boolean getBoolean(String k) {
		if(StringUtils.isEmpty(getString(k)))
			return false;


		return getString(k).equalsIgnoreCase("true")||getString(k).equalsIgnoreCase("yes");
	}

	public String[] getArray(String k) {
		return getString(k).split(",");
	}

	public File getFile(String k) {
		return new File(getString(k));
	}

	@Override
	public String getString(String k) {

		if (props.getProperty(k) == null) {
			logger.error("{} is not found in {}",k,getName());
			props.put(k, getDefaultAttributes().get(k).getDefaultValue());
			save();
			load();
		}

		return getProperty(k, "").trim();
	}
	
	
	public char getChar(String k)
	{
		return getString(k).charAt(0);
	}

	@Override
	public boolean isEnable() {
		if(isPartner())
			return true;

		return enable;
	}

	@Override
	public void enable(boolean t) {
		this.enable = t;

	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		return this.hashCode() == obj.hashCode();
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public STATUT getStatut() {
		return STATUT.STABLE;
	}

	protected void initDefault() {
		getDefaultAttributes().entrySet().forEach(e->setProperty(e.getKey(), e.getValue().getDefaultValue()));
	}


	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return new HashMap<>();
	}



	@Override
	public void unload() {
		logger.trace("Unloading {}",getName());

	}

	@Override
	public Icon getIcon() {
		try {
			return new ImageIcon(new ImageIcon(AbstractMTGPlugin.class.getResource("/icons/plugins/"+getName().toLowerCase()+".png")).getImage().getScaledInstance(MTGConstants.MENU_ICON_SIZE, MTGConstants.MENU_ICON_SIZE, Image.SCALE_SMOOTH));
		}
		catch(Exception e)
		{
			return MTGConstants.ICON_DEFAULT_PLUGIN;
		}
	}

	@Override
	public String termsAndCondition() {
		return "Copyright" + '\u00A9' + " "+ Calendar.getInstance().get(Calendar.YEAR) + ", All data are property of "+ getName();

	}


}
