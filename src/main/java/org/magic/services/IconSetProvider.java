package org.magic.services;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractMTGPicturesCache;

public class IconSetProvider {

	private static IconSetProvider inst;

	private Map<String, ImageIcon> cache24;
	private Map<String, ImageIcon> cache16;
	private File localDirectory;
	Logger logger = MTGLogger.getLogger(this.getClass());

	private IconSetProvider() {
		cache24 = new TreeMap<String, ImageIcon>(String.CASE_INSENSITIVE_ORDER);
		cache16 = new TreeMap<String, ImageIcon>(String.CASE_INSENSITIVE_ORDER);

		localDirectory = new File(AbstractMTGPicturesCache.confdir, "sets_icons");

		if (!localDirectory.exists())
			localDirectory.mkdir();

		try {
			logger.debug("Init IconSet cache");
			long time_1 = System.currentTimeMillis();
			initCache();
			long time_2 = System.currentTimeMillis();
			logger.debug("Init IconSet cache : done " + (time_2 - time_1) / 1000 + " sec");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static IconSetProvider getInstance() {
		if (inst == null)
			inst = new IconSetProvider();

		return inst;
	}

	private BufferedImage extract(String id) throws IOException
	{
		
		File iconFile = new File(localDirectory,id+"_set.png");
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
				im = ImageIO.read(IconSetProvider.class.getResource("/set/icons/"+set+"_set.png"));
				
				if(!set.equals(id))
					iconFile.renameTo(new File(localDirectory,set+"_set.png"));
					
				ImageIO.write(im, "png", iconFile);
			}
			catch(Exception ex)
			{
				logger.error("couldnt load " + id);
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
		default:
			return set;
		}

	}

	private void initCache() throws Exception {
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
