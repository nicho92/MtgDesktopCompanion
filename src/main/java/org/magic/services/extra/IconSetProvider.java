package org.magic.services.extra;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractMTGPicturesCache;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class IconSetProvider {

	private static IconSetProvider inst;

	private Map<String, ImageIcon> cache24;
	private Map<String, ImageIcon> cache16;
	private File localDirectory;
	private Logger logger = MTGLogger.getLogger(this.getClass());
	private static final String EXT="_set.png";

	
	public void clean() throws IOException
	{
		FileUtils.cleanDirectory(localDirectory);
	}
	
	
	
	private IconSetProvider() {
		cache24 = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		cache16 = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

		localDirectory = new File(AbstractMTGPicturesCache.CACHEDIR, "sets_icons");

		if (!localDirectory.exists())
			localDirectory.mkdir();

		try {
			logger.debug("Init IconSet cache");
			long time1 = System.currentTimeMillis();
			initCache();
			long time2 = System.currentTimeMillis();
			logger.debug("Init IconSet cache : done " + (time2 - time1) / 1000 + " sec");
		} catch (Exception e) {
			logger.error("error init cache",e);
		}
	}

	public static IconSetProvider getInstance() {
		if (inst == null)
			inst = new IconSetProvider();

		return inst;
	}

	private BufferedImage extract(String id) throws IOException
	{
		
		File iconFile = new File(localDirectory,id+EXT);
		if(iconFile.exists())
		{
			logger.trace("load from cache " + iconFile);
			return ImageIO.read(iconFile);
		}
		else
		{
			BufferedImage im=null;
			logger.trace("load from jar " + id);
			
			try
			{
				String set=getEquiv(id);
				im = ImageIO.read(IconSetProvider.class.getResource("/set/icons/"+set+EXT));
				
				if(!set.equals(id))
					{
					FileUtils.moveFile(iconFile, new File(localDirectory,set+EXT));
					}
					
				ImageIO.write(im, "png", iconFile);
			}
			catch(Exception ex)
			{
				logger.trace("couldnt load " + id);
				im = ImageIO.read(IconSetProvider.class.getResource("/set/icons/PMTG1_set.png"));
				
			}
			return im;
		}
		
	}

	private String getEquiv(String set) {

		switch (set) {
		case "PI13":return "PIDW";
		case "PI14":return "PIDW";
		case "PSOI":return "SOI";
		case "FNM":return "PFNM";
		case "F01":return "PFNM";
		case "F02":return "PFNM";
		case "F03":return "PFNM";
		case "F04":return "PFNM";
		case "F05":return "PFNM";
		case "F06":return "PFNM";
		case "F07":return "PFNM";
		case "F08":return "PFNM";
		case "F09":return "PFNM";
		case "F10":return "PFNM";
		case "F11":return "PFNM";
		case "F12":return "PFNM";
		case "F13":return "PFNM";
		case "F14":return "PFNM";
		case "F15":return "PFNM";
		case "F16":return "PFNM";
		case "F17":return "PFNM";
		case "F18":return "PFNM";
		case "P02":return "PO2";
		case "PARL":return "PARL2";
		default:
			return set;
		}

	}

	private void initCache() throws IOException {
		for (MagicEdition e : MTGControler.getInstance().getEnabledProviders().loadEditions()) {
			BufferedImage im = extract(e.getId().toUpperCase());
			cache24.put(e.getId(), new ImageIcon(im.getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
			cache16.put(e.getId(), new ImageIcon(im.getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
		}
	}

	public ImageIcon get24(String id) {
		return cache24.get(id);
	}

	public ImageIcon get16(String id) {
		return cache16.get(id);
	}

}
