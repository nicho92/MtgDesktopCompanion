package org.magic.api.interfaces;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;

public interface MTGTokensProvider extends MTGPlugin {

	boolean isTokenizer(MagicCard mc);

	boolean isEmblemizer(MagicCard mc);

	public List<MagicCard> listTokensFor(MagicEdition ed)  throws IOException;

	public MagicCard generateTokenFor(MagicCard mc) throws IOException;

	public 	MagicCard generateEmblemFor(MagicCard mc) throws IOException;

	public 	BufferedImage getPictures(MagicCard tok) throws IOException;

}