package org.magic.api.interfaces;

import java.awt.image.BufferedImage;

import org.magic.api.beans.MagicCard;

public interface MagicTokensProvider {

	boolean isTokenizer(MagicCard mc);

	boolean isEmblemizer(MagicCard mc);

	MagicCard generateTokenFor(MagicCard mc);

	MagicCard generateEmblemFor(MagicCard mc) throws Exception;

	BufferedImage getPictures(MagicCard tok) throws Exception;

}