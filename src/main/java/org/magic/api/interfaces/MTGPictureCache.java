package org.magic.api.interfaces;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.magic.api.beans.MagicCard;

public interface MTGPictureCache extends MTGCache<MagicCard, BufferedImage> {

	public BufferedImage getItem(MagicCard mc);

	public void put(BufferedImage im, MagicCard mc) throws IOException;

}
