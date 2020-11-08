package org.magic.api.pictures.impl;

import static org.magic.tools.MTG.getPlugin;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.abstracts.AbstractPicturesProvider;
import org.magic.tools.URLTools;

public class GathererPicturesProvider extends AbstractPicturesProvider {

	@Override
	public BufferedImage extractPicture(MagicCard mc) throws IOException {
		return getPicture(mc).getSubimage(15, 34, 184, 132);
	}

	@Override
	public BufferedImage getOnlinePicture(MagicCard mc) throws IOException {

			for (String k : getArray("CALL_MCI_FOR")) {
			if (mc.getCurrentSet().getId().startsWith(k)) {
				return getPlugin(getString("SECOND_PROVIDER"), MTGPictureProvider.class).getPicture(mc);
			}
		}
		return URLTools.extractImage(generateUrl(mc));
	}

	public String generateUrl(MagicCard mc)
	{
		return "http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=" + mc.getCurrentSet().getMultiverseid() + "&type=card";
	}
	
	

	@Override
	public String getName() {
		return "Gatherer";
	}

	@Override
	public void initDefault() {
		super.initDefault();
		setProperty("SECOND_PROVIDER", "ScryFall");
		setProperty("CALL_MCI_FOR", "p,CEI,CED,CPK,CST");
		setProperty("SET_SIZE", "medium");
	}

	@Override
	public String getVersion() {
		return "2.0";
	}

}
