package org.magic.api.interfaces;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;

public interface MTGPictureEditor extends MTGPlugin {

	enum MOD { LOCAL,URI,FILE}

	public BufferedImage getPicture(MagicCard mc, MagicEdition me) throws IOException;
	public MOD getMode();
}
