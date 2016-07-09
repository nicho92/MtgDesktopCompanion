package org.magic.api.interfaces;

import java.awt.image.BufferedImage;
import java.net.URL;

import org.magic.api.beans.MagicCard;

public interface PictureProvider {

	
	public BufferedImage getPicture(MagicCard mc) throws Exception;
	public URL getPictureURL(String id) throws Exception;
	public BufferedImage getPicture(String setID,String rarity) throws Exception;
	public BufferedImage getPicture(String multiverseid) throws Exception;
}
