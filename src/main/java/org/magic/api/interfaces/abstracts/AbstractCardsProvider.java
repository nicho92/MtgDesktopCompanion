package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;

public abstract class AbstractCardsProvider implements MTGCardsProvider {

	protected Logger logger = MTGLogger.getLogger(this.getClass());
	protected boolean enable;
	protected Properties props;
	protected File confdir = new File(MTGConstants.CONF_DIR, "cardsProviders");

	
	public AbstractCardsProvider() {
		props=new Properties();

		if(!confdir.exists())
			confdir.mkdir();
		load();
	}

	@Override
	public Properties getProperties() {
		return props;
	}

	@Override
	public void setProperties(String k, Object value) {
		props.put(k,value);
	}

	@Override
	public String getProperty(String k) {
		return String.valueOf(props.get(k));
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
	public PLUGINS getType() {
		return PLUGINS.PROVIDER;
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
