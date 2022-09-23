package org.magic.api.interfaces;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;

public interface MTGPictureEditor extends MTGPlugin {

	enum MOD { LOCAL,URI,FILE}


	public void setFoil(Boolean b);
	public void setTextSize(int size);
	public void setCenter(boolean center);
	public void setColorIndicator(boolean selected);
	public void setColorAccentuation(String c);
	public BufferedImage getPicture(MagicCard mc, MagicEdition me) throws IOException;
	public MOD getMode();
}
