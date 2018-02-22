package org.magic.api.pictures.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractPicturesProvider;
import org.magic.services.MTGControler;

public class MagidexPicturesProvider extends AbstractPicturesProvider {
	
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	
	public MagidexPicturesProvider() {
		super();
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("USER_AGENT", "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.10 (maverick) Firefox/3.6.13");
			props.put("CARD_SIZE_WIDTH", "223");
			props.put("CARD_SIZE_HEIGHT", "310");
			save();
		}
		newW= Integer.parseInt(props.getProperty("CARD_SIZE_WIDTH"));
		newH= Integer.parseInt(props.getProperty("CARD_SIZE_HEIGHT"));
	
	}
	
	
	@Override
	public BufferedImage getPicture(MagicCard mc, MagicEdition me) throws IOException {
		
		String cardName = mc.getName().toLowerCase();
		MagicEdition edition = me;
		if(me==null)
			edition=mc.getEditions().get(0);
		
		
		if(MTGControler.getInstance().getEnabledCache().getPic(mc,edition)!=null)
		{
			logger.trace("cached " + mc + "("+edition+") found");
			return resizeCard(MTGControler.getInstance().getEnabledCache().getPic(mc,edition),newW,newH);
		}
		
        String cardSet = edition.getId();
        
        if (mc.getName().contains("//")) {
            cardName = cardName.replaceAll("//", "");
        }

        // This will properly escape the url
        URI uri;
		try {
			uri = new URI("http", "magidex.com", "/extstatic/card/" + cardSet.toUpperCase() + '/' + cardName + ".jpg", null, null);
		} catch (URISyntaxException e1) {
			throw new IOException(e1);
		}
        
        logger.debug("get card from " + uri.toURL());
        HttpURLConnection connection = (HttpURLConnection)uri.toURL().openConnection();
		  connection.setInstanceFollowRedirects(true);
		  connection.setRequestProperty("User-Agent", props.getProperty("USER_AGENT"));
		  connection.connect();
        
		  try{
				BufferedImage bufferedImage =ImageIO.read(connection.getInputStream());
				if(bufferedImage!=null)
					MTGControler.getInstance().getEnabledCache().put(bufferedImage, mc,edition);
				return resizeCard(bufferedImage,newW,newH) ;
			}
			catch(Exception e)
			{
				logger.error(e);
				return getBackPicture();
			}
	}

	@Override
	public BufferedImage getSetLogo(String setID, String rarity) throws IOException {
		return null;
	}

	@Override
	public String getName() {
		return "MagiDex";
	}

	@Override
	public BufferedImage extractPicture(MagicCard mc) throws IOException {
		return null;
	}
	
	

}
