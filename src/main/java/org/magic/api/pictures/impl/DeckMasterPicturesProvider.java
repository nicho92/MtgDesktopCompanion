package org.magic.api.pictures.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.abstracts.AbstractPicturesProvider;

public class DeckMasterPicturesProvider extends AbstractPicturesProvider {

	@Override
	public STATUT getStatut() {
		return STATUT.DEPRECATED;
	}

	@Override
	public String generateUrl(MagicCard mc) {
		return getString("URL") + "/card.php?multiverseid=" + mc.getCurrentSet().getMultiverseid();
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
							   "ICON_SET_SIZE", "medium");
	}

	@Override
	public String getVersion() {
		return "0.5";
	}



}
