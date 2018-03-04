package org.magic.api.interfaces.abstracts;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;
import org.magic.tools.ImageUtils;
import org.utils.patterns.observer.Observable;

public abstract class AbstractPicturesProvider extends AbstractMTGPlugin implements MTGPictureProvider {

	protected int newW;
	protected int newH;
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.PICTURES;
	}
	
	public AbstractPicturesProvider() {
		confdir = new File(MTGConstants.CONF_DIR, "pictures");
		props=new Properties();
		if(!confdir.exists())
			confdir.mkdir();
		load();
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

	
	public BufferedImage resizeCard(BufferedImage img,int newW, int newH) {  
	    return ImageUtils.resize(img, newH, newW);
	}  

}
