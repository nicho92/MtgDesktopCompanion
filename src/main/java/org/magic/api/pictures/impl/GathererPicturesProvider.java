package org.magic.api.pictures.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.MTGPicturesCache;
import org.magic.api.interfaces.abstracts.AbstractPicturesProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.URLTools;

public class GathererPicturesProvider extends AbstractPicturesProvider {

	@Override
	public BufferedImage extractPicture(MagicCard mc) throws IOException {
		return getPicture(mc, null).getSubimage(15, 34, 184, 132);
	}

	@Override
	public BufferedImage getPicture(MagicCard mc, MagicEdition ed) throws IOException {

		MagicEdition selected = ed;

		if (ed == null)
			selected = mc.getCurrentSet();

		for (String k : getArray("CALL_MCI_FOR")) {
			if (selected.getId().startsWith(k)) {
				return MTGControler.getInstance().getPlugin(getString("SECOND_PROVIDER"), MTGPictureProvider.class).getPicture(mc, selected);
			}
		}

		if (MTGControler.getInstance().getEnabled(MTGPicturesCache.class).getPic(mc, selected) != null) {
			logger.trace("cached " + mc + "(" + selected + ") found");
			return resizeCard(MTGControler.getInstance().getEnabled(MTGPicturesCache.class).getPic(mc, selected), newW, newH);
		}

		BufferedImage im = getPicture(selected.getMultiverseid());

		if (im != null)
			MTGControler.getInstance().getEnabled(MTGPicturesCache.class).put(im, mc, ed);

		return resizeCard(im, newW, newH);
	}

	private BufferedImage getPicture(String multiverseid) throws IOException {
		return ImageIO.read(URLTools.openConnection("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=" + multiverseid + "&type=card").getInputStream());
	}

	
	@Override
	public BufferedImage getPicture(MagicCardNames fn, MagicCard mc) throws IOException {
		return getPicture(String.valueOf(fn.getGathererId()));
	}
	
	@Override
	public BufferedImage getSetLogo(String set, String rarity) throws IOException {
		return ImageIO.read(URLTools.openConnection("http://gatherer.wizards.com/Handlers/Image.ashx?type=symbol&set=" + set + "&size="+ getString("SET_SIZE") + "&rarity=" + rarity.substring(0, 1)).getInputStream());
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
	
	@Override
	public Icon getIcon() {
		return new ImageIcon(MTGConstants.IMAGE_LOGO);
	}

}
