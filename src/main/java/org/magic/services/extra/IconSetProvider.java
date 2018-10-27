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
import org.magic.api.cache.impl.FileCache;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.tools.Chrono;

public class IconSetProvider {

	private static IconSetProvider inst;
	private Map<String, ImageIcon> cache24;
	private Map<String, ImageIcon> cache16;
	private File localDirectory;
	private Logger logger = MTGLogger.getLogger(this.getClass());
	private static final String EXT = "_set.png";
	
	public void clean() throws IOException {
		FileUtils.cleanDirectory(localDirectory);
	}

	private IconSetProvider() {
		cache24 = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		cache16 = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		Chrono chrono = new Chrono();
		localDirectory = new File(new FileCache().getConfdir(), "sets_icons");

		if (!localDirectory.exists())
			localDirectory.mkdir();

		try {
			chrono.start();
			logger.debug("Init IconSet cache");
			initCache();
			logger.debug("Init IconSet cache : done " + chrono.stop() + " sec");
		} catch (Exception e) {
			logger.error("error init cache", e);
		}
	}

	public static IconSetProvider getInstance() {
		if (inst == null)
			inst = new IconSetProvider();

		return inst;
	}

	private BufferedImage extract(String id) throws IOException {

		File iconFile = new File(localDirectory, id + EXT);
		if (iconFile.exists()) {
			logger.trace("load from cache " + iconFile);
			return ImageIO.read(iconFile);
		} 
		else {
			BufferedImage im = null;
			logger.trace("load from jar " + id);

			try {
				String equivSet = getEquiv(id);
				im = ImageIO.read(IconSetProvider.class.getResource(MTGConstants.SET_ICON_DIR + equivSet + EXT));
				ImageIO.write(im, "png", iconFile);
			} catch (Exception ex) {
				logger.trace("couldnt load icons for " + id +"=" + getEquiv(id));
				im = ImageIO.read(IconSetProvider.class.getResource(MTGConstants.SET_ICON_DIR+"PMTG1_set.png"));

			}
			return im;
		}

	}

	private String getEquiv(String set) 
	{

		switch (set) {
		case "RIN" :
			return "REN";
		case "NMS" :
			return "NEM";
		case "PI13":
			return "PIDW";
		case "PI14":
			return "PIDW";
		case "PSOI":
			return "SOI";
		case "FNM":
			return "PFNM";
		case "F01":
			return "PFNM";
		case "F02":
			return "PFNM";
		case "F03":
			return "PFNM";
		case "F04":
			return "PFNM";
		case "F05":
			return "PFNM";
		case "F06":
			return "PFNM";
		case "F07":
			return "PFNM";
		case "F08":
			return "PFNM";
		case "F09":
			return "PFNM";
		case "F10":
			return "PFNM";
		case "F11":
			return "PFNM";
		case "F12":
			return "PFNM";
		case "F13":
			return "PFNM";
		case "F14":
			return "PFNM";
		case "F15":
			return "PFNM";
		case "F16":
			return "PFNM";
		case "F17":
			return "PFNM";
		case "F18":
			return "PFNM";
		case "PO2":
			return "P02";
		case "CON_":
			return "CON";
		case "PAL01":
			return "PARL";	
		case "PAL02":
			return "PARL";
		case "PAL03":
			return "PARL";
		case "PAL04":
			return "PARL";
		case "PAL05":
			return "PARL";
		case "PAL06":
			return "PARL";
		case "PAL99":
			return "PARL";
		case "PAL00":
			return "PARL";	
		case "PHPR":
			return "PBOOK";
		case "PDTP":
			return "PXBOX";
		case "PSAL":
			return "PHUK";
		case "PMPS06":
			return "PMPS";
		case "PMPS07":
			return "PMPS";
		case "PMPS08":
			return "PMPS";	
		case "PMPS09":
			return "PMPS";	
		case "PMPS10":
			return "PMPS";	
		case "PMPS11":
			return "PMPS";	
		case "G99": 
			return "PDCI";
		case "G00": 
			return "PDCI";
		case "G01": 
			return "PDCI";
		case "JGP":
			return "PDCI";
		case "G02": 
			return "PDCI";
		case "G03": 
			return "PDCI";
		case "G04": 
			return "PDCI";
		case "G05": 
			return "PDCI";
		case "G06": 
			return "PDCI";
		case "G07": 
			return "PDCI";
		case "G08": 
			return "PDCI";
		case "G09": 
			return "PDCI";
		case "G10": 
			return "PDCI";
		case "G11": 
			return "PDCI";
		case "PGTW":
			return "PDCI";
		case "PJJT":
			return "PDCI";
		case "PSUS":
			return "PDCI";
		
		case "PJAS":
			return "PDCI";
		case "PJSE":
			return "PDCI";
		case "PURL":
			return "PMEI";
		case "PWCQ":
			return "PMEI";
		case "PLNY":
			return "PMEI";
		case "J12": 
			return "PMEI";	
		case "J13": 
			return "PMEI";	
		case "J14": 
			return "PMEI";		
		case "J15": 
			return "PMEI";	
		case "J16": 
			return "PMEI";		
		case "J17": 
			return "PMEI";	
		case "J18": 
			return "PMEI";	
		case "J19": 
			return "PMEI";	
		case "FBB": 
			return "3ED";
		case "OC13":
			return "C13";
		case "OC14":
			return "C14";	
		case "OC15":
			return "C15";
		case "OC16":
			return "C16";
		case "OC17":
			return "C17";
		case "OC18":
			return "C18";
		case "OCMD":
			return "CMD";
		case "SUM":	
			return "PSUM";
		case "CP1":	
			return "PMEI";
		case "CP2":	
			return "PMEI";	
		case "CP3":	
			return "PMEI";
		case "DVD":
			return "DDC";
		case "EVG":
			return "DD1";
		case "GVL":
			return "DDD";
		case "JVC":
			return "DD2";
		case "HHO":
			return "PMEI";
			
		default:return set;
		}

	}

	private void initCache() throws IOException {
		for (MagicEdition e : MTGControler.getInstance().getEnabled(MTGCardsProvider.class).loadEditions()) {
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
