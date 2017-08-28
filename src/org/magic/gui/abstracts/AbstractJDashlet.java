package org.magic.gui.abstracts;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.swing.JInternalFrame;

import org.magic.services.MTGControler;

public abstract class AbstractJDashlet extends JInternalFrame  {

	protected File confdir = new File(MTGControler.CONF_DIR, "dashboards/dashlets");
	protected Properties props;
	
	
	public AbstractJDashlet() {
		props=new Properties();
		if(!confdir.exists())
			confdir.mkdir();
		load();
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
	
	
	
	public abstract String getName();
	
	public abstract void save(String k , Object value);
	
	public abstract void init();

	public abstract boolean isStartup();
	
	
	@Override
	public String toString() {
		return getName();
	}
}
