package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MagicPricesProvider;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.utils.patterns.observer.Observable;

public abstract class AbstractMagicPricesProvider extends Observable implements MagicPricesProvider {

	private boolean enable=true;
	protected Properties props;
	protected File confdir = new File(MTGControler.CONF_DIR, "pricers");
	protected Logger logger = MTGLogger.getLogger(this.getClass());

	@Override
	public PLUGINS getType() {
		return PLUGINS.PRICER;
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
	
	
	public AbstractMagicPricesProvider() {
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
	public Object getProperty(String k) {
		return props.get(k);
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
