package org.magic.api.interfaces;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;

public interface MTGPictureProvider extends MTGPlugin {

	public BufferedImage getPicture(MagicCard mc, MagicEdition me) throws IOException;

	public BufferedImage getSetLogo(String setID, String rarity) throws IOException;

	public BufferedImage getBackPicture();

	public BufferedImage extractPicture(MagicCard mc) throws IOException;
	
	public void updateSize();
	
	
//	public String getURLPicture(MagicCard mc, MagicEdition ed)throws IOException;
	

}
