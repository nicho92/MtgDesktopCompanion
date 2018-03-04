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
	}
	
	public String getProperty(String k , String defaultVal)
	{
		if(getProperty(k)!=null)
			return getProperty(k);
		else
			return defaultVal;
	}
	
	public File getConfFile() {
		return confFile;
	}
	
	public File getConfdir() {
		return confdir;
	}
	
	public void load()
	{
		File f =null;
		try {
			f = new File(confdir,getName()+".conf");
			if(f.exists())
			{	
				FileInputStream fis = new FileInputStream(f);
				props.load(fis);
				fis.close();
			}
		} catch (Exception e) {
			logger.error("couln't load properties " + f,e);
		} 
	}
	
	public void save()
	{
		File f = null;
		try {
			f = new File(confdir, getName()+".conf");
		
			FileOutputStream fos = new FileOutputStream(f);
			props.store(fos,"");
			fos.close();
		} catch (Exception e) {
			logger.error("error writing file " + f,e);
		} 
	}
	
	@Override
	public Properties getProperties() {
		return props;
	}

	@Override
	public void setProperty(String k, Object value) {
		props.put(k,value);
	}

	@Override
	public String getProperty(String k) {
		return String.valueOf(props.get(k));
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
