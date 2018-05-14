package org.magic.api.pictures.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractPicturesProvider;
import org.magic.services.MTGConstants;

public class PersonalSetPicturesProvider extends AbstractPicturesProvider {

	private static final String PICS_DIR = "PICS_DIR";

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	BufferedImage back;

	public void savePicture(BufferedImage bi, MagicCard mc, MagicEdition ed) throws IOException {
		File mainDir = getFile(PICS_DIR);
		File edDir = new File(mainDir, ed.getId());

		if (!edDir.exists())
			edDir.mkdir();

		ImageIO.write(bi, getString("FORMAT"),
				Paths.get(edDir.getAbsolutePath(), mc.getId() + "." + getString("FORMAT").toLowerCase()).toFile());
	}

	public void removePicture(MagicEdition ed, MagicCard mc) {
		File mainDir = getFile(PICS_DIR);
		File edDir = new File(mainDir, ed.getId());
		FileUtils.deleteQuietly(new File(edDir, mc.getId() + "." + getString("FORMAT")));
	}

	public PersonalSetPicturesProvider() {
		super();

		if (!getFile(PICS_DIR).exists())
			getFile(PICS_DIR).mkdir();

	}

	@Override
	public BufferedImage extractPicture(MagicCard mc) throws IOException {
		return null;
	}

	@Override
	public BufferedImage getPicture(MagicCard mc, MagicEdition ed) throws IOException {
		File mainDir = getFile(PICS_DIR);
		File edDir = new File(mainDir, ed.getId());
		logger.debug("load pic directory " + edDir + " pics :" + mc.getId());
		if (edDir.exists())
			return ImageIO.read(new File(edDir, mc.getId() + "." + getString("FORMAT")));
		else
			return null;
	}

	@Override
	public BufferedImage getSetLogo(String set, String rarity) throws IOException {
		return null;
	}

	@Override
	public String getName() {
		return "Personal Set Pictures";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}
	
	@Override
	public void initDefault() {
		super.initDefault();
		
		setProperty(PICS_DIR,Paths.get(MTGConstants.CONF_DIR.getAbsolutePath(),"sets","privatePics").toFile().getAbsolutePath());
		setProperty("FORMAT","PNG");
	}

}
