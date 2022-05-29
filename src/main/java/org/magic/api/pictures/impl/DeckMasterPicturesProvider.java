package org.magic.api.pictures.impl;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.abstracts.AbstractPicturesProvider;
import org.magic.tools.InstallCert;

public class DeckMasterPicturesProvider extends AbstractPicturesProvider {

	private static final String LOAD_CERTIFICATE = "LOAD_CERTIFICATE";

	@Override
	public STATUT getStatut() {
		return STATUT.DEPRECATED;
	}
	
	
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
	
	@Override
	public String generateUrl(MagicCard mc) {
		return getString("URL") + "/card.php?multiverseid=" + mc.getCurrentSet().getMultiverseid();
	}

	private BufferedImage resizeIconSet(BufferedImage img) {
		var mode = getString("ICON_SET_SIZE");

		var newW = 27;
		var newH = 30;

		if (mode.equalsIgnoreCase("large")) {
			newW = 118;
			newH = 130;
		}

		var tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		var dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = dimg.createGraphics();
		
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}

	@Override
	public String getName() {
		return "Deck Master";
	}

	@Override
	public BufferedImage extractPicture(MagicCard mc) throws IOException {
		return getPicture(mc).getSubimage(15, 34, 184, 132);
	}

	@Override
	public Map<String, String> getDefaultAttributes() {
		
		return Map.of("CALL_MCI_FOR", "p,CEI,CED,CPK,CST",
							   "URL", "https://deckmaster.info/",
							   "ICON_SET_SIZE", "medium",
							   LOAD_CERTIFICATE, TRUE);
	}

	@Override
	public String getVersion() {
		return "0.5";
	}

	

}
