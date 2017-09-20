package org.magic.api.pictures.impl;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractPicturesProvider;
import org.magic.api.providers.impl.ScryFallProvider;
import org.magic.services.MTGControler;
import org.magic.tools.InstallCert;

public class ScryFallPicturesProvider extends AbstractPicturesProvider {

	static final Logger logger = LogManager.getLogger(ScryFallPicturesProvider.class.getName());
	

	public ScryFallPicturesProvider() {
		super();
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("USER_AGENT", "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.10 (maverick) Firefox/3.6.13");
			props.put("CERT_SERV", "scryfall.com");
			props.put("CARD_SIZE_WIDTH", "223");
			props.put("CARD_SIZE_HEIGHT", "310");
			props.put("ICON_SET_SIZE","medium");
			save();
		}
		try {
   			InstallCert.install(props.getProperty("CERT_SERV"));
    		System.setProperty("javax.net.ssl.trustStore",new File(MTGControler.CONF_DIR,MTGControler.KEYSTORE_NAME).getAbsolutePath());
 		} catch (Exception e1) {
			logger.error(e1);
		}
	}
	
	@Override
	public BufferedImage getPicture(MagicCard mc, MagicEdition ed) throws Exception {
		
		MagicEdition selected=ed;
		
		if(ed==null)
			selected = mc.getEditions().get(0);
		
		if(MTGControler.getInstance().getEnabledCache().getPic(mc,selected)!=null)
		{
			logger.debug("cached " + mc + "("+selected+") found");
			return resizeCard(MTGControler.getInstance().getEnabledCache().getPic(mc,selected));
		}
		
		URL url = new URL("https://api.scryfall.com/cards/"+selected.getId().toLowerCase()+"/"+selected.getNumber()+"?format=image");
		
		if((MTGControler.getInstance().getEnabledProviders() instanceof ScryFallProvider))
			url = new URL("https://api.scryfall.com/cards/"+mc.getId()+"?format=image");
		
		if(selected.getMultiverse_id()!=null)
			if(!selected.getMultiverse_id().equals("0"))
				url = new URL("https://api.scryfall.com/cards/multiverse/"+selected.getMultiverse_id()+"?format=image");
	
		
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		  connection.setInstanceFollowRedirects(true);
		  connection.setRequestProperty("User-Agent", props.getProperty("USER_AGENT"));
		  connection.connect();
		  logger.debug("load pics " + connection.getURL().toString());  
			
			try{
				BufferedImage bufferedImage =ImageIO.read(connection.getInputStream());//= new BufferedImage(img.getWidth(null), img.getHeight(null),BufferedImage.TYPE_INT_RGB);
		
				if(bufferedImage!=null)
					MTGControler.getInstance().getEnabledCache().put(bufferedImage, mc,selected);
					 
				return resizeCard(bufferedImage) ;
			}
			catch(Exception e)
			{
				logger.error(e);
				return getBackPicture();
			}
	}
	
	
	

	@Override
	public BufferedImage getSetLogo(String setID, String rarity) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getName() {
		return "ScryFall";
	}

	@Override
	public BufferedImage extractPicture(MagicCard mc) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
