package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.magic.api.beans.ShopItem;
import org.magic.api.interfaces.MagicShopper;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.utils.patterns.observer.Observable;

public abstract class AbstractMagicShopper extends Observable implements MagicShopper {

	
	
	
	
	private boolean enable=true;
	protected Properties props;
	protected Logger logger = MTGLogger.getLogger(this.getClass());

	
	public abstract List<ShopItem> search(String search);
	
	protected File confdir = new File(MTGControler.CONF_DIR, "shoppers");

	
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
	public Object getProperty(String k) {
		return props.get(k);
	}

	public Properties getProperties() {
		return props;
	}
	
	public void setProperties(String k, Object value) {
		props.put(k,value);
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
		return this.hashCode()==obj.hashCode();
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
