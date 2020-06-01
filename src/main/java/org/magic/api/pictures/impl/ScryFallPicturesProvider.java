package org.magic.api.pictures.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpConnection;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractPicturesProvider;
import org.magic.api.providers.impl.ScryFallProvider;
import org.magic.services.MTGControler;
import org.magic.tools.ImageTools;
import org.magic.tools.InstallCert;
import org.magic.tools.URLTools;

public class ScryFallPicturesProvider extends AbstractPicturesProvider {

	private static final String HTTP_API_SCRYFALL = "https://api.scryfall.com/cards/";
	private static final String IMAGE_TAG = "?format=image";
	private static final String LOAD_CERTIFICATE = "LOAD_CERTIFICATE";
	private Boolean scryfallProvider = null;
	
	public ScryFallPicturesProvider() {
		super();
		if(getBoolean(LOAD_CERTIFICATE))
		{
			try {
				InstallCert.installCert("scryfall.com");
				setProperty(LOAD_CERTIFICATE, "false");
			} catch (Exception e1) {
				logger.error(e1);
			}
		}


	}
	
	@Override
	public String generateUrl(MagicCard mc, MagicEdition me) {
		try {
			return generateLink(mc,me,false).toString();
		} catch (MalformedURLException e) {
			return "";
		}
	}
	
	

	private URL generateLink(MagicCard mc, MagicEdition selected, boolean crop) throws MalformedURLException {

		if(selected==null)
			selected = mc.getCurrentSet();
		
		
		if (scryfallProvider == null)
			scryfallProvider = MTGControler.getInstance().getEnabled(MTGCardsProvider.class) instanceof ScryFallProvider;

		String url = HTTP_API_SCRYFALL + selected.getId().toLowerCase() + "/" + selected.getNumber()+ IMAGE_TAG;

		if (scryfallProvider.booleanValue()) {
			url = HTTP_API_SCRYFALL + mc.getId() + IMAGE_TAG;
		}

		if (selected.getMultiverseid() != null && !selected.getMultiverseid().equals("0"))
			url = HTTP_API_SCRYFALL+"multiverse/" + selected.getMultiverseid() + IMAGE_TAG;


		if (crop)
			url += "&version=art_crop";
		else
			url += "&version=" + getProperty("PIC_SIZE", "large");

		return new URL(url);
	}
	
	@Override
	public BufferedImage getOnlinePicture(MagicCard mc, MagicEdition ed) throws IOException {
		MagicEdition selected = ed;
		if (ed == null)
			selected = mc.getCurrentSet();

		URL url = generateLink(mc, selected, false);
		try {
			return URLTools.extractImage(url);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getName() {
		return "ScryFall";
	}

	@Override
	public BufferedImage extractPicture(MagicCard mc) throws IOException {
		URL u = generateLink(mc, mc.getCurrentSet(), true);
		try {
			return URLTools.extractImage(u);
		} catch (Exception e) {
			logger.error(e);
			return getBackPicture();
		}
	}

	@Override
	public void initDefault() {
		super.initDefault();
		
		setProperty("CERT_SERV", "scryfall.com");
		setProperty("PIC_SIZE", "large");
		setProperty("ICON_SET_SIZE", "medium");
		setProperty(LOAD_CERTIFICATE, TRUE);
	}

	@Override
	public String getVersion() {
		return "2.0";
	}

}
