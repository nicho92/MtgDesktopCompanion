package org.magic.api.pictures.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractPicturesProvider;
import org.magic.tools.URLTools;

public class MythicSpoilerPicturesProvider extends AbstractPicturesProvider {

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	
	public String generateUrl(MagicCard mc , MagicEdition me)
	{
		MagicEdition edition = me;
		if (me == null)
			edition = mc.getCurrentSet();

		String cardSet = edition.getId();

		String cardName = mc.getName().toLowerCase().replace(" ", "").replace("-", "").replace("'", "")
				.replace(",", "").replace("/", "");

		// This will properly escape the url
		URI uri=null;
		try {
			uri = new URI("http", "mythicspoiler.com", "/" + cardSet.toLowerCase() + "/cards/" + cardName + ".jpg",
					null, null);
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
	public BufferedImage getOnlinePicture(MagicCard mc, MagicEdition me) throws IOException {
		try {
			return URLTools.extractImage(generateUrl(mc,me));
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getName() {
		return "MythicSpoiler";
	}

	@Override
	public BufferedImage extractPicture(MagicCard mc) throws IOException {
		return null;
	}


}
