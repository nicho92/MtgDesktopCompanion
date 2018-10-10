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

	protected int newW=MTGConstants.DEFAULT_PIC_HEIGHT;
	protected int newH=MTGConstants.DEFAULT_PIC_WIDTH;
	
	
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
			logger.error("couldn't set size",e);
		}
	}

	@Override
	public void setSize(Dimension d) {
		newW=(int)d.getWidth();
		newH=(int)d.getHeight();
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
		if(img==null)
			return null;
		return ImageUtils.resize(img, newH, newW);
	}

}
