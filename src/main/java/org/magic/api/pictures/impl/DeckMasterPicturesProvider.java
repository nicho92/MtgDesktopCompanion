package org.magic.api.pictures.impl;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.abstracts.AbstractPicturesProvider;
import org.magic.services.PluginRegistry;
import org.magic.tools.InstallCert;
import org.magic.tools.URLTools;

public class DeckMasterPicturesProvider extends AbstractPicturesProvider {

	private static final String LOAD_CERTIFICATE = "LOAD_CERTIFICATE";

	public DeckMasterPicturesProvider() {
		super();

		if(getBoolean(LOAD_CERTIFICATE))
		{
			try {
				InstallCert.installCert("deckmaster.info");
				setProperty(LOAD_CERTIFICATE, "false");
			} catch (Exception e1) {
				logger.error(e1);
			}
		}
	}

	private BufferedImage extractbyMultiverseId(String multiverseid) throws IOException {
		try {
			Document d = URLTools.extractHtml(getString("URL") + "/card.php?multiverseid=" + multiverseid);
			logger.debug("read " + getString("URL") + "/card.php?multiverseid=" + multiverseid);
			Element e = d.select(".card > img").get(0);
			return URLTools.extractImage(e.attr("src"));
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}

	private BufferedImage resizeIconSet(BufferedImage img) {
		String mode = getString("ICON_SET_SIZE");

		int newW = 27;
		int newH = 30;

		if (mode.equalsIgnoreCase("large")) {
			newW = 118;
			newH = 130;
		}

		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}

	@Override
	public BufferedImage getOnlinePicture(MagicCard mc, MagicEdition ed) throws IOException {


		MagicEdition selected = ed;
		if (ed == null)
			selected = mc.getCurrentSet();

		for (String k : getArray("CALL_MCI_FOR")) {
			if (selected.getId().startsWith(k)) {
				return PluginRegistry.inst().getPlugin("Scryfall", MTGPictureProvider.class).getPicture(mc, selected);
			}
		}
		return extractbyMultiverseId(selected.getMultiverseid());
	}

	@Override
	public BufferedImage getSetLogo(String setID, String rarity) throws IOException {

		switch (setID) {
		case "ICE":
			setID = "IA";
			break;
		case "FEM":
			setID = "FE";
			break;
		case "LEA":
			setID = "1E";
			break;
		case "LEB":
			setID = "2E";
			break;
		case "2ED":
			setID = "2U";
			break;
		case "LEG":
			setID = "LE";
			break;
		case "ATQ":
			setID = "AQ";
			break;
		case "ARN":
			setID = "AN";
			break;
		default:
			break;
		}

		URL u = new URL(getString("URL") + "/images/sets/" + setID.toUpperCase() + "_"+ rarity.substring(0, 1).toUpperCase() + ".png");
		HttpURLConnection con = URLTools.openConnection(u);
		BufferedImage im = ImageIO.read(con.getInputStream());
		return resizeIconSet(im);
	}

	@Override
	public String getName() {
		return "Deck Master";
	}

	@Override
	public BufferedImage extractPicture(MagicCard mc) throws IOException {
		return getPicture(mc, null).getSubimage(15, 34, 184, 132);
	}

	@Override
	public void initDefault() {
		super.initDefault();
		setProperty("CALL_MCI_FOR", "p,CEI,CED,CPK,CST");
		
		setProperty("URL", "https://deckmaster.info/");
		setProperty("ICON_SET_SIZE", "medium");
		setProperty(LOAD_CERTIFICATE, "true");
	}

	@Override
	public String getVersion() {
		return "0.5";
	}

	

}
