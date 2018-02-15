package org.magic.api.interfaces;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;

public interface MTGPicturesCache extends MTGPlugin {

	
	public BufferedImage getPic(MagicCard mc,MagicEdition ed);
	public void put(BufferedImage im,MagicCard mc,MagicEdition ed) throws IOException;
	public void clear();
	

}
