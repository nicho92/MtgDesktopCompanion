package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Observable;
import java.util.Properties;

import javax.swing.Icon;

import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.CardExporter;
import org.magic.services.MagicFactory;

public abstract class AbstractCardExport extends Observable implements CardExporter {

	public abstract String getFileExtension();
	public abstract void export(MagicDeck deck, File dest) throws IOException ;
	public abstract String getName() ;
	public abstract Icon getIcon() ;
	
	private boolean enable;
	protected Properties props;

	protected File confdir = new File(MagicFactory.CONF_DIR, "exports");
	
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



}
