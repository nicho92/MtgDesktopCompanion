package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Observable;
import java.util.Properties;

import org.magic.api.interfaces.MTGServer;
import org.magic.services.MTGControler;

public abstract class AbstractMTGServer extends Observable implements MTGServer {

	private boolean enable;
	protected Properties props;
	protected File confdir = new File(MTGControler.CONF_DIR, "servers");
	
	
	public AbstractMTGServer() {
		props=new Properties();
		if(!confdir.exists())
			confdir.mkdir();
		load();
	}
	
	
	@Override
	public abstract String description() ;
	
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public Properties getProperties() {
		return props;
	}

	@Override
	public boolean isEnable() {
		return enable;
	}

	@Override
	public void setProperties(String k, Object value) {
		props.put(k, value);
		
	}

	@Override
	public Object getProperty(String k) {
		return props.get(k);
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
