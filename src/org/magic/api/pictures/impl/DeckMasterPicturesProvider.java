package org.magic.api.pictures.impl;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractPicturesProvider;
import org.magic.services.MTGControler;
import org.magic.tools.InstallCert;

public class DeckMasterPicturesProvider extends AbstractPicturesProvider {

	static final Logger logger = LogManager.getLogger(DeckMasterPicturesProvider.class.getName());

	
	public DeckMasterPicturesProvider() {
		super();
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("CALL_MCI_FOR", "p,CEI,CED,CPK,CST");
			props.put("USER_AGENT", "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.10 (maverick) Firefox/3.6.13");
			props.put("CERT_SERV", "deckmaster.info");
			props.put("CARD_SIZE_WIDTH", "223");
			props.put("CARD_SIZE_HEIGHT", "310");
			save();
		}
		try {
   			InstallCert.install(props.getProperty("CERT_SERV"), MTGControler.KEYSTORE_NAME, MTGControler.KEYSTORE_PASS);
    		System.setProperty("javax.net.ssl.trustStore",new File(MTGControler.CONF_DIR,MTGControler.KEYSTORE_NAME).getAbsolutePath());
 		} catch (Exception e1) {
			logger.error(e1);
		}
	}
	
	private BufferedImage getPicture(String multiverseid) throws Exception{
		
		try{
		
		Document d = Jsoup.connect("https://deckmaster.info/card.php?multiverseid="+multiverseid)
				  .userAgent(props.getProperty("USER_AGENT"))
				  .get();
			Element e = d.select(".card > img" ).get(0);
			HttpURLConnection con = (HttpURLConnection)new URL(e.attr("src")).openConnection();
			con.setRequestProperty("User-Agent",props.getProperty("USER_AGENT"));
			BufferedImage im = ImageIO.read(con.getInputStream());
			return im;
			
		}
		catch(Exception e)
		{
			logger.error(e);
			return null;
		}
	}
	
	
	private BufferedImage resize(BufferedImage img) {  
	    int newW = Integer.parseInt(props.getProperty("CARD_SIZE_WIDTH"));
	    int newH = Integer.parseInt(props.getProperty("CARD_SIZE_HEIGHT"));
	    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	}  
	
	
	@Override
	public BufferedImage getPicture(MagicCard mc, MagicEdition ed) throws Exception {
	
		MagicEdition selected=ed;
		if(ed==null)
			selected = mc.getEditions().get(0);
		
		
		for(String k : props.getProperty("CALL_MCI_FOR").split(","))
		{
			if(selected.getId().startsWith(k))
			{
				return new MagicCardInfoPicturesProvider().getPicture(mc, selected);
			}
		}
		
		if(MTGControler.getInstance().getEnabledCache().getPic(mc,selected)!=null)
			return resize(MTGControler.getInstance().getEnabledCache().getPic(mc,selected));

		
		BufferedImage im = getPicture(selected.getMultiverse_id());
		
		if(im!=null)
			MTGControler.getInstance().getEnabledCache().put(im, mc,ed);
		
		return resize(im);
	}

	@Override
	public BufferedImage getSetLogo(String setID, String rarity) throws Exception {
		
		switch(setID)
		{
		case "ICE":setID="IA";break;
		case "FEM":setID="FE";break;
		default : break;
		}
		
		
		URL u = new URL("https://deckmaster.info/images/sets/"+setID.toUpperCase()+"_"+rarity.substring(0, 1).toUpperCase()+".png");
		HttpURLConnection con = (HttpURLConnection)u.openConnection();
		con.setRequestProperty("User-Agent",props.getProperty("USER_AGENT"));
		BufferedImage im = ImageIO.read(con.getInputStream());
		return im;
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
		return "Deck Master";
	}

	@Override
	public BufferedImage extractPicture(MagicCard mc) throws Exception {
		return getPicture(mc,null).getSubimage(15, 34, 184, 132);
	}

}
