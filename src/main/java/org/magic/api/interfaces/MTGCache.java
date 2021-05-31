package org.magic.api.interfaces;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;

public interface MTGCache extends MTGPlugin {

	public BufferedImage getPic(MagicCard mc);

	public void put(BufferedImage im, MagicCard mc) throws IOException;

	public void clear();
	
	public void clear(MagicEdition ed);
	
	public long size();

}
