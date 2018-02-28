package org.magic.api.pictures.impl;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractPicturesProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.InstallCert;

public class DeckMasterPicturesProvider extends AbstractPicturesProvider {
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	
	public DeckMasterPicturesProvider() {
		super();
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("CALL_MCI_FOR", "p,CEI,CED,CPK,CST");
			props.put("USER_AGENT", MTGConstants.USER_AGENT);
			props.put("CERT_SERV", "deckmaster.info");
			props.put("CARD_SIZE_WIDTH", "223");
			props.put("CARD_SIZE_HEIGHT", "310");
			props.put("ICON_SET_SIZE","medium");
			save();
		}
		try {
   			InstallCert.install(getProperty("CERT_SERV"));
    		System.setProperty("javax.net.ssl.trustStore",new File(MTGControler.CONF_DIR,MTGConstants.KEYSTORE_NAME).getAbsolutePath());
 		} catch (Exception e1) {
			logger.error(e1);
		}
		
		 newW= Integer.parseInt(getProperty("CARD_SIZE_WIDTH"));
		 newH= Integer.parseInt(getProperty("CARD_SIZE_HEIGHT"));
	}
	
	private BufferedImage getPicture(String multiverseid) throws IOException{
		
		try{
		
		Document d = Jsoup.connect("https://deckmaster.info/card.php?multiverseid="+multiverseid)
				  .userAgent(getProperty("USER_AGENT"))
				  .get();
		
			logger.debug("read https://deckmaster.info/card.php?multiverseid="+multiverseid);
			Element e = d.select(".card > img" ).get(0);
			HttpURLConnection con = (HttpURLConnection)new URL(e.attr("src")).openConnection();
			con.setRequestProperty("User-Agent",getProperty("USER_AGENT"));
			return ImageIO.read(con.getInputStream());
			
		}
		catch(Exception e)
		{
			logger.error(e);
			return null;
		}
	}
	
	private BufferedImage resizeIconSet(BufferedImage img) {  
	    String mode = props.getProperty("ICON_SET_SIZE","medium");
	    
	    int newW=27;
	    int newH=30;
	    
	    if(mode.equalsIgnoreCase("large")){
	    	newW=118;
	    	newH=130;
	    }
	    
	    
	    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	}  
	
	
	@Override
	public BufferedImage getPicture(MagicCard mc, MagicEdition ed) throws IOException {
	
		MagicEdition selected=ed;
		if(ed==null)
			selected = mc.getEditions().get(0);
		
		
		for(String k : getProperty("CALL_MCI_FOR").split(","))
		{
			if(selected.getId().startsWith(k))
			{
				return new MagicCardInfoPicturesProvider().getPicture(mc, selected);
			}
		}
		
		if(MTGControler.getInstance().getEnabledCache().getPic(mc,selected)!=null)
		{
			
			return resizeCard(MTGControler.getInstance().getEnabledCache().getPic(mc,selected),newW,newH);
		}

		
		BufferedImage im = getPicture(selected.getMultiverse_id());
		
		if(im!=null)
			MTGControler.getInstance().getEnabledCache().put(im, mc,ed);
		
		return resizeCard(im,newW,newH);
	}

	@Override
	public BufferedImage getSetLogo(String setID, String rarity) throws IOException {
		
		switch(setID)
		{
		case "ICE":setID="IA";break;
		case "FEM":setID="FE";break;
		case "LEA":setID="1E";break;
		case "LEB":setID="2E";break;
		case "2ED":setID="2U";break;
		case "LEG":setID="LE";break;
		case "ATQ":setID="AQ";break;
		case "ARN":setID="AN";break;
		default : break;
		}
		
		
		URL u = new URL("https://deckmaster.info/images/sets/"+setID.toUpperCase()+"_"+rarity.substring(0, 1).toUpperCase()+".png");
		HttpURLConnection con = (HttpURLConnection)u.openConnection();
		con.setRequestProperty("User-Agent",getProperty("USER_AGENT"));
		BufferedImage im = ImageIO.read(con.getInputStream());
		return resizeIconSet(im);
	}

	

	@Override
	public String getName() {
		return "Deck Master";
	}

	@Override
	public BufferedImage extractPicture(MagicCard mc) throws IOException {
		return getPicture(mc,null).getSubimage(15, 34, 184, 132);
	}

}
