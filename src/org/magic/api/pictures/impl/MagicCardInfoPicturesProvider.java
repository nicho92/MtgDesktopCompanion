package org.magic.api.pictures.impl;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.PictureProvider;


public class MagicCardInfoPicturesProvider implements PictureProvider {

	private int w,h;
	
	public MagicCardInfoPicturesProvider() {
		
		w=223;
		h=311;
	}
	
	@Override
	public BufferedImage getPicture(MagicCard mc,MagicEdition ed) throws Exception {

		URL url=new URL("http://magiccards.info/scans/en/"+mc.getEditions().get(0).getMagicCardsInfoCode()+"/"+mc.getEditions().get(0).getNumber()+".jpg");
		if(mc.getEditions().get(0).getMagicCardsInfoCode()==null)
			url=new URL("http://magiccards.info/scans/en/"+mc.getEditions().get(0).getId().toLowerCase()+"/"+mc.getEditions().get(0).getNumber()+".jpg");

		URLConnection connection = url.openConnection();
		
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
	
		
		connection.connect();
		Image img = ImageIO.read(connection.getInputStream()).getScaledInstance(w, h, BufferedImage.SCALE_SMOOTH);
		
		BufferedImage bufferedImage = new BufferedImage(img.getWidth(null), img.getHeight(null),
		        BufferedImage.TYPE_INT_RGB);

		    Graphics g = bufferedImage.createGraphics();
		    g.drawImage(img, 0, 0, null);
		    g.dispose();

		
		return bufferedImage ;
	}

	@Override
	public URL getPictureURL(MagicCard mc) throws Exception {
		URL connection = new URL("http://magiccards.info/scans/en/"+mc.getEditions().get(0).getMagicCardsInfoCode()+"/"+mc.getEditions().get(0).getNumber()+".jpg");
			return connection;
	}

	@Override
	public BufferedImage getSetLogo(String set, String rarity) throws Exception {
		URL url = new URL("http://gatherer.wizards.com/Handlers/Image.ashx?type=symbol&set="+set+"&size=medium&rarity="+rarity.substring(0,1));
		return ImageIO.read(url);
	}

//	@Override
//	public BufferedImage getPicture(String multiverseid) throws Exception {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public BufferedImage getBackPicture() throws Exception {
		URL url = new URL("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=132667&type=card");
		return ImageIO.read(url);
	}

}
