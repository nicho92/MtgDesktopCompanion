package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.magic.api.interfaces.MTGWallpaperProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;

public abstract class AbstractWallpaperProvider implements MTGWallpaperProvider {

	private boolean enable;
	protected Properties props;
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	protected File confdir = new File(MTGConstants.CONF_DIR, "wallpapers");

	@Override
	public String toString() {
		return getName();
	}
	
	public AbstractWallpaperProvider() {
		props=new Properties();
		if(!confdir.exists())
			confdir.mkdir();
		load();
	}
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.DECKS;
	}
	
	@Override
	public Properties getProperties() {
		return props;
	}

	@Override
	public void setProperties(String k, Object value) {
		try{
		props.put(k, value);
		}
		catch(Exception e)
		{
			logger.error("Error set properties" + k+"="+value,e);
		}

	}

	@Override
	public String getProperty(String k) {
		return String.valueOf(props.get(k));
	}

	@Override
	public boolean isEnable() {
		return enable;
	}

	public void load()
	{
		File f=null;
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

	@Override
	public void enable(boolean t) {
		enable=t;
	}

}
