package org.magic.api.cache;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
		
	}
	
	
	@Override
	public String getName() {
		return "File Cache";
	}

	@Override
	public BufferedImage getPic(MagicCard mc){
		try {
			
			File save = new File(directory,mc.getEditions().get(0).getId());
			if(!save.exists())
				save.mkdir();
			
			return ImageIO.read(new File(save,mc.getId()+".jpg"));
		} catch (IOException e) {
			return null;
		}
	}


	@Override
	public void put(BufferedImage im, MagicCard mc) throws IOException {
	
		File f = new File(directory,mc.getEditions().get(0).getId());
		if(!f.exists())
			f.mkdir();
		
		ImageIO.write(im, "jpg", new File(f,mc.getId()+".jpg"));
		
	}

}
