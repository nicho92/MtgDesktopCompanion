package org.magic.api.interfaces.abstracts;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Observable;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.PictureProvider;
import org.magic.gui.MagicGUI;
import org.magic.services.MTGControler;

import com.itextpdf.awt.geom.Dimension;

public abstract class AbstractPicturesProvider extends Observable implements PictureProvider {

	
	protected File confdir = new File(MTGControler.CONF_DIR, "pictures");
	private boolean enable=true;
	protected Properties props;
	
	
	@Override
	public String toString() {
		return getName();
	}
	
	public AbstractPicturesProvider() {
		
		props=new Properties();
		if(!confdir.exists())
			confdir.mkdir();
		load();
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
	public Properties getProperties() {
		return props;
	}

	@Override
	public boolean isEnable() {
		return enable;
	}
	
	@Override
	public BufferedImage getBackPicture() throws Exception {
			try {
				return ImageIO.read(AbstractPicturesProvider.class.getResource("/res/back.jpg"));
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} 
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
