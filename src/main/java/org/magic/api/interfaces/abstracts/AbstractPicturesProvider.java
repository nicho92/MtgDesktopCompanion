package org.magic.api.interfaces.abstracts;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardNames;
import org.magic.api.interfaces.MTGPictureCache;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.MTGTokensProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.network.URLTools;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.MTG;
import org.magic.services.tools.TCache;

public abstract class AbstractPicturesProvider extends AbstractMTGPlugin implements MTGPictureProvider {

	protected int newW=MTGConstants.DEFAULT_PIC_HEIGHT;
	protected int newH=MTGConstants.DEFAULT_PIC_WIDTH;
	protected TCache<BufferedImage> setCache;

	@Override
	public PLUGINS getType() {
		return PLUGINS.PICTURE;
	}

	protected AbstractPicturesProvider() {
		super();
		setCache = new TCache<>("setIcons");

		try {
			setSize(MTGControler.getInstance().getPictureProviderDimension());
		}
		catch(Exception e)
		{
			logger.error("couldn't set size",e);
		}
	}

	public BufferedImage getOnlinePicture(MTGCard mc) throws IOException {
		try {
			return URLTools.extractAsImage(generateUrl(mc));
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public BufferedImage getFullSizePicture(MTGCard mc) throws IOException {
		
		if (getEnabledPlugin(MTGPictureCache.class).getItem(mc) != null) {
			logger.trace("cached {} ({}) found",mc,mc.getCurrentSet());
			return getEnabledPlugin(MTGPictureCache.class).getItem(mc);
		}

		if(mc==null)
			return getBackPicture(mc);
		

		BufferedImage bufferedImage  = null;
		if(mc.isSpecialTokenOrExtra())
			bufferedImage =MTG.getEnabledPlugin(MTGTokensProvider.class).getPictures(mc);
		else
			bufferedImage  = getOnlinePicture(mc);


		if (bufferedImage != null)
		{
			getEnabledPlugin(MTGPictureCache.class).put(bufferedImage, mc);
			return bufferedImage;
		}
		else
		{
			return getBackPicture(mc);
		}
	}





	@Override
	public BufferedImage getPicture(MTGCard mc) throws IOException {
		return resizeCard(getFullSizePicture(mc), newW, newH);
	}

	@Override
	public BufferedImage getForeignNamePicture(MTGCardNames fn, MTGCard mc) throws IOException {
		var foreignCard = mc.toForeign(fn);
		return getPicture(foreignCard);
	}

	@Override
	public void setSize(Dimension d) {
		newW=(int)d.getWidth();
		newH=(int)d.getHeight();
	}

	@Override
	public BufferedImage getBackPicture(MTGCard mc) {
		try {
			
			
			if(mc!=null&&mc.getCurrentSet()!=null&&mc.getCurrentSet().getId().equals("30A"))
				return resizeCard(ImageTools.readLocal(MTGConstants.ANNIVERSARY_BACK_CARD), newW, newH);
			
			if(mc!=null&&mc.getCurrentSet()!=null&&mc.getCurrentSet().getId().equals("CED"))
				return resizeCard(ImageTools.readLocal(MTGConstants.COLLECTOR_BACK_CARD), newW, newH);
			
			return resizeCard(ImageTools.readLocal(MTGConstants.DEFAULT_BACK_CARD), newW, newH);
			
		} catch (IOException e) {
			logger.error(e);
			return null;
		}
	}

	public BufferedImage resizeCard(BufferedImage img, int newW, int newH) {
		if(img==null)
			return null;
		return ImageTools.resize(img, newH, newW);
	}

	@Override
	public boolean equals(Object obj) {

		if(obj ==null)
			return false;

		return hashCode()==obj.hashCode();
	}

	@Override
	public int hashCode() {
		return (getType()+getName()).hashCode();
	}


}
