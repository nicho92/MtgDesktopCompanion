package org.magic.api.pictures.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.magic.api.beans.MTGCard;
import org.magic.api.interfaces.abstracts.AbstractPicturesProvider;
import org.magic.services.tools.UITools;

public class MythicSpoilerPicturesProvider extends AbstractPicturesProvider {

	@Override
	public STATUT getStatut() {
		return STATUT.DEPRECATED;
	}


	@Override
	public String generateUrl(MTGCard mc)
	{
		String cardSet = mc.getCurrentSet().getId();
		String cardName = UITools.replaceSpecialCharacters(mc.getName(), "").toLowerCase();

		// This will properly escape the url
		URI uri=null;
		try {
			uri = new URI("https", "mythicspoiler.com", "/" + cardSet.toLowerCase() + "/cards/" + cardName + ".jpg",null, null);
		} catch (URISyntaxException e1) {
			logger.error(e1);
			return null;
		}
		try {
			return uri.toURL().toString();
		} catch (Exception e) {
			return null;
		}
	}


	@Override
	public String getName() {
		return "MythicSpoiler";
	}

	@Override
	public BufferedImage extractPicture(MTGCard mc) throws IOException {
		return null;
	}


}
