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
import org.magic.services.network.URLTools;

public class ScryFallPicturesProvider extends AbstractPicturesProvider {

	private static final String HTTP_API_SCRYFALL = "https://api.scryfall.com/cards/";
	private static final String IMAGE_TAG = "?format=image";


	@Override
	public String generateUrl(MTGCard mc) {
		try {
			return generateLink(mc,false).toString();
		} catch (MalformedURLException _) {
			return "";
		}
	}

	private URL generateLink(MTGCard mc, boolean crop) throws MalformedURLException {

		String  url = HTTP_API_SCRYFALL + mc.getScryfallId() + IMAGE_TAG;
		
		if(mc.isDoubleFaced() && !mc.getSide().equals("a") && mc.getLayout()!=EnumLayout.MELD)
				url=url+"&face=back";
		
		if (crop)
			url += "&version=art_crop";
		else
			url += "&version=" + getProperty("PIC_SIZE", "large");

		return URI.create(url).toURL();
	}
	
	private BufferedImage extractAsImage(String url) throws IOException
	{
		
		return  RequestBuilder.build().setClient(URLTools.newClient()).url(url).addHeader("Accept","application/json;q=0.9,*/*;q=0.8").get().toImage();
		
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
