package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Properties;

import javax.swing.Icon;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.CardExporter;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public abstract class AbstractCardExport extends Observable implements CardExporter {
	protected Logger logger = MTGLogger.getLogger(this.getClass());

	public abstract String getFileExtension();
	public abstract void export(MagicDeck deck, File dest) throws Exception ;
	public abstract String getName() ;
	public abstract Icon getIcon() ;
	
	private boolean enable;
	protected Properties props;

	protected File confdir = new File(MTGControler.CONF_DIR, "exports");
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.EXPORT;
	}
	
	
	public void load()
	{
		try {
			File f = new File(confdir,getName()+".conf");
			
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
	
	
	public AbstractCardExport() {
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
		return this.hashCode()==obj.hashCode();
	}
	
	@Override
	public String toString() {
		return getName();
	}


	
	protected List<MagicCardStock> importFromDeck(MagicDeck deck)
	{
		List<MagicCardStock> mcs = new ArrayList<MagicCardStock>();
		
		for(MagicCard mc : deck.getMap().keySet())
		{
			MagicCardStock stock = new MagicCardStock();
				stock.setMagicCard(mc);
				stock.setQte(deck.getMap().get(mc));
				stock.setComment("import from " + deck.getName());
				stock.setIdstock(-1);
				stock.setUpdate(true);
				mcs.add(stock);
		}
		return mcs;
	}

}
