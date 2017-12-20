package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.DashBoard;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public abstract class AbstractDashBoard extends Observable implements DashBoard {

	protected Logger logger = MTGLogger.getLogger(this.getClass());

	
	public static enum FORMAT { standard,legacy,vintage,modern};
	public static enum ONLINE_PAPER {online, paper};

	
	public abstract List<CardShake> getShakerFor(String gameFormat) throws IOException;
	public abstract List<CardShake> getShakeForEdition(MagicEdition edition) throws IOException;
	public abstract Map<Date,Double> getPriceVariation(MagicCard mc,MagicEdition me) throws IOException;
	
	public abstract String getName();
	public abstract Date getUpdatedDate();

	protected File confdir = new File(MTGControler.CONF_DIR, "dashboards");

	
	private boolean enable=true;
	protected Properties props;
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.DASHBOARD;
	}
	
	@Override
	public String toString() {
		return getName();
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
	
	
	public AbstractDashBoard() {
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
