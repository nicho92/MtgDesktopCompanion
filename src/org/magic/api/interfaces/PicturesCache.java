package org.magic.api.interfaces;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.magic.api.beans.MagicCard;

public interface PicturesCache {

	public String getName();
	
	public BufferedImage getPic(MagicCard mc);
	
	public void put(BufferedImage im,MagicCard mc) throws Exception;
	
}
