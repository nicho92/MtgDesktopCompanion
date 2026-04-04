package org.magic.api.pictures.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.enums.EnumLayout;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractPicturesProvider;
import org.magic.services.network.RequestBuilder;

public class ScryFallPicturesProvider extends AbstractPicturesProvider {

	@Override
	public String generateUrl(MTGCard mc) {
		try {
			return generateLink(mc,false).toString();
		} catch (MalformedURLException _) {
			return "";
		}
	}

	private URL generateLink(MTGCard mc, boolean crop) throws MalformedURLException {

		var url = new StringBuilder("https://cards.scryfall.io/");
		
		
		if(crop)
		{
			url.append("art_crop");
		}
		else
		{
			url.append( getProperty("PIC_SIZE", "large"));
		}
				
		if(mc.isDoubleFaced() && !mc.getSide().equals("a") && mc.getLayout()!=EnumLayout.MELD)
			url.append("/back/");
		else
			url.append("/front/");
		
		
		url.append(mc.getScryfallId().charAt(0)).append("/").append(mc.getScryfallId().charAt(1)).append("/").append(mc.getScryfallId()).append(".jpg");
		
		return URI.create(url.toString()).toURL();
		
		
	}
	
	private BufferedImage extractAsImage(String url) throws IOException
	{
		
		return  RequestBuilder.build().newClient().url(url).addHeader("Accept","application/json;q=0.9,*/*;q=0.8").get().toImage();
		
	}
	

	@Override
	public BufferedImage getOnlinePicture(MTGCard mc) throws IOException {
		var url = generateLink(mc, false);
		try {
			return extractAsImage(url.toString());
		} catch (Exception _) {
			return null;
		}
	}

	@Override
	public String getName() {
		return "ScryFall";
	}

	@Override
	public BufferedImage extractPicture(MTGCard mc) throws IOException {
		var u = generateLink(mc,true);
		try {
			return extractAsImage(u.toString());
		} catch (Exception e) {
			logger.error(e);
			return getBackPicture(mc);
		}
	}


	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of( "PIC_SIZE", new MTGProperty("large", "Image quality to download from scryfall", "large","normal","small"));
	}

	@Override
	public String getVersion() {
		return "2.0";
	}

}
