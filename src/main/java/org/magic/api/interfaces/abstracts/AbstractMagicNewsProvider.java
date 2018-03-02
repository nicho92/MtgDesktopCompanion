package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.magic.api.interfaces.MTGNewsProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;
import org.utils.patterns.observer.Observable;

public abstract class AbstractMagicNewsProvider extends Observable implements MTGNewsProvider {

	protected Logger logger = MTGLogger.getLogger(this.getClass());
	protected File confdir = new File(MTGConstants.CONF_DIR, "news");
	private boolean enable=true;
	protected Properties props;
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.NEWS;
	}
	
	@Override
	public String toString() {
		return getName();
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
	
	
	public AbstractMagicNewsProvider() {
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

	@Override
	public void setProperties(String k, Object value) {
		props.put(k,value);
	}

	@Override
	public boolean isEnable() {
		return enable;
	}

	@Override
	public void enable(boolean t) {
		this.enable=t;
		
	}
	
}
