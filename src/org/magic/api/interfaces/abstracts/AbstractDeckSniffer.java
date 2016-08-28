package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Observable;
import java.util.Properties;

import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.interfaces.DeckSniffer;
import org.magic.services.MagicFactory;

public abstract class AbstractDeckSniffer extends Observable implements DeckSniffer {

	private boolean enable;
	protected Properties props;

	protected File confdir = new File(MagicFactory.CONF_DIR, "decksniffers");

	
	public AbstractDeckSniffer() {
		props=new Properties();
		if(!confdir.exists())
			confdir.mkdir();
		load();
	}
	
	
	@Override
	public abstract String[] listFilter() ;

	@Override
	public abstract MagicDeck getDeck(RetrievableDeck info) throws Exception ;

	@Override
	public abstract List<RetrievableDeck> getDeckList() throws Exception ;

	@Override
	public abstract void connect() throws Exception ;
	
	
	@Override
	public Properties getProperties() {
		return props;
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
	public abstract String getName() ;

	@Override
	public boolean isEnable() {
		return enable;
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

	@Override
	public void enable(boolean t) {
		enable=t;
	}

}
