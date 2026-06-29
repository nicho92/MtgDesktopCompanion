package org.magic.api.pictures.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.enums.EnumLayout;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractPicturesProvider;
import org.magic.services.network.RequestBuilder;
import org.magic.services.network.URLTools;

public class ScryFallPicturesProvider extends AbstractPicturesProvider {

	private static final String LARGE = "large";

	@Override
	public String generateUrl(MTGCard mc, boolean crop) {

		var url = new StringBuilder("https://cards.scryfall.io/");

		if (crop) {
			url.append("art_crop");
		} else {
			url.append(getProperty("PIC_SIZE", LARGE));
		}

		if (mc.isDoubleFaced() && !mc.getSide().equals("a") && mc.getLayout() != EnumLayout.MELD)
			url.append("/back/");
		else
			url.append("/front/");

		url.append(mc.getScryfallId().charAt(0)).append("/").append(mc.getScryfallId().charAt(1)).append("/")
				.append(mc.getScryfallId());
		
		if(getString("PIC_SIZE").equals("display"))
				url.append(".webp");
		else
			url.append(".jpg");

		return url.toString();

	}

	private BufferedImage extractAsImage(String url) throws IOException {

		return RequestBuilder.build().newClient().url(url).addHeader(URLTools.ACCEPT, "application/json;q=0.9,*/*;q=0.8").get()
				.toImage();

	}

	@Override
	public BufferedImage getOnlinePicture(MTGCard mc) throws IOException {
		var url = generateUrl(mc, false);
		try {
			return extractAsImage(url);
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
		var u = generateUrl(mc, true);
		try {
			return extractAsImage(u);
		} catch (Exception e) {
			logger.error(e);
			return getBackPicture(mc);
		}
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("PIC_SIZE",
				new MTGProperty(LARGE, "Image quality to download from scryfall", LARGE, "normal", "small","display"));
	}

	@Override
	public String getVersion() {
		return "2.0";
	}

}
