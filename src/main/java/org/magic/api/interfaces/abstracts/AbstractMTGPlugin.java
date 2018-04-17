package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.services.MTGLogger;
import org.utils.patterns.observer.Observable;

public abstract class AbstractMTGPlugin extends Observable implements MTGPlugin{
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	private boolean enable;
	private Properties props;
	protected File confdir;
	protected File confFile;
	
	public void setProps(Properties props) {
		this.props = props;
	}
	
	public AbstractMTGPlugin() {
		props=new Properties();
		load();
	}
	
	public String getProperty(String k , String defaultVal)
	{
		return props.getProperty(k, defaultVal);
	}
	
	public File getConfFile() {
		return confFile;
	}
	
	public File getConfdir() {
		return confdir;
	}
	
	public void load()
	{
		try {
			confFile = new File(confdir, getName()+".conf");
			if(confFile.exists())
			{	
				FileInputStream fis = new FileInputStream(confFile);
				props.load(fis);
				fis.close();
			}
		} catch (Exception e) {
			logger.error("couln't load properties " + confFile,e);
		} 
	}
	
	public void save()
	{
		try {
			FileOutputStream fos = new FileOutputStream(confFile);
			props.store(fos,"");
			fos.close();
		} catch (Exception e) {
			logger.error("error writing file " + confFile,e);
		} 
	}
	
	@Override
	public Properties getProperties() {
		return props;
	}

	@Override
	public void setProperty(String k, Object value) {
		
		if(value==null)
			value="";
		
		props.put(k,value);
	}
	
	public int getInt(String k)
	{
		return Integer.parseInt(getString(k));
	}
	
	public double getDouble(String k)
	{
		return Double.parseDouble(getString(k));
	}
	
	public boolean getBoolean(String k)
	{
		return getString(k).equalsIgnoreCase("true");
	}
	
	public String[] getArray(String k)
	{
		return getString(k).split(",");
	}
	
	public File getFile(String k)
	{
		return new File(getString(k));
	}
	

	@Override
	public String getString(String k) {
		
		if(props.getProperty(k)==null)
		{
			logger.error(k + " is not found in "+ getName());
			props.put(k, "");
			save();
			load();
		}
		
		return getProperty(k,"");
	}

	

	@Override
	public boolean isEnable() {
		return enable;
	}

	@Override
	public void enable(boolean t) {
		this.enable=t;
		
	}
	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null)
			return false;
		
		return this.hashCode()==obj.hashCode();
	}
	
	@Override
	public String toString() {
		return getName();
	}


}
