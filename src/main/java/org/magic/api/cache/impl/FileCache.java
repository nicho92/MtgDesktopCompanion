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

	private enum KEYS {
		FORMAT, DIRECTORY
	}

	public File getDirectory() {
		return directory;
	}

	@Override
	public STATUT getStatut() {
		return STATUT.STABLE;
	}

	File directory;

	private String generateIdIndex(MagicCard mc, MagicEdition ed) {
		return IDGenerator.generate(mc, ed);
	}

	public FileCache() {
		super();

		directory = new File(getString(KEYS.DIRECTORY.name()));
		if (!directory.exists())
			directory.mkdir();
	}

	@Override
	public String getName() {
		return "File Cache";
	}

	@Override
	public BufferedImage getPic(MagicCard mc, MagicEdition ed) {
		try {

			if (ed == null)
				ed = mc.getEditions().get(0);

			logger.trace("search in cache : " + mc + " " + ed);

			File save = new File(directory, MTGControler.getInstance().getEnabledPicturesProvider().getName());
			if (!save.exists())
				save.mkdir();

			save = new File(save, removeCon(ed.getId()));
			if (!save.exists())
				save.mkdir();

			return ImageIO.read(new File(save, generateIdIndex(mc, ed) + "." + getString(KEYS.FORMAT.name())));
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public void put(BufferedImage im, MagicCard mc, MagicEdition ed) throws IOException {

		if (ed == null)
			ed = mc.getEditions().get(0);

		logger.debug("save in cache : " + mc + " " + ed);

		File f = new File(directory, MTGControler.getInstance().getEnabledPicturesProvider().getName());
		if (!f.exists())
			f.mkdir();

		f = new File(f, removeCon(ed.getId()));
		if (!f.exists())
			f.mkdir();

		ImageIO.write(im, getString(KEYS.FORMAT.name()),
				new File(f, generateIdIndex(mc, ed) + "." + getString(KEYS.FORMAT.name())));

	}

	private String removeCon(String a) {
		if (a.equalsIgnoreCase("con"))
			return a + "_set";

		return a;
	}

	@Override
	public void clear() {
		try {
			FileUtils.cleanDirectory(directory);
		} catch (IOException e) {
			logger.error("Couldn't clean " + directory, e);
		}

	}

	@Override
	public void initDefault() {
		setProperty(KEYS.DIRECTORY.name(), MTGConstants.CONF_DIR + "/caches/cachePics");
		setProperty(KEYS.FORMAT.name(), "png");

	}

	@Override
	public String getVersion() {
		return "1";
	}

}
