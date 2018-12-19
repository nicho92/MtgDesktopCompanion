package org.magic.api.pictures.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGPicturesCache;
import org.magic.api.interfaces.abstracts.AbstractPicturesProvider;
import org.magic.services.MTGControler;
import org.magic.tools.URLTools;

public class MythicSpoilerPicturesProvider extends AbstractPicturesProvider {

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public BufferedImage getOnlinePicture(MagicCard mc, MagicEdition me) throws IOException {

		MagicEdition edition = me;
		if (me == null)
			edition = mc.getCurrentSet();

		String cardSet = edition.getId();

		String cardName = mc.getName().toLowerCase().replaceAll(" ", "").replaceAll("-", "").replaceAll("'", "")
				.replaceAll(",", "").replaceAll("/", "");

		// This will properly escape the url
		URI uri;
		try {
			uri = new URI("http", "mythicspoiler.com", "/" + cardSet.toLowerCase() + "/cards/" + cardName + ".jpg",
					null, null);
		} catch (URISyntaxException e1) {
			throw new IOException(e1);
		}
		try {
			logger.debug("get card from " + uri.toURL());
			return URLTools.extractImage(uri.toURL());
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public BufferedImage getSetLogo(String setID, String rarity) throws IOException {
		return null;
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
