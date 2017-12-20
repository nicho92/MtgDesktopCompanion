package org.magic.api.interfaces;

import java.awt.image.BufferedImage;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;

public interface PictureProvider extends MTGPlugin {

	
	public BufferedImage getPicture(MagicCard mc,MagicEdition me) throws Exception;
	public BufferedImage getSetLogo(String setID,String rarity) throws Exception;
	public BufferedImage getBackPicture() throws Exception;
	public BufferedImage extractPicture(MagicCard mc) throws Exception;

}
