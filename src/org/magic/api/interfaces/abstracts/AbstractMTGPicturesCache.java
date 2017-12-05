package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Observable;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.magic.api.interfaces.MTGPicturesCache;
import org.magic.services.MTGControler;
import org.magic.tools.MTGLogger;

public abstract class AbstractMTGPicturesCache extends Observable implements MTGPicturesCache {

	protected Logger logger = MTGLogger.getLogger(this.getClass());
	public static File confdir = new File(MTGControler.CONF_DIR, "caches");
	private boolean enable=true;
	protected Properties props;
	
	
	@Override
	public String toString() {
		return getName();
	}
	
	public AbstractMTGPicturesCache() {
		
		props=new Properties();
		if(!confdir.exists())
			confdir.mkdir();
		load();
	}
	
	@Override
	public void setProperties(String k, Object value) {
		try{
			props.put(k, value);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		
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
		try {
			File f = new File(confdir, getName()+".conf");
			
			if(f.exists())
			{	
				FileInputStream fis = new FileInputStream(f);
				props.load(fis);
				fis.close();
			}
			else
			{
				//save();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void save()
	{
		try {
			File f = new File(confdir, getName()+".conf");
		
			FileOutputStream fos = new FileOutputStream(f);
			props.store(fos,"");
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	@Override
	public void enable(boolean enabled) {
		this.enable=enabled;
	}
	
	

}
