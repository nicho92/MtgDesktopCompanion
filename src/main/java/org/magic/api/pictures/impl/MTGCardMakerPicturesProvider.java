package org.magic.api.pictures.impl;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MagicCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractPicturesProvider;
import org.magic.services.MTGLogger;

public class MTGCardMakerPicturesProvider extends AbstractPicturesProvider  {

	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	
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
				MTGLogger.printStackTrace(e);
				return null;
			}
			
	}
	
	

	public int count(String manaCost , String item)
	{
		int count = 0;
		String regex ="\\{(.*?)\\}";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(manaCost);
		while(m.find()) {
			if(m.group().equals(item))
				count ++;
		}
		return count;
	}
	
	public int extractColorless(String manaCost)
	{
		try{
		return Integer.parseInt(manaCost.replaceAll("[^0-9]", ""));
		}
		catch(Exception e)
		{
			return 0;
		}
		
	}
	
	
	public URL getPictureURL(MagicCard mc) throws Exception {
		
		String color="colorless";
		if(mc.getColors().size()>0)
			color = (mc.getColors().size()>1?"Gold":mc.getColors().get(0));
		
		if(color.toLowerCase().equals("colorless"))
			color="Gold";
		
		if(mc.getCost()==null)
			mc.setCost("");
		
		return new URL("http://www.mtgcardmaker.com/mcmaker/createcard.php?"
				+ "name="+URLEncoder.encode(String.valueOf(mc.getName()),"UTF-8")
				+ "&color="+color
				+ "&mana_r="+(mc.getCost().contains("{R}")?String.valueOf(count(mc.getCost(),"{R}")):"0")
				+ "&mana_u="+(mc.getCost().contains("{U}")?String.valueOf(count(mc.getCost(),"{U}")):"0")
				+ "&mana_g="+(mc.getCost().contains("{G}")?String.valueOf(count(mc.getCost(),"{G}")):"0")
				+ "&mana_b="+(mc.getCost().contains("{B}")?String.valueOf(count(mc.getCost(),"{B}")):"0")
				+ "&mana_w="+(mc.getCost().contains("{W}")?String.valueOf(count(mc.getCost(),"{W}")):"0")
				+ "&mana_colorless="+(extractColorless(mc.getCost())>0?extractColorless(mc.getCost()):(mc.getCost().contains("X"))?"X":"0")
				+ "&picture="
				+ "&supertype="
				+ "&cardtype="+URLEncoder.encode(mc.getFullType(),"UTF-8")
				+ "&subtype="
				+ "&expansion="
				+ "&rarity="+mc.getRarity()
				+ "&cardtext="+URLEncoder.encode(String.valueOf(mc.getText()),"UTF-8")
				+ "&power="+mc.getPower()
				+ "&toughness="+mc.getToughness()
				+ "&artist="+URLEncoder.encode(String.valueOf(mc.getArtist()),"UTF-8")
				+ "&bottom="+URLEncoder.encode("\u2122 & \u00A9 1993-"+new Date().getYear()+" Wizards of the Coast LLC","UTF-8")
				+ "&set1="
				+ "&set2="
				+ "&setname=");
		
		
		
	}


	@Override
	public BufferedImage getSetLogo(String setID, String rarity) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public BufferedImage extractPicture(MagicCard mc) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getName() {
		return "MTGCard Maker";
	}


}
