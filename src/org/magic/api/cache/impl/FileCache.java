package org.magic.api.cache.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGPicturesCache;
import org.magic.api.interfaces.abstracts.AbstractMTGPicturesCache;
import org.magic.services.MTGControler;

public class FileCache extends AbstractMTGPicturesCache {

	
	File directory ;
	
	public FileCache() {
		
		super();
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("DIRECTORY", MTGControler.CONF_DIR+"/cachePics");
			props.put("FORMAT", "png");
		save();
		}
	
		directory = new File(props.getProperty("DIRECTORY"));
		if(!directory.exists())
			directory.mkdir();
		
	}
	
	
	@Override
	public String getName() {
		return "File Cache";
	}

	@Override
	public BufferedImage getPic(MagicCard mc,MagicEdition ed){
		try {
			
			if(ed==null)
				ed=mc.getEditions().get(0);
	
			File save = new File(directory,ed.getId());
			if(!save.exists())
				save.mkdir();
			
			return ImageIO.read(new File(save,ed.getMultiverse_id()+"."+props.getProperty("FORMAT")));
		} catch (IOException e) {
			return null;
		}
	}


	@Override
	public void put(BufferedImage im, MagicCard mc,MagicEdition ed) throws IOException {
	
		if(ed==null)
			ed=mc.getEditions().get(0);
		
		File f = new File(directory,ed.getId());
		if(!f.exists())
			f.mkdir();
		
		ImageIO.write(im, props.getProperty("FORMAT"), new File(f,ed.getMultiverse_id()+"."+props.getProperty("FORMAT")));
		
	}

}
