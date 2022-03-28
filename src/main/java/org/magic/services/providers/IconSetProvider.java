package org.magic.services.providers;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ImageIcon;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;
import org.magic.services.network.URLTools;
import org.magic.tools.Chrono;
import org.magic.tools.ImageTools;

import com.kitfox.svg.app.beans.SVGIcon;

public class IconSetProvider {

	private static IconSetProvider inst;
	private Map<String, ImageIcon> cache24;
	private Map<String, ImageIcon> cache16;
	private File localDirectory;
	private Logger logger = MTGLogger.getLogger(this.getClass());
	private static final String EXT = "_set.png";
	private Map<String,String> equiv;
	
	
	public void clean() throws IOException {
		FileUtils.cleanDirectory(localDirectory);
	}

	private IconSetProvider() {
		cache24 = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		cache16 = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		var chrono = new Chrono();
		
		localDirectory = new File(MTGConstants.DATA_DIR, "sets_icons");
		
		initEquiv();
		
		if (!localDirectory.exists())
			localDirectory.mkdir();

		try {
			chrono.start();
			logger.trace("Init IconSet cache");
			initCache();
			logger.trace("Init IconSet cache : done " + chrono.stop() + " sec");
		} catch (Exception e) {
			logger.error("error init cache", e);
		}
	}

	public static IconSetProvider getInstance() {
		if (inst == null)
			inst = new IconSetProvider();

		return inst;
	}

	public ImageIcon getSVGIcon(String id)
	{
		var ic = new SVGIcon();
	
			
			var localF = new File(localDirectory, getEquiv(id)+".svg");
			
			if(!localF.exists())
			{
				try {
					URLTools.download("https://raw.githubusercontent.com/andrewgioia/keyrune/master/svg/"+getEquiv(id).toLowerCase()+".svg", localF);
				}
				catch(Exception e)
				{
					return getSVGIcon("PMTG1");
				}
			}
				
			ic.setSvgURI(localF.toURI());
			
				
	
		ic.setAntiAlias(true);
		ic.setAutosize(1);
		
		return ic;
	}
	
	
	private BufferedImage extract(String id) throws IOException {

		var iconFile = new File(localDirectory, id + EXT);
		if (iconFile.exists()) {
			logger.trace("load from cache " + iconFile);
			return ImageTools.read(iconFile);
		} 
		else {
			BufferedImage im = null;
			logger.trace("load from jar " + id);

			try {
				String equivSet = getEquiv(id);
				im = ImageTools.readLocal(IconSetProvider.class.getResource(MTGConstants.SET_ICON_DIR + equivSet + EXT));
				ImageTools.saveImage(im, iconFile, "png");
			} catch (Exception ex) {
				logger.trace("couldnt load icons for " + id +"=" + getEquiv(id));
				im = ImageTools.readLocal(IconSetProvider.class.getResource(MTGConstants.SET_ICON_DIR+"PMTG1_set.png"));
			}
			return im;
		}

	}

