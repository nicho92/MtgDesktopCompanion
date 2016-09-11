package org.magic.api.interfaces;

import java.awt.image.BufferedImage;
import java.net.URL;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;

public interface PictureProvider {

	
	public BufferedImage getPicture(MagicCard mc,MagicEdition me) throws Exception;
	public URL getPictureURL(MagicCard mc) throws Exception;
	public BufferedImage getSetLogo(String setID,String rarity) throws Exception;
//	public BufferedImage getPicture(String multiverseid) throws Exception;
	public BufferedImage getBackPicture() throws Exception;
	public String getName();
}
