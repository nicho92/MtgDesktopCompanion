package org.magic.api.interfaces.abstracts;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.interfaces.MTGPictureCache;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.MTGTokensProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.network.URLTools;
import org.magic.tools.ImageTools;
import org.magic.tools.MTG;
import org.magic.tools.TCache;

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
	
	public BufferedImage getOnlinePicture(MagicCard mc) throws IOException {
		try {
			return URLTools.extractImage(generateUrl(mc));
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public BufferedImage getFullSizePicture(MagicCard mc) throws IOException {
		if (getEnabledPlugin(MTGPictureCache.class).getItem(mc) != null) {
			logger.trace("cached " + mc + "(" + mc.getCurrentSet() + ") found");
			return getEnabledPlugin(MTGPictureCache.class).getItem(mc);
		}
		
		
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
			return getBackPicture();
		}
	}
	
	
	
	
	
	@Override
	public BufferedImage getPicture(MagicCard mc) throws IOException {
		return resizeCard(getFullSizePicture(mc), newW, newH);
	}
	
	@Override
	public BufferedImage getSetLogo(String set, String rarity) throws IOException {
		
		
		try {
			return setCache.get(set+rarity, new Callable<BufferedImage>() {
				@Override
				public BufferedImage call() throws Exception {
					return URLTools.extractImage("http://gatherer.wizards.com/Handlers/Image.ashx?type=symbol&set=" + set + "&size="+ getProperty("SET_SIZE","medium") + "&rarity=" + rarity.substring(0, 1));
				}
			});
		} catch (ExecutionException e) {
			logger.error(e);
		}
		
		return null;
	}
	
	@Override
	public BufferedImage getForeignNamePicture(MagicCardNames fn, MagicCard mc) throws IOException {
		MagicCard foreignCard = mc.toForeign(fn);
		return getPicture(foreignCard);
	}

	@Override
	public void setSize(Dimension d) {
		newW=(int)d.getWidth();
		newH=(int)d.getHeight();
	}

	@Override
	public BufferedImage getBackPicture() {
		try {
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
