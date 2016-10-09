package org.magic.api.cache;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.PicturesCache;
import org.magic.services.MagicFactory;

public class FileCache implements PicturesCache {

	
	File directory ;
	
	public FileCache() {
		directory = new File(MagicFactory.CONF_DIR,"cache");
		if(!directory.exists())
			directory.mkdir();
		
		directory = new File(directory,MagicFactory.getInstance().getEnabledProviders().toString());
		if(!directory.exists())
			directory.mkdir();
	
		
		
		
	}
	
	
	@Override
	public String getName() {
		return "Memory Cache";
	}

	@Override
	public BufferedImage getPic(MagicCard mc){
		try {
			
			File f = new File(directory,mc.getEditions().get(0).getId());
			if(!directory.exists())
				directory.mkdir();
			
			return ImageIO.read(new File(directory,mc.getId()+".jpg"));
		} catch (IOException e) {
			return null;
		}
	}


	@Override
	public void put(BufferedImage im, MagicCard mc) throws IOException {
	
		File f = new File(directory,mc.getEditions().get(0).getId());
		if(!directory.exists())
			directory.mkdir();
		
		ImageIO.write(im, "jpg", new File(directory,mc.getId()+".jpg"));
		
	}

}
