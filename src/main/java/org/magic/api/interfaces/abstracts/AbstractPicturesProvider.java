package org.magic.api.interfaces.abstracts;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.ImageUtils;

public abstract class AbstractPicturesProvider extends AbstractMTGPlugin implements MTGPictureProvider {

	protected int newW=223;
	protected int newH=310;

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
		
		try {
			setSize(MTGControler.getInstance().getPictureProviderDimension());
		}
		catch(Exception e)
		{
			logger.error("couldn't set size");
		}
		
	}


	private void setSize(Dimension d) {
		setSize((int)d.getWidth(), (int)d.getHeight());
		
	}


	@Override
	public void setSize(int w,int h) {
		newW=w;
		newH=h;
		
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
	}

	public BufferedImage resizeCard(BufferedImage img, int newW, int newH) {
		return ImageUtils.resize(img, newH, newW);
	}

}
