package org.magic.api.cache.impl;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.abstracts.AbstractCacheProvider;
import org.magic.services.MTGConstants;
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
	public BufferedImage getItem(MagicCard mc) {
		try {

			logger.trace("search in cache : " + mc + " " + mc.getCurrentSet());

			var save = new File(dir, getEnabledPlugin(MTGPictureProvider.class).getName());
			if (!save.exists())
				save.mkdir();

			save = new File(save, removeCon(mc.getCurrentSet().getId()));
			if (!save.exists())
				save.mkdir();

			return ImageTools.read(new File(save, generateIdIndex(mc) + "." + getString(FORMAT)));
		} catch (IOException e) {
			logger.trace("search in cache : " + mc + " " + mc.getCurrentSet() +" not found :" + e);

			return null;
		}
	}


	@Override
	public void put(BufferedImage im, MagicCard mc) throws IOException {
		logger.debug("save in cache : " + mc + " " + mc.getCurrentSet());

		var f = new File(dir, getEnabledPlugin(MTGPictureProvider.class).getName());
		if (!f.exists())
			f.mkdir();

		f = new File(f, removeCon(mc.getCurrentSet().getId()));
		if (!f.exists())
			f.mkdir();

		ImageTools.saveImage(im, new File(f, generateIdIndex(mc) + "." + getString(FORMAT)), getString(FORMAT));
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
	

	@Override
	public int hashCode() {
		return getName().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj ==null)
			return false;
		
		return hashCode()==obj.hashCode();
	}
	

}
