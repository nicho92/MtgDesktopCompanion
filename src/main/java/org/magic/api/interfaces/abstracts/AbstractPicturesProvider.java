package org.magic.api.interfaces.abstracts;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.magic.api.interfaces.PictureProvider;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.tools.ImageUtils;
import org.utils.patterns.observer.Observable;

public abstract class AbstractPicturesProvider extends Observable implements PictureProvider {

	
	protected File confdir = new File(MTGControler.CONF_DIR, "pictures");
	private boolean enable=true;
	protected Properties props;
	protected Logger logger = MTGLogger.getLogger(this.getClass());

	
	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.PICTURES;
	}
	
	public AbstractPicturesProvider() {
		
		props=new Properties();
		if(!confdir.exists())
			confdir.mkdir();
		load();
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
	public Properties getProperties() {
		return props;
	}

	@Override
	public boolean isEnable() {
		return enable;
	}
	
	@Override
	public BufferedImage getBackPicture(){
			try {
				return ImageIO.read(AbstractPicturesProvider.class.getResource("/icons/back.jpg"));
			} catch (IOException e) {
				logger.error("Error reading back picture ",e);
				return null;
			} 
	}
	
	public void load()
	{
		File f=null;
		try {
			f = new File(confdir, getName()+".conf");
			
			if(f.exists())
			{	
				FileInputStream fis = new FileInputStream(f);
				props.load(fis);
				fis.close();
			}
		} catch (Exception e) {
			logger.error("couln't load properties " + f,e);
		} 
	}
	
	public void save()
	{
		File f=null;
		try {
			f = new File(confdir, getName()+".conf");
		
			FileOutputStream fos = new FileOutputStream(f);
			props.store(fos,"");
			fos.close();
		} catch (Exception e) {
			logger.error("couln't save properties " + f,e);
		} 
	}
	
	
	public BufferedImage resizeCard(BufferedImage img) {  
	    int newW = Integer.parseInt(props.getProperty("CARD_SIZE_WIDTH"));
	    int newH = Integer.parseInt(props.getProperty("CARD_SIZE_HEIGHT"));
	    return ImageUtils.resize(img, newH, newW);
	}  

	@Override
	public void enable(boolean enabled) {
		this.enable=enabled;
	}
	
	

}
