package org.magic.api.interfaces;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicEdition;

public interface MTGPictureProvider extends MTGPlugin {

	public BufferedImage getPicture(MagicCard mc, MagicEdition me) throws IOException;
	
	public BufferedImage getPicture(MagicCard mc) throws IOException;
	
	public BufferedImage getForeignNamePicture(MagicCardNames fn,MagicCard mc) throws IOException;

	public BufferedImage getSetLogo(String setID, String rarity) throws IOException;

	public BufferedImage getBackPicture();

	public BufferedImage extractPicture(MagicCard mc) throws IOException;
	
	public void setSize(Dimension d);

}
