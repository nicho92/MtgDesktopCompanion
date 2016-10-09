package org.magic.api.interfaces;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.pictures.impl.MagicCardInfoPicturesProvider;

public interface PicturesCache {

	static final Logger logger = LogManager.getLogger(PicturesCache.class.getName());

	
	public String getName();
	
	public BufferedImage getPic(MagicCard mc);
	
	public void put(BufferedImage im,MagicCard mc) throws Exception;
	
}
