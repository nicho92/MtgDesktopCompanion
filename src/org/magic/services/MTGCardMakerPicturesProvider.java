package org.magic.services;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLEncoder;

import javax.imageio.ImageIO;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.PictureProvider;

public class MTGCardMakerPicturesProvider  {

	public BufferedImage generatePictureForCard(MagicCard mc, BufferedImage pic)
	{
			BufferedImage cadre = getPicture(mc,null);
			Graphics g = cadre.createGraphics();
			g.drawImage( pic,35, 68, 329, 242, null);
			g.dispose();
			return cadre;
	}
	
	
	public BufferedImage getPicture(MagicCard mc,MagicEdition ed) {
		try{
			
			URL url = getPictureURL(mc);
				return ImageIO.read(url);
			
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return null;
			}
			
	}



	public URL getPictureURL(MagicCard mc) throws Exception {
		
		String color = (mc.getColors().size()>1?"Gold":mc.getColors().get(0));
		
		if(color.toLowerCase().equals("colorless"))
			color="Gold";
		
		
		
		return new URL("http://www.mtgcardmaker.com/mcmaker/createcard.php?"
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
				+ "&setname=");
		
	}

}
