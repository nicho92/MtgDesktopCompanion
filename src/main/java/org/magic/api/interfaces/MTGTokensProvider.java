package org.magic.api.interfaces;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.magic.api.beans.MagicCard;

public interface MTGTokensProvider extends MTGPlugin {

	boolean isTokenizer(MagicCard mc);

	boolean isEmblemizer(MagicCard mc);

	MagicCard generateTokenFor(MagicCard mc);

	MagicCard generateEmblemFor(MagicCard mc) throws IOException;

	BufferedImage getPictures(MagicCard tok) throws IOException;

	String getName();

}