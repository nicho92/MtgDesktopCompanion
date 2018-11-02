package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;
import org.utils.patterns.observer.Observable;

public abstract class AbstractMTGPlugin extends Observable implements MTGPlugin {
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	private boolean enable;
	protected Properties props;
	protected File confdir;
	protected File confFile;

	public void setProps(Properties props) {
		this.props = props;
	}
	
	@Override
	public ObjectName getObjectName() {
		try {
			return new ObjectName("org.magic.api:type="+getType().name().toLowerCase()+",name="+getName());
		} catch (MalformedObjectNameException e) {
			return null;
		}
	}
	

	public void notify(Object obj) {
		setChanged();
		notifyObservers(obj);
	}
	
	@Override
	public String getVersion() {
		return "1.0";
	}

	public AbstractMTGPlugin() {
		props = new Properties();
		load();
	}

	public String getProperty(String k, String defaultVal) {
		return props.getProperty(k, defaultVal);
	}

	public File getConfFile() {
		return confFile;
	}

	public File getConfdir() {
		return confdir;
	}

	public void load() {
		try {
			confFile = new File(confdir, getName() + ".conf");
			if (confFile.exists()) {
				FileInputStream fis = new FileInputStream(confFile);
				props.load(fis);
				fis.close();
			}
		} catch (Exception e) {
			logger.error("couln't load properties " + confFile, e);
		}
	}

	public void save() {
		try {
			FileOutputStream fos = new FileOutputStream(confFile);
			props.store(fos, "");
			fos.close();
		} catch (Exception e) {
			logger.error("error writing file " + confFile, e);
		}
	}

	@Override
	public Properties getProperties() {
		return props;
	}

	@Override
	public void setProperty(String k, Object value) {
		logger.debug("setProperty " + k + "="+value);
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
		return Double.parseDouble(getString(k));
	}

	public boolean getBoolean(String k) {
		if(getString(k)==null)
			return false;
			
		
		return getString(k).equalsIgnoreCase("true");
	}

	public String[] getArray(String k) {
		return getString(k).split(",");
	}

	
	public URL getURL(String k)
	{
		try {
			return new URL(props.getProperty(k));
		} catch (MalformedURLException e) {
			logger.error(e);
		}
		return null;
	}
	
	public File getFile(String k) {
		return new File(getString(k));
	}

	@Override
	public String getString(String k) {

		if (props.getProperty(k) == null) {
			logger.error(k + " is not found in " + getName());
			props.put(k, "");
			save();
			load();
		}

		return getProperty(k, "").trim();
	}

	@Override
	public boolean isEnable() {
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

	public STATUT getStatut() {
		return STATUT.STABLE;
	}

	@Override
	public void initDefault() {
		// do nothing
		
	}
	
	@Override
	public Icon getIcon() {
		try {
			return new ImageIcon(AbstractMTGPlugin.class.getResource("/icons/plugins/"+getName().toLowerCase()+".png"));
		}
		catch(Exception e)
		{
			return MTGConstants.ICON_DEFAULT_PLUGIN;
		}
	}
	
}
