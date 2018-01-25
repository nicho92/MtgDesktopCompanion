package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.magic.api.interfaces.MTGPicturesCache;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.utils.patterns.observer.Observable;

public abstract class AbstractMTGPicturesCache extends Observable implements MTGPicturesCache {

	protected Logger logger = MTGLogger.getLogger(this.getClass());
	public static File CONFDIR = new File(MTGControler.CONF_DIR, "caches");
	private boolean enable=true;
	protected Properties props;
	
	
	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.CACHE;
	}
	
	public AbstractMTGPicturesCache() {
		
		props=new Properties();
		if(!CONFDIR.exists())
			CONFDIR.mkdir();
		load();
	}
	
	@Override
	public void setProperties(String k, Object value) {
			props.put(k, value);
	}

	@Override
	public Object getProperty(String k) {
		return props.get(k);
	}

	
	
	@Override
	public Properties getProperties() {
		return props;
	}

	@Override
	public boolean isEnable() {
		return enable;
	}

	public void load()
	{
		File f=null;
		try {
			f = new File(CONFDIR, getName()+".conf");
			
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
			f = new File(CONFDIR, getName()+".conf");
		
			FileOutputStream fos = new FileOutputStream(f);
			props.store(fos,"");
			fos.close();
		} catch (Exception e) {
			logger.error("couln't save properties " + f,e);
		} 
	}

	@Override
	public void enable(boolean enabled) {
		this.enable=enabled;
	}
	
	

}
