package org.magic.api.interfaces.abstracts;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Observable;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.magic.api.interfaces.PictureProvider;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

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
				return ImageIO.read(AbstractPicturesProvider.class.getResource("/icons/back.jpg"));
			} catch (IOException e) {
				logger.error("Error reading back picture ",e);
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
	
	
	public BufferedImage resizeCard(BufferedImage img) {  
	    int newW = Integer.parseInt(props.getProperty("CARD_SIZE_WIDTH"));
	    int newH = Integer.parseInt(props.getProperty("CARD_SIZE_HEIGHT"));
	    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	}  

	@Override
	public void enable(boolean enabled) {
		this.enable=enabled;
	}
	
	

}
