package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.magic.api.interfaces.MagicTokensProvider;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public abstract class AbstractTokensProvider implements MagicTokensProvider {

	protected Properties props;
	private boolean enable;
	protected File confdir = new File(MTGControler.CONF_DIR, "tokens");
	protected Logger logger = MTGLogger.getLogger(this.getClass());

	
	
	public AbstractTokensProvider() {
		props=new Properties();
		if(!confdir.exists())
			confdir.mkdir();
		load();
	}
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.TOKENS;
	}
	
	@Override
	public Properties getProperties() {
		return props;
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
	public boolean isEnable() {
		return enable; 
	}

	@Override
	public void save() {
		try {
			File f = new File(confdir, getName()+".conf");
		
			FileOutputStream fos = new FileOutputStream(f);
			props.store(fos,"");
			fos.close();
		} catch (Exception e) {
			MTGLogger.printStackTrace(e);
		} 

	}

	@Override
	public void load() {
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
			MTGLogger.printStackTrace(e);
		} 

	}

	@Override
	public void enable(boolean t) {
		enable=t;
	}


}
