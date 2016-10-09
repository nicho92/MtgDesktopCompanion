package org.magic.api.cache;

import java.awt.image.BufferedImage;

import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.PicturesCache;
import org.magic.services.MagicFactory;

public class NoCache implements PicturesCache {

	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public String getName() {
		return "No Cache";
	}

	@Override
	public BufferedImage getPic(MagicCard mc) {
		try {
			return MagicFactory.getInstance().getEnabledPicturesProvider().getPicture(mc, null);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void put(BufferedImage im, MagicCard mc) throws Exception {
		//Nothing to do

	}

}
