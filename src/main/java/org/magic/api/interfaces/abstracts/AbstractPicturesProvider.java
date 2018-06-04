package org.magic.api.interfaces.abstracts;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.services.MTGConstants;
import org.magic.tools.ImageUtils;

public abstract class AbstractPicturesProvider extends AbstractMTGPlugin implements MTGPictureProvider {

	protected int newW;
	protected int newH;

	@Override
	public PLUGINS getType() {
		return PLUGINS.PICTURES;
	}


	public AbstractPicturesProvider() {
		super();
		confdir = new File(MTGConstants.CONF_DIR, "pictures");
		if (!confdir.exists())
			confdir.mkdir();
		load();

		if (!new File(confdir, getName() + ".conf").exists()) {
			initDefault();
			save();
		}

		newW = getInt("CARD_SIZE_WIDTH");
		newH = getInt("CARD_SIZE_HEIGHT");
		
	}

	@Override
	public BufferedImage getBackPicture() {
		try {
			return ImageIO.read(AbstractPicturesProvider.class.getResource("/icons/back.jpg"));
		} catch (IOException e) {
			logger.error("Error reading back picture ", e);
			return null;
		}
	}

	@Override
	public void initDefault() {
		setProperty("CARD_SIZE_WIDTH", "223");
		setProperty("CARD_SIZE_HEIGHT", "310");
	}

	public BufferedImage resizeCard(BufferedImage img, int newW, int newH) {
		return ImageUtils.resize(img, newH, newW);
	}

}
