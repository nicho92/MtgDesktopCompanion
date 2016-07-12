package org.magic.api.pictures.impl;

import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;

import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.PictureProvider;

public class GathererPicturesProvider implements PictureProvider {

	BufferedImage back;
	
	public BufferedImage getBackPicture() throws Exception
	{
		if(back==null)
			back = getPicture("132667");
		return back;
	}
	
	@Override
	public BufferedImage getPicture(MagicCard mc) throws Exception{
		return getPicture(mc.getEditions().get(0).getMultiverse_id());
	}
	
	@Override
	public BufferedImage getPicture(String multiverseid) throws Exception{
		URL url = new URL("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid="+multiverseid+"&type=card");
		return ImageIO.read(url);
}
	

	@Override
	public BufferedImage getPicture(String set, String rarity) throws Exception {
		URL url = new URL("http://gatherer.wizards.com/Handlers/Image.ashx?type=symbol&set="+set+"&size=medium&rarity="+rarity.substring(0,1));
		return ImageIO.read(url);
	}


	@Override
	public URL getPictureURL(String id) throws Exception {
		URL url = new URL("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid="+id+"&type=card");
		return url;
		
	}

}
