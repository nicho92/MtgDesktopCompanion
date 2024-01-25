package org.magic.api.interfaces;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardNames;

public interface MTGPictureProvider extends MTGPlugin {

	public BufferedImage getPicture(MTGCard mc) throws IOException;

	public BufferedImage getFullSizePicture(MTGCard mc) throws IOException;

	public BufferedImage getForeignNamePicture(MTGCardNames fn,MTGCard mc) throws IOException;

	public BufferedImage getBackPicture(MTGCard mc);

	public BufferedImage extractPicture(MTGCard mc) throws IOException;

	public void setSize(Dimension d);

	public String generateUrl(MTGCard mc);
}
