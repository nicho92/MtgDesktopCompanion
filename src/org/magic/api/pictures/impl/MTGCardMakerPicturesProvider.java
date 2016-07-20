package org.magic.api.pictures.impl;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLEncoder;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.PictureProvider;
import org.magic.services.MagicFactory;

public class MTGCardMakerPicturesProvider implements PictureProvider {

	public static void main(String[] args) throws Exception {
		JFrame f = new JFrame();
		MagicCard mc = MagicFactory.getInstance().getEnabledProviders().searchCardByCriteria("name", "Liliana's elite", null).get(0);
		
		GathererPicturesProvider gather = new GathererPicturesProvider();
		BufferedImage pic2 = gather.extractPicture(mc);
		
		
		MTGCardMakerPicturesProvider pics = new MTGCardMakerPicturesProvider();
		final BufferedImage pic = pics.getPicture(mc,pic2);
		
		
		f.getContentPane().add(new JPanel(){
			
			@Override
			public void paint(Graphics g) {
				g.drawImage( pic, 0, 0, null);
			}
		});
		
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
	
	public BufferedImage getPicture(MagicCard mc, final BufferedImage pic)
	{
				BufferedImage cadre = getPicture(mc);
			Graphics g = cadre.createGraphics();
			g.drawImage( pic,35, 68, 329, 242, null);
			g.dispose();
			return cadre;
	}
	
	
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
