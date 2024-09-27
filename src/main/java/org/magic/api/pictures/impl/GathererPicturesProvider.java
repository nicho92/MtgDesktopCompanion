package org.magic.api.pictures.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.magic.api.beans.MTGCard;
import org.magic.api.interfaces.abstracts.AbstractPicturesProvider;
import org.magic.services.network.URLTools;

public class GathererPicturesProvider extends AbstractPicturesProvider {

	@Override
	public BufferedImage extractPicture(MTGCard mc) throws IOException {
		return getPicture(mc).getSubimage(15, 34, 184, 132);
	}

	@Override
	public BufferedImage getOnlinePicture(MTGCard mc) throws IOException {
		return URLTools.extractAsImage(generateUrl(mc));
	}

	@Override
	public String generateUrl(MTGCard mc)
	{
		return "http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=" + mc.getMultiverseid() + "&type=card";
	}



	@Override
	public String getName() {
		return "Gatherer";
	}

	@Override
	public String getVersion() {
		return "2.0";
	}

}