	private void initEquiv()
	{
		equiv = new HashMap<>();
		
		equiv.put("NMS", "NEM");
		equiv.put("PI13", "PIDW");
		equiv.put("PI14", "PIDW");
		equiv.put("PIDW", "PIDW");
		equiv.put("PSOI", "SOI");
		equiv.put("PAST", "PAST");
		equiv.put("FNM", "PFNM");
		equiv.put("F01", "PFNM");
		equiv.put("F02", "PFNM");
		equiv.put("F03", "PFNM");
		equiv.put("F04", "PFNM");
		equiv.put("F05", "PFNM");
		equiv.put("F06", "PFNM");
		equiv.put("F07", "PFNM");
		equiv.put("F08", "PFNM");
		equiv.put("F09", "PFNM");
		equiv.put("F10", "PFNM");
		equiv.put("F11", "PFNM");
		equiv.put("F12", "PFNM");
		equiv.put("F13", "PFNM");
		equiv.put("F14", "PFNM");
		equiv.put("F15", "PFNM");
		equiv.put("F16", "PFNM");
		equiv.put("F17", "PFNM");
		equiv.put("F18", "PFNM");
		equiv.put("PO2", "P02");
		equiv.put("CON_", "CON");
		equiv.put("PARL", "PARL");
		equiv.put("PAL01", "PARL");
		equiv.put("PAL02", "PARL");
		equiv.put("PAL03", "PARL");
		equiv.put("PAL04", "PARL");
		equiv.put("PAL05", "PARL");
		equiv.put("PAL06", "PARL");
		equiv.put("PAL99", "PARL");
		equiv.put("PAL00", "PARL");
		equiv.put("PHPR", "PBOOK");
		equiv.put("PDTP", "PXBOX");
		equiv.put("PSAL", "PHUK");
		equiv.put("PMPS06", "PMPS");
		equiv.put("PMPS07", "PMPS");
		equiv.put("PMPS08", "PMPS");
		equiv.put("PMPS09", "PMPS");
		equiv.put("PMPS10", "PMPS");
		equiv.put("PMPS11", "PMPS");
		equiv.put("G99", "PDCI");
		equiv.put("G00", "PDCI");
		equiv.put("G01", "PDCI");
		equiv.put("JGP", "PDCI");
		equiv.put("G02", "PDCI");
		equiv.put("G03", "PDCI");
		equiv.put("G04", "PDCI");
		equiv.put("G05", "PDCI");
		equiv.put("G06", "PDCI");
		equiv.put("G07", "PDCI");
		equiv.put("G08", "PDCI");
		equiv.put("G09", "PDCI");
		equiv.put("G10", "PDCI");
		equiv.put("G11", "PDCI");
		equiv.put("PGTW", "PDCI");
		equiv.put("PJJT", "PDCI");
		equiv.put("PSUS", "PDCI");
		equiv.put("PJAS", "PDCI");
		equiv.put("PJSE", "PDCI");
		
		equiv.put("PW09", "PDCI");
		equiv.put("PW08", "PDCI");
		equiv.put("PW21", "PMEI");
		equiv.put("PW22", "PMEI");
		equiv.put("PW10", "PMEI");
		equiv.put("PW11", "PMEI");
		equiv.put("PW12", "PMEI");
		
		equiv.put("PWPN","PDCI");
		equiv.put("PG08", "PDCI");
		equiv.put("PG07", "PDCI");
		equiv.put("MPR", "PDCI");
		equiv.put("PMPS","RAV");
		equiv.put("PR2", "PDCI");
		equiv.put("P03", "PDCI");
		equiv.put("P04", "PDCI");
		equiv.put("P05", "PDCI");
		equiv.put("P06", "PDCI");
		equiv.put("P07", "PDCI");
		equiv.put("P08", "PDCI");
		equiv.put("P09", "PDCI");
		equiv.put("P10", "PDCI");
		equiv.put("P11", "PDCI");
		equiv.put("FBB", "3ED");
		equiv.put("4BB", "4ED");
		equiv.put("OC13", "C13");
		equiv.put("OC14", "C14");
		equiv.put("OC15", "C15");
		equiv.put("OC16", "C16");
		equiv.put("OC17", "C17");
		equiv.put("OC18", "C18");
		equiv.put("OC19", "C19");
		equiv.put("OCMD", "CMD");
		equiv.put("SUM", "PSUM");
		equiv.put("CP1", "PMEI");
		equiv.put("CP2", "PMEI");
		equiv.put("CP3", "PMEI");
		equiv.put("PWCQ", "PMEI");
		equiv.put("PLNY", "PMEI");
		equiv.put("J12", "PMEI");
		equiv.put("J13", "PMEI");
		equiv.put("J14", "PMEI");
		equiv.put("J15", "PMEI");
		equiv.put("J16", "PMEI");
		equiv.put("J17", "PMEI");
		equiv.put("J18", "PMEI");
		equiv.put("J19", "PMEI");
		equiv.put("HHO", "PMEI");
		equiv.put("PURL", "PMEI");
		equiv.put("PL22", "PMEI");
		
		
		equiv.put("UGIN", "FRF");
		
		equiv.put("PF19", "PMEI");
		equiv.put("PSS3", "PMEI");
		equiv.put("HTR", "PMEI");
		equiv.put("PNAT", "PMEI");
		equiv.put("HTR17", "PMEI");
		equiv.put("PSS2", "PMEI");
		equiv.put("SLD", "PMEI");
		equiv.put("PSLD", "PMEI");
		equiv.put("SLU", "PMEI");
		equiv.put("PL21", "PMEI");
		equiv.put("PLG21","PMEI");
		equiv.put("HTR20","PMEI");
		equiv.put("DVD", "DDC");
		equiv.put("EVG", "DD1");
		equiv.put("GVL", "DDD");
		equiv.put("JVC", "DD2");
		equiv.put("PUMA", "UMA");
		equiv.put("OPC2", "PC2");
		equiv.put("OE01", "E01");
		equiv.put("OHOP", "HOP");
		equiv.put("OPCA", "PCA");
		equiv.put("MED", "MPS_MED");
		equiv.put("PSS1", "BFZ");
		equiv.put("PS11", "PSALVAT11");
		equiv.put("OCM1", "CM1");
		equiv.put("PBOK", "PBOOK");
		equiv.put("PGRU","PGRU");
		equiv.put("PPP1", "PMEI");
		equiv.put("CELD", "ELD");
		equiv.put("OARC", "ARC");
		equiv.put("OC20", "C20");
		equiv.put("HA2", "HA1");
		equiv.put("HA3", "HA1");
		equiv.put("HA4","HA1");
		equiv.put("HA5", "HA1");
		equiv.put("CMB1","MB1");
		equiv.put("PLGS", "PMEI");
		equiv.put("AJMP","JMP");
		equiv.put("ANB","ANA");
		equiv.put("AKR","E01");
		equiv.put("FJMP", "JMP");
		equiv.put("PLIST", "MB1");
		equiv.put("KLR", "KLD");
		equiv.put("ASTX", "STX");
		equiv.put("AKHM", "KHM");
		equiv.put("OC21","C21");
		equiv.put("H1R", "MH1");
		equiv.put("AMH2", "MH2");
		equiv.put("AAFR","AFR");
		equiv.put("C21","AFC");
		equiv.put("MAFR","AFR");
		equiv.put("OAFC", "AFR");
		equiv.put("CMB2", "MB1");
		equiv.put("FMB1", "MB1");
		equiv.put("SMID","MID");
		equiv.put("Q06", "PMEI");
		equiv.put("AMID", "MID");
		equiv.put("AVOW","VOW");
		equiv.put("MMH2", "MH2");
		equiv.put("MSTX", "STX");
		equiv.put("MKHM", "KHM");
		equiv.put("PF21", "PMEI");
		equiv.put("OMIC", "MID");
		equiv.put("OVOC", "VOW");
		equiv.put("YNEO", "Y22");
		equiv.put("YMID", "Y22");
		
		}
	
	private String getEquiv(String set) 
	{
		if(equiv.get(set)!=null)
			return equiv.get(set);
	

		if(set.length()==4 && set.toLowerCase().startsWith("p"))
			set=set.substring(1);
		
		
		
		return set;
	}

	private void initCache() throws IOException {
		for (MagicEdition e : getEnabledPlugin(MTGCardsProvider.class).listEditions()) {
			BufferedImage im = extract(e.getId().toUpperCase());
			cache24.put(e.getId(), new ImageIcon(im.getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
			cache16.put(e.getId(), new ImageIcon(im.getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
		}
	}

	public ImageIcon get24(String id) {
		if(id==null)
			return null;
		
		return cache24.get(id);
	}

	public ImageIcon get16(String id) {
		if(id==null)
			return null;
		
		return cache16.get(id);
	}

}
