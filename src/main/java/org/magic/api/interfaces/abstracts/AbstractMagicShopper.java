package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.magic.api.interfaces.MTGShopper;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;
import org.utils.patterns.observer.Observable;

public abstract class AbstractMagicShopper extends Observable implements MTGShopper {
	
	private boolean enable=true;
	protected Properties props;
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	protected File confdir = new File(MTGConstants.CONF_DIR, "shoppers");

	
	@Override
	public PLUGINS getType() {
		return PLUGINS.SHOPPER;
	}
	
	public AbstractMagicShopper() {
		props=new Properties();
		if(!confdir.exists())
			confdir.mkdir();
		load();
	}
	

	@Override
	public String getProperty(String k) {
		return String.valueOf(props.get(k));
	}

	public Properties getProperties() {
		return props;
	}
	
	public void setProperties(String k, Object value) {
		props.put(k,value);
	}
	
	public void load()
	{
		File f=null;
		try {
			f = new File(confdir, getName()+".conf");
			
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
		File f=null;
		try {
			f = new File(confdir, getName()+".conf");
		
			FileOutputStream fos = new FileOutputStream(f);
			props.store(fos,"");
			fos.close();
		} catch (Exception e) {
			logger.error("couln't save properties " + f,e);
		} 
	}
	
	public boolean isEnable() {
		return enable;
	}

	public void enable(boolean t) {
		this.enable=t;
		
	}
	
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
