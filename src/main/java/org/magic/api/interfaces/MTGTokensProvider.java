package org.magic.api.interfaces;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;

public interface MTGTokensProvider extends MTGPlugin {

	boolean isTokenizer(MTGCard mc);

	boolean isEmblemizer(MTGCard mc);

	public List<MTGCard> listTokensFor(MTGEdition ed)  throws IOException;

	public MTGCard generateTokenFor(MTGCard mc) throws IOException;

	public 	MTGCard generateEmblemFor(MTGCard mc) throws IOException;

	public 	BufferedImage getPictures(MTGCard tok) throws IOException;

}