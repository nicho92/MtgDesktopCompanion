package org.magic.api.cache.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMTGPicturesCache;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.IDGenerator;

public class FileCache extends AbstractMTGPicturesCache {

	private static final String DIRECTORY = "DIRECTORY";
	private static final String FORMAT = "FORMAT";
	private File dir;


	public File getDirectory() {
		return dir;
	}

	@Override
	public STATUT getStatut() {
		return STATUT.STABLE;
	}

	

	private String generateIdIndex(MagicCard mc, MagicEdition ed) {
		return IDGenerator.generate(mc, ed);
	}

	public FileCache() {
		super();

		dir = new File(getString(DIRECTORY));
		if (!dir.exists())
			dir.mkdir();
	}

	@Override
	public String getName() {
		return "File Cache";
	}

	@Override
	public BufferedImage getPic(MagicCard mc, MagicEdition ed) {
		try {

			if (ed == null)
				ed = mc.getCurrentSet();

			logger.trace("search in cache : " + mc + " " + ed);

			File save = new File(dir, MTGControler.getInstance().getEnabledPicturesProvider().getName());
			if (!save.exists())
				save.mkdir();

			save = new File(save, removeCon(ed.getId()));
			if (!save.exists())
				save.mkdir();

			return ImageIO.read(new File(save, generateIdIndex(mc, ed) + "." + getString(FORMAT)));
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public void put(BufferedImage im, MagicCard mc, MagicEdition ed) throws IOException {

		if (ed == null)
			ed = mc.getCurrentSet();

		logger.debug("save in cache : " + mc + " " + ed);

		File f = new File(dir, MTGControler.getInstance().getEnabledPicturesProvider().getName());
		if (!f.exists())
			f.mkdir();

		f = new File(f, removeCon(ed.getId()));
		if (!f.exists())
			f.mkdir();

		ImageIO.write(im, getString(FORMAT),
				new File(f, generateIdIndex(mc, ed) + "." + getString(FORMAT)));

	}

	private String removeCon(String a) {
		if (a.equalsIgnoreCase("con"))
			return a + "_set";

		return a;
	}

	@Override
	public void clear() {
		try {
			FileUtils.cleanDirectory(dir);
		} catch (IOException e) {
			logger.error("Couldn't clean " + dir, e);
		}

	}

	@Override
	public void initDefault() {
		setProperty(DIRECTORY, MTGConstants.CONF_DIR + "/caches/cachePics");
		setProperty(FORMAT, "png");

	}

	@Override
	public String getVersion() {
		return "1";
	}

}
