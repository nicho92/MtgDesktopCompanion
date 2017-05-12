package org.magic.api.pictures.impl;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractPicturesProvider;
import org.magic.services.MTGControler;


public class MagicCardInfoPicturesProvider extends AbstractPicturesProvider {

	private int w,h;
	static final Logger logger = LogManager.getLogger(MagicCardInfoPicturesProvider.class.getName());
	
	
	
	
	public MagicCardInfoPicturesProvider() {
		super();
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("WEBSITE", "http://magiccards.info/scans/");
			props.put("LANG", "en");
			props.put("USER_AGENT","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			save();
		}
	
		w=223;
		h=311;
	}
	
	@Override
	public BufferedImage getPicture(MagicCard mc,MagicEdition ed) throws Exception {

		if(MTGControler.getInstance().getEnabledCache().getPic(mc,ed)!=null)
			return MTGControler.getInstance().getEnabledCache().getPic(mc,ed);
	
		
		String infocode=mc.getEditions().get(0).getMagicCardsInfoCode();
		
		/*if(ed!=null)
			infocode=ed.getMagicCardsInfoCode();
		*/
		
		if(infocode==null)
			infocode=mc.getEditions().get(0).getId().toLowerCase();
		
		URL url;//new URL(props.getProperty("WEBSITE")+"/"+props.getProperty("LANG")+"/"+infocode+"/"+mc.getEditions().get(0).getNumber().replaceAll("a", "").replaceAll("b", "")+".jpg");
		
		
		
		if(mc.getMciNumber()!=null)
		{
			if(mc.getMciNumber().contains("/"))
			{
				String mcinumber=mc.getMciNumber().substring(mc.getMciNumber().lastIndexOf("/")).replaceAll(".html", "");
				url=new URL(props.getProperty("WEBSITE")+"/"+props.getProperty("LANG")+"/"+infocode+"/"+mcinumber+".jpg");
			}
			else	
			{
				url=new URL(props.getProperty("WEBSITE")+"/"+props.getProperty("LANG")+"/"+infocode+"/"+mc.getMciNumber()+".jpg");
			}
		}
		else
		{
			url=new URL(props.getProperty("WEBSITE")+"/"+props.getProperty("LANG")+"/"+infocode+"/"+mc.getEditions().get(0).getNumber().replaceAll("a", "").replaceAll("b", "")+".jpg");
		}
		
		logger.debug(getName() +" get card pic from " + url);

		URLConnection connection = url.openConnection();
					  connection.setRequestProperty("User-Agent", props.getProperty("USER_AGENT"));
					  connection.connect();
					  
		Image img = null;
				
				try{
					img = ImageIO.read(connection.getInputStream()).getScaledInstance(w, h, BufferedImage.SCALE_SMOOTH);
					BufferedImage bufferedImage = new BufferedImage(img.getWidth(null), img.getHeight(null),
					        BufferedImage.TYPE_INT_RGB);

					    Graphics g = bufferedImage.createGraphics();
					    g.drawImage(img, 0, 0, null);
					    g.dispose();

					MTGControler.getInstance().getEnabledCache().put(bufferedImage, mc,ed);
					return bufferedImage ;
				}
				catch(Exception e)
				{
					logger.error(e);
				}
		
		return getBackPicture();
	}


	@Override
	public BufferedImage getSetLogo(String set, String rarity) throws Exception {
		URL url = new URL("http://gatherer.wizards.com/Handlers/Image.ashx?type=symbol&set="+set+"&size=medium&rarity="+rarity.substring(0,1));
		return ImageIO.read(url);
	}


	@Override
	public BufferedImage getBackPicture() throws Exception {
		try {
			return ImageIO.read(AbstractPicturesProvider.class.getResource("/res/back.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} 
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
