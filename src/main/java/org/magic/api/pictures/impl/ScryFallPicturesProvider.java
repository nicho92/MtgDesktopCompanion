package org.magic.api.pictures.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractPicturesProvider;
import org.magic.api.providers.impl.ScryFallProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.InstallCert;

public class ScryFallPicturesProvider extends AbstractPicturesProvider {
	
	private Boolean scryfallProvider=null;
	
	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}
	
	
	public ScryFallPicturesProvider() {
		super();
		if(!new File(confdir, getName()+".conf").exists()){
			setProperty("USER_AGENT", MTGConstants.USER_AGENT);
			setProperty("CERT_SERV", "scryfall.com");
			setProperty("CARD_SIZE_WIDTH", "223");
			setProperty("CARD_SIZE_HEIGHT", "310");
			setProperty("PIC_SIZE", "large");
			setProperty("ICON_SET_SIZE","medium");
			save();
		}
		try {
   			InstallCert.install(getProperty("CERT_SERV"));
    		System.setProperty("javax.net.ssl.trustStore",new File(MTGConstants.CONF_DIR,MTGConstants.KEYSTORE_NAME).getAbsolutePath());
 		} catch (Exception e1) {
			logger.error(e1);
		}
		
		newW= Integer.parseInt(getProperty("CARD_SIZE_WIDTH"));
		newH= Integer.parseInt(getProperty("CARD_SIZE_HEIGHT"));
		
	}
	
	private URL generateLink(MagicCard mc, MagicEdition selected,boolean crop) throws MalformedURLException
	{
		
		
		if(scryfallProvider==null)
			scryfallProvider=MTGControler.getInstance().getEnabledProviders() instanceof ScryFallProvider;
		
		
		
		String url ="https://api.scryfall.com/cards/"+selected.getId().toLowerCase()+"/"+selected.getNumber()+"?format=image";
		
		if(scryfallProvider)
		{
			url = "https://api.scryfall.com/cards/"+mc.getId()+"?format=image";
		}
		
	
		
		if(selected.getMultiverse_id()!=null && !selected.getMultiverse_id().equals("0"))
			url = "https://api.scryfall.com/cards/multiverse/"+selected.getMultiverse_id()+"?format=image";

		if(crop)
			url+="&version=art_crop";
		else
			url +="&version="+getProperty("PIC_SIZE","large");
		
		
		
		return new URL(url);
	}
	
	
	
	@Override
	public BufferedImage getPicture(MagicCard mc, MagicEdition ed) throws IOException {
		
		MagicEdition selected=ed;
		
		if(ed==null)
			selected = mc.getEditions().get(0);
		
		
		if(MTGControler.getInstance().getEnabledCache().getPic(mc,selected)!=null)
		{
			logger.trace("cached " + mc + "("+selected+") found");
			return resizeCard(MTGControler.getInstance().getEnabledCache().getPic(mc,selected),newW,newH);
		}
		
		URL url = generateLink(mc, selected, false);
		
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		  connection.setInstanceFollowRedirects(true);
		  connection.setRequestProperty("User-Agent", getProperty("USER_AGENT"));
		  connection.connect();
		  logger.debug("load pics " + connection.getURL().toString());  
			
			try{
				BufferedImage bufferedImage =ImageIO.read(connection.getInputStream());
		
				if(bufferedImage!=null)
					MTGControler.getInstance().getEnabledCache().put(bufferedImage, mc,selected);
					 
				return resizeCard(bufferedImage,newW,newH) ;
			}
			catch(Exception e)
			{
				logger.error(e);
				return getBackPicture();
			}
	}
	
	
	

	@Override
	public BufferedImage getSetLogo(String set, String rarity) throws IOException {
		URL url = new URL("http://gatherer.wizards.com/Handlers/Image.ashx?type=symbol&set="+set+"&size=medium&rarity="+rarity.substring(0,1));
		return ImageIO.read(url);
	}


	@Override
	public String getName() {
		return "ScryFall";
	}

	@Override
	public BufferedImage extractPicture(MagicCard mc) throws IOException {
		URL u = generateLink(mc, mc.getEditions().get(0), true);
		
		HttpURLConnection connection = (HttpURLConnection)u.openConnection();
		  connection.setInstanceFollowRedirects(true);
		  connection.setRequestProperty("User-Agent", getProperty("USER_AGENT"));
		  connection.connect();
		  logger.debug("load pics " + connection.getURL().toString());  
			
			try{
				BufferedImage bufferedImage =ImageIO.read(connection.getInputStream());
				return resizeCard(bufferedImage,newW,newH) ;
			}
			catch(Exception e)
			{
				logger.error(e);
				return getBackPicture();
			}
	}

}
