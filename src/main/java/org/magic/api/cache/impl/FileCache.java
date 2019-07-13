package org.magic.api.cache.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.abstracts.AbstractCacheProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.ImageTools;

public class FileCache extends AbstractCacheProvider {

	private static final String DIRECTORY = "DIRECTORY";
	private static final String FORMAT = "FORMAT";
	private File dir;


	public File getDirectory() {
		return dir;
	}


	

	public FileCache() {
		super();

		dir = getFile(DIRECTORY);
		if (!dir.exists())
			dir.mkdir();
	}

	@Override
	public String getName() {
		return "File";
	}

	@Override
	public BufferedImage getPic(MagicCard mc, MagicEdition ed) {
		try {

			if (ed == null)
				ed = mc.getCurrentSet();

			logger.trace("search in cache : " + mc + " " + ed);

			File save = new File(dir, MTGControler.getInstance().getEnabled(MTGPictureProvider.class).getName());
			if (!save.exists())
				save.mkdir();

			save = new File(save, removeCon(ed.getId()));
			if (!save.exists())
				save.mkdir();

			return ImageIO.read(new File(save, generateIdIndex(mc, ed) + "." + getString(FORMAT)));
		} catch (IOException e) {
			logger.trace("search in cache : " + mc + " " + ed +" not found :" + e);

			return null;
		}
	}
	
	@Override
	public void clear(MagicEdition ed) {
		try {
			FileUtils.deleteDirectory(Paths.get(dir.getAbsolutePath(), ed.getId()).toFile());
		} catch (IOException e) {
			logger.error(e);
		}
		
	}

	@Override
	public void put(BufferedImage im, MagicCard mc, MagicEdition ed) throws IOException {

		if (ed == null)
			ed = mc.getCurrentSet();

		logger.debug("save in cache : " + mc + " " + ed);

		File f = new File(dir, MTGControler.getInstance().getEnabled(MTGPictureProvider.class).getName());
		if (!f.exists())
			f.mkdir();

		f = new File(f, removeCon(ed.getId()));
		if (!f.exists())
			f.mkdir();

		ImageTools.saveImage(im, new File(f, generateIdIndex(mc, ed) + "." + getString(FORMAT)), getString(FORMAT));
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
		setProperty(DIRECTORY, Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(),"cachePics").toFile().getAbsolutePath());
		setProperty(FORMAT, "png");

	}

	@Override
	public String getVersion() {
		return "1";
	}
	
	@Override
	public long size() {
		
		if(dir!=null)
			return FileUtils.sizeOfDirectory(dir);
		
		
		return 0;
	}

}
