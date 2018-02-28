package org.magic.api.pictures.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractPicturesProvider;
import org.magic.services.MTGControler;

public class GathererPicturesProvider extends AbstractPicturesProvider {

	
	@Override
	public STATUT getStatut() {
		return STATUT.STABLE;
	}
	
	
	BufferedImage back;
	private MagicCardInfoPicturesProvider mciProv;
	
	public GathererPicturesProvider() {
		super();
		
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("CALL_MCI_FOR", "p,CEI,CED,CPK,CST");
			props.put("SET_SIZE", "medium");
			props.put("CARD_SIZE_WIDTH", "223");
			props.put("CARD_SIZE_HEIGHT", "310");
			save();
		}
		 newW= Integer.parseInt(getProperty("CARD_SIZE_WIDTH"));
		 newH= Integer.parseInt(getProperty("CARD_SIZE_HEIGHT"));
	}
	
	
	
	@Override
	public BufferedImage extractPicture(MagicCard mc) throws IOException
	{
		return getPicture(mc,null).getSubimage(15, 34, 184, 132);
	}
	
	
	
	@Override
	public BufferedImage getPicture(MagicCard mc,MagicEdition ed) throws IOException{
		
		
		MagicEdition selected=ed;
		
		if(ed==null)
			selected = mc.getEditions().get(0);
		
		for(String k : getProperty("CALL_MCI_FOR").split(","))
		{
			if(selected.getId().startsWith(k))
			{
				if(mciProv==null)
					mciProv=new MagicCardInfoPicturesProvider();
					
					return mciProv.getPicture(mc, selected);
			}
		}
		
		if(MTGControler.getInstance().getEnabledCache().getPic(mc,selected)!=null)
		{
			logger.trace("cached " + mc + "("+selected+") found");
			return resizeCard(MTGControler.getInstance().getEnabledCache().getPic(mc,selected),newW,newH);
		}
	
		
		BufferedImage im = getPicture(selected.getMultiverse_id());
		
		if(im!=null)
			MTGControler.getInstance().getEnabledCache().put(im, mc,ed);
		
		return resizeCard(im,newW,newH);
	}
	
	private BufferedImage getPicture(String multiverseid) throws IOException{
			URL url = new URL("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid="+multiverseid+"&type=card");
			return ImageIO.read(url);
	
	}
	
	

	@Override
	public BufferedImage getSetLogo(String set, String rarity) throws IOException {
		URL url = new URL("http://gatherer.wizards.com/Handlers/Image.ashx?type=symbol&set="+set+"&size="+getProperty("SET_SIZE")+"&rarity="+rarity.substring(0,1));
		return ImageIO.read(url);
	}

	@Override
	public String getName() {
		return "Gatherer";
	}


}
