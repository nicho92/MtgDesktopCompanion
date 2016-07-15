package org.magic.api.pictures.impl;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLEncoder;

import javax.imageio.ImageIO;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.PictureProvider;

public class MTGCardMakerPicturesProvider implements PictureProvider {

	String url;
	public BufferedImage getPicture(MagicCard mc) {
		try{
			
			String color = (mc.getColors().size()>1?"Gold":mc.getColors().get(0));
			
			if(color.toLowerCase().equals("colorless"))
				color="Gold";
			
			
			url = "http://www.mtgcardmaker.com/mcmaker/createcard.php?"
					+ "name="+URLEncoder.encode(mc.getName(),"UTF-8")
					+ "&color="+color
					+ "&mana_r="+(mc.getCost().contains("{R}")?"1":"0")
					+ "&mana_u="+(mc.getCost().contains("{U}")?"1":"0")
					+ "&mana_g="+(mc.getCost().contains("{G}")?"1":"0")
					+ "&mana_b="+(mc.getCost().contains("{B}")?"1":"0")
					+ "&mana_w="+(mc.getCost().contains("{W}")?"1":"0")
					+ "&mana_colorless="+(mc.getCost().contains("{1}")?"1":"0")
					+ "&picture="
					+ "&supertype="
					+ "&cardtype="+URLEncoder.encode(mc.getFullType(),"UTF-8")
					+ "&subtype="
					+ "&expansion="
					+ "&rarity="+mc.getRarity()
					+ "&cardtext="+URLEncoder.encode(mc.getText(),"UTF-8")
					+ "&power="+mc.getPower()
					+ "&toughness="+mc.getToughness()
					+ "&artist="+URLEncoder.encode(mc.getArtist(),"UTF-8")
					+ "&bottom="+URLEncoder.encode("™ & © 1993-2016 Wizards of the Coast LLC","UTF-8")
					+ "&set1="
					+ "&set2="
					+ "&setname=";
			
				return ImageIO.read(new URL(url));
			
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return null;
			}
			
	}



	@Override
	public URL getPictureURL(String id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BufferedImage getPicture(String multiverseid) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BufferedImage getSetLogo(String setID, String rarity) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
