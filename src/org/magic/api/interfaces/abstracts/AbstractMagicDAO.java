package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.ShopItem;
import org.magic.api.interfaces.MagicDAO;

public abstract class AbstractMagicDAO implements MagicDAO {

	
	private boolean enable=true;
	protected Properties props;
	protected File confdir = new File(System.getProperty("user.home")+"/magicDeskCompanion/");

	
	public AbstractMagicDAO() {
		props=new Properties();
		load();
	}
	
	
	@Override
	public Properties getProperties() {
		return props;
	}

	@Override
	public boolean isEnable() {
		return enable;
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
