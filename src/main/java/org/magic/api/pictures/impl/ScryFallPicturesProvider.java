package org.magic.api.pictures.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractPicturesProvider;
import org.magic.api.providers.impl.ScryFallProvider;
import org.magic.services.MTGControler;
import org.magic.tools.InstallCert;
import org.magic.tools.URLTools;

public class ScryFallPicturesProvider extends AbstractPicturesProvider {

	private static final String HTTP_API_SCRYFALL = "https://api.scryfall.com/cards/";
	private static final String IMAGE_TAG = "?format=image";
	private static final String LOAD_CERTIFICATE = "LOAD_CERTIFICATE";
	private Boolean scryfallProvider = null;
	
	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

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

	private URL generateLink(MagicCard mc, MagicEdition selected, boolean crop) throws MalformedURLException {

		if (scryfallProvider == null)
			scryfallProvider = MTGControler.getInstance().getEnabled(MTGCardsProvider.class) instanceof ScryFallProvider;

		String url = HTTP_API_SCRYFALL + selected.getId().toLowerCase() + "/" + selected.getNumber()+ IMAGE_TAG;

		if (scryfallProvider) {
			url = HTTP_API_SCRYFALL + mc.getId() + IMAGE_TAG;
		}

		if (selected.getMultiverseid() != null && !selected.getMultiverseid().equals("0"))
			url = HTTP_API_SCRYFALL+"/multiverse/" + selected.getMultiverseid() + IMAGE_TAG;

		if (crop)
			url += "&version=art_crop";
		else
			url += "&version=" + getProperty("PIC_SIZE", "large");

		return new URL(url);
	}
	
	/*@Override
	public BufferedImage getForeignNamePicture(MagicCardNames fn, MagicCard mc) throws IOException {
		return resizeCard(URLTools.extractImage(HTTP_API_SCRYFALL+"/multiverse/" + fn.getGathererId() + IMAGE_TAG), newW, newH);
	}
	*/

	@Override
	public BufferedImage getOnlinePicture(MagicCard mc, MagicEdition ed) throws IOException {
		MagicEdition selected = ed;
		if (ed == null)
			selected = mc.getCurrentSet();

		URL url = generateLink(mc, selected, false);
		logger.debug("load pics " + url);
		try {
			return URLTools.extractImage(url);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public BufferedImage getSetLogo(String set, String rarity) throws IOException {
		URL url = new URL("http://gatherer.wizards.com/Handlers/Image.ashx?type=symbol&set=" + set
				+ "&size=medium&rarity=" + rarity.substring(0, 1));
		return ImageIO.read(url);
	}

	@Override
	public String getName() {
		return "ScryFall";
	}

	@Override
	public BufferedImage extractPicture(MagicCard mc) throws IOException {
		URL u = generateLink(mc, mc.getCurrentSet(), true);

		HttpURLConnection connection = URLTools.openConnection(u);
		logger.debug("load pics " + connection.getURL().toString());

		try {
			return ImageIO.read(connection.getInputStream());
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
		setProperty(LOAD_CERTIFICATE, "true");
	}

	@Override
	public String getVersion() {
		return "2.0";
	}

}
