package org.magic.api.pictures.impl;

import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.PictureProvider;

public class GathererPicturesProvider implements PictureProvider {

	BufferedImage back;
	
	public BufferedImage getBackPicture() throws Exception
	{
		if(back==null)
			back = getPicture("132667");
		return back;
	}
	
	public BufferedImage extractPicture(MagicCard mc) throws Exception
	{
		return getPicture(mc,null).getSubimage(15, 34, 184, 132);
	}
	
	@Override
	public BufferedImage getPicture(MagicCard mc,MagicEdition ed) throws Exception{
		
		MagicEdition selected =ed;
		
		if(ed==null)
			selected = mc.getEditions().get(0);

		if(selected.getId().startsWith("p"))
			return new MagicCardInfoPicturesProvider().getPicture(mc, selected);
		
		return getPicture(selected.getMultiverse_id());
	}
	
	private BufferedImage getPicture(String multiverseid) throws Exception{
		URL url = new URL("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid="+multiverseid+"&type=card");
		return ImageIO.read(url);
	}
	

	@Override
	public BufferedImage getSetLogo(String set, String rarity) throws Exception {
		URL url = new URL("http://gatherer.wizards.com/Handlers/Image.ashx?type=symbol&set="+set+"&size=medium&rarity="+rarity.substring(0,1));
		return ImageIO.read(url);
	}


	public URL getPictureURL(MagicCard id) throws Exception {
		URL url = new URL("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid="+id.getEditions().get(0).getMultiverse_id()+"&type=card");
		return url;
		
	}

}
