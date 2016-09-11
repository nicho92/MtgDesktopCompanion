package org.magic.api.pictures.impl;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractPicturesProvider;


public class MagicCardInfoPicturesProvider extends AbstractPicturesProvider {

	private int w,h;
	static final Logger logger = LogManager.getLogger(MagicCardInfoPicturesProvider.class.getName());
	
	String website = "";
	String lang ="";
	
	
	
	public MagicCardInfoPicturesProvider() {
		super();
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("WEBSITE", "http://magiccards.info/scans/");
			props.put("LANG", "en");
			save();
		}
		
		w=223;
		h=311;
		website = "http://magiccards.info/scans/";
		lang="en";
	}
	
	@Override
	public BufferedImage getPicture(MagicCard mc,MagicEdition ed) throws Exception {

		String infocode=mc.getEditions().get(0).getMagicCardsInfoCode();
		
		if(infocode==null)
			infocode=mc.getEditions().get(0).getId().toLowerCase();
		
		
		URL url=new URL(website+"/"+lang+"/"+infocode+"/"+mc.getEditions().get(0).getNumber()+".jpg");
		
		if(mc.getMciNumber()!=null)
		{
			if(mc.getMciNumber().contains("/"))
				url=new URL(website+"/"+lang+"/"+infocode+"/"+mc.getMciNumber().substring(mc.getMciNumber().lastIndexOf("/"))+".jpg");
			else	
				url=new URL(website+"/"+lang+"/"+infocode+"/"+mc.getMciNumber()+".jpg");
		}
		
		logger.debug(getName() +" get card pic from " + url);
		
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
	public BufferedImage getSetLogo(String set, String rarity) throws Exception {
		URL url = new URL("http://gatherer.wizards.com/Handlers/Image.ashx?type=symbol&set="+set+"&size=medium&rarity="+rarity.substring(0,1));
		return ImageIO.read(url);
	}


	@Override
	public BufferedImage getBackPicture() throws Exception {
		URL url = new URL("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=132667&type=card");
		return ImageIO.read(url);
	}

	@Override
	public String getName() {
		return "MagicCardInfo";
	}

	@Override
	public BufferedImage extractPicture(MagicCard mc) throws Exception {
		return getPicture(mc,null).getSubimage(15, 34, 184, 132);
	}

}
