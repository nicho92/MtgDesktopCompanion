package org.magic.api.pictures.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractPicturesProvider;

public class GathererPicturesProvider extends AbstractPicturesProvider {

	BufferedImage back;
	Map<String,BufferedImage> cache;
	
	public GathererPicturesProvider() {
		super();
		cache=new HashMap<String,BufferedImage>();
		
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("BACKGROUND_ID", "132667");
			props.put("CALL_MCI_FOR", "p,CEI,CED,CPK");
			save();
		}
	}
	
	
	@Override
	public BufferedImage getBackPicture() throws Exception
	{
		if(back==null)
			back = getPicture(props.getProperty("BACKGROUND_ID"));
		return back;
	}
	
	@Override
	public BufferedImage extractPicture(MagicCard mc) throws Exception
	{
		return getPicture(mc,null).getSubimage(15, 34, 184, 132);
	}
	
	@Override
	public BufferedImage getPicture(MagicCard mc,MagicEdition ed) throws Exception{
		
		MagicEdition selected =ed;
		
		if(ed==null)
			selected = mc.getEditions().get(0);

		
		for(String k : props.getProperty("CALL_MCI_FOR").split(","))
		{
			if(selected.getId().startsWith(k))
				return new MagicCardInfoPicturesProvider().getPicture(mc, selected);
		}
			
		return getPicture(selected.getMultiverse_id());
	}
	
	private BufferedImage getPicture(String multiverseid) throws Exception{
		if(cache.get(multiverseid)==null)
		{
			URL url = new URL("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid="+multiverseid+"&type=card");
			cache.put(multiverseid, ImageIO.read(url));
		}
		
		return cache.get(multiverseid);
		
	}
	

	@Override
	public BufferedImage getSetLogo(String set, String rarity) throws Exception {
		URL url = new URL("http://gatherer.wizards.com/Handlers/Image.ashx?type=symbol&set="+set+"&size=medium&rarity="+rarity.substring(0,1));
		return ImageIO.read(url);
	}

	@Override
	public String getName() {
		return "Gatherer";
	}


}
