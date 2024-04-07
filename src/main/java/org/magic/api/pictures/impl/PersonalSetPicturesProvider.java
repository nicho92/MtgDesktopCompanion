package org.magic.api.pictures.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

import javax.swing.Icon;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.interfaces.abstracts.AbstractPicturesProvider;
import org.magic.services.MTGConstants;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.ImageTools;

public class PersonalSetPicturesProvider extends AbstractPicturesProvider {

	public static final String PERSONAL_SET_PICTURES = "Personal Set Pictures";
	private static final String FORMAT = "FORMAT";
	private static final String PICS_DIR = "PICS_DIR";

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	BufferedImage back;

	public void savePicture(BufferedImage bi, MTGCard mc, MTGEdition ed) throws IOException {
		var mainDir = getFile(PICS_DIR);
		var edDir = new File(mainDir, ed.getId());

		if (!edDir.exists())
			edDir.mkdir();

		
		ImageTools.saveImage(bi, Paths.get(edDir.getAbsolutePath(), mc.getScryfallId() + "." + getString(FORMAT).toLowerCase()).toFile(), getString(FORMAT));
	}

	public void removePicture(MTGEdition ed, MTGCard mc) {
		var mainDir = getFile(PICS_DIR);
		var edDir = new File(mainDir, ed.getId());

		try {
			FileTools.deleteFile(new File(edDir, mc.getScryfallId() + "." + getString(FORMAT)));
		} catch (IOException e) {
			logger.error("error removing {}",new File(edDir, mc.getScryfallId() + "." + getString(FORMAT)),e);
		}
	}

	public PersonalSetPicturesProvider() {
		super();

		if (!getFile(PICS_DIR).exists())
			getFile(PICS_DIR).mkdir();

	}

	@Override
	public BufferedImage extractPicture(MTGCard mc) throws IOException {
		return null;
	}


	@Override
	public BufferedImage getPicture(MTGCard mc) throws IOException {
		return getOnlinePicture(mc);
	}


	@Override
	public String generateUrl(MTGCard mc) {
		var mainDir = getFile(PICS_DIR);
		var edDir = new File(mainDir,mc.getEdition().getId());

		return new File(edDir, mc.getScryfallId() + "." + getString(FORMAT)).getAbsolutePath();
	}

	@Override
	public BufferedImage getOnlinePicture(MTGCard mc) throws IOException {

		try {
			return ImageTools.read(new File(generateUrl(mc)));
		}
		catch(Exception e)
		{
			logger.debug("{} is not found",generateUrl(mc));
			return null;
		}
	}

	@Override
	public String getName() {
		return PERSONAL_SET_PICTURES;
	}

	@Override
	public Map<String, String> getDefaultAttributes() {

		return Map.of(PICS_DIR,Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(),"privateSets","pics").toFile().getAbsolutePath(),
							   FORMAT,"PNG");
	}


	@Override
	public boolean equals(Object obj) {

		if(obj ==null)
			return false;

		return hashCode()==obj.hashCode();
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_LOGO;
	}

}
