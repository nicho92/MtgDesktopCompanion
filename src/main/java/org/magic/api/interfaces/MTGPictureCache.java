package org.magic.api.interfaces;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;

public interface MTGPictureCache extends MTGCache<MagicCard, BufferedImage> {

	public BufferedImage getItem(MagicCard mc);

	public void put(BufferedImage im, MagicCard mc) throws IOException;

	public void clear();
	
	public void clear(MagicEdition ed);
	
	public long size();

}
