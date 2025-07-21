package org.magic.api.interfaces;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;

public interface MTGPictureEditor extends MTGPlugin {

	enum MOD { URI,FILE}

	public BufferedImage getPicture(MTGCard mc, MTGEdition me) throws IOException;
	public MOD getMode();
}
