package org.magic.api.interfaces.abstracts;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.MTGPicturesCache;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.ImageTools;
import org.magic.tools.TCache;
import org.magic.tools.URLTools;

public abstract class AbstractPicturesProvider extends AbstractMTGPlugin implements MTGPictureProvider {

	protected int newW=MTGConstants.DEFAULT_PIC_HEIGHT;
	protected int newH=MTGConstants.DEFAULT_PIC_WIDTH;
	protected TCache<BufferedImage> setCache;
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.PICTURES;
	}


	public AbstractPicturesProvider() {
		super();
		setCache = new TCache<>("setIcons");
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
	
	public abstract BufferedImage getOnlinePicture(MagicCard mc, MagicEdition ed) throws IOException;
	
	@Override
	public BufferedImage getPicture(MagicCard mc, MagicEdition ed) throws IOException {
		if (MTGControler.getInstance().getEnabled(MTGPicturesCache.class).getPic(mc, ed) != null) {
			logger.trace("cached " + mc + "(" + ed + ") found");
			return resizeCard(MTGControler.getInstance().getEnabled(MTGPicturesCache.class).getPic(mc, ed), newW, newH);
		}
		BufferedImage bufferedImage = getOnlinePicture(mc, ed);
		if (bufferedImage != null)
		{
			MTGControler.getInstance().getEnabled(MTGPicturesCache.class).put(bufferedImage, mc, ed);
			return resizeCard(bufferedImage, newW, newH);
		}
		else
		{
			return getBackPicture();
		}
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
		return getPicture(foreignCard,foreignCard.getCurrentSet());
	}

	@Override
	public void setSize(Dimension d) {
		newW=(int)d.getWidth();
		newH=(int)d.getHeight();
	}

	@Override
	public BufferedImage getBackPicture() {
		try {
			return ImageIO.read(MTGConstants.DEFAULT_BACK_CARD);
		} catch (IOException e) {
			logger.error(e);
			return null;
		}
	}

	@Override
	public void initDefault() {
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
