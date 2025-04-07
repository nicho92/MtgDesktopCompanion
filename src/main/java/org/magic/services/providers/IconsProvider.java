package org.magic.services.providers;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ImageIcon;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MTGEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.services.MTGConstants;
import org.magic.services.logging.MTGLogger;
import org.magic.services.network.URLTools;
import org.magic.services.tools.Chrono;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.ImageTools;

import com.kitfox.svg.app.beans.SVGIcon;

public class IconsProvider {

	private static IconsProvider inst;
	private Map<String, ImageIcon> cache24;
	private Map<String, ImageIcon> cache16;
	private File localDirectory;
	private Logger logger = MTGLogger.getLogger(this.getClass());
	private static final String EXT = "_set.png";
	private Map<String,String> equiv;
	private TreeMap<String,Integer> map;


	public void clean() throws IOException {
		FileTools.cleanDirectory(localDirectory);
	}
	
	
	private void initManaSymbols() {

		map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		map.put("X", 21);
		map.put("Y",22);
		map.put("Z",23);
		map.put("W",24);
		map.put("U",25);
		map.put("B",26);
		map.put("R",27);
		map.put("G",28);
		map.put("S",29);
		map.put("W/P",45);
		map.put("U/P",46);
		map.put("B/P",47);
		map.put("R/P",48);
		map.put("G/P",49);
		map.put("W/U",30);
		map.put("W/B",31);
		map.put("U/B",32);
		map.put("U/R",33);
		map.put("B/R",34);
		map.put("B/G",35);
		map.put("R/W",36);
		map.put("R/G",37);
		map.put("G/W",38);
		map.put("G/U",39);
		map.put("2/W",40);
		map.put("2/U",41);
		map.put("2/B",42);
		map.put("2/R",43);
		map.put("2/G",44);
		map.put("T",50);
		map.put("Q",51);
		map.put("C",69);
		map.put("W/U/P", 70);
		map.put("W/B/P", 71);
		map.put("U/R/P", 72);
		map.put("U/B/P", 73);
		map.put("R/W/P", 74);
		map.put("R/G/P", 75);
		map.put("G/W/P", 76);
		map.put("G/U/P", 77);
		map.put("B/R/P", 78);
		map.put("B/G/P", 79);
		map.put("TIX", 80);
		map.put("TK", 80);
		map.put("\u221e",52);
		map.put("\u00BD",53);
		map.put("CHAOS",67);
		map.put("E",68);
		map.put("P",59);
		map.put("hr",58);
		map.put("hw",57);
		map.put("C/W",81);
		map.put("C/U",82);
		map.put("C/B",83);
		map.put("C/R",84);
		map.put("C/G",85);
		map.put("Paw Print",86);
		map.put("C/P",87);
		map.put("D",88);
		map.put("L",89);
		
	}

	
	
	

	private IconsProvider() {
		cache24 = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		cache16 = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		initManaSymbols();
		
		var chrono = new Chrono();
		
		
		localDirectory = new File(MTGConstants.DATA_DIR, "sets_icons");

		try {
			initEquiv();
		} catch (IOException e1) {
			logger.error("Error init Equiv SetFile");
		}

		if (!localDirectory.exists())
			localDirectory.mkdir();

		try {
			chrono.start();
			logger.trace("Init IconSet cache");
			initCache();
			logger.trace("Init IconSet cache : done {}sec",chrono.stop());
		} catch (Exception e) {
			logger.error("error init cache", e);
		}
	}

	public static IconsProvider getInstance() {
		if (inst == null)
			inst = new IconsProvider();

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
			logger.trace("load from cache {}",iconFile);
			return ImageTools.read(iconFile);
		}
		else {
			BufferedImage im = null;
			logger.trace("load from jar {}",id);

			try {
				String equivSet = getEquiv(id);
				im = ImageTools.readLocal(IconsProvider.class.getResource(MTGConstants.SET_ICON_DIR + equivSet + EXT));
				ImageTools.saveImage(im, iconFile, "png");
			} catch (Exception ex) {
				logger.trace("couldnt load icons for {}={}",id,getEquiv(id));
				im = ImageTools.readLocal(IconsProvider.class.getResource(MTGConstants.SET_ICON_DIR+"PMTG1_set.png"));
			}
			return im;
		}

	}
	
	

	private void initEquiv() throws IOException
	{
		equiv = new HashMap<>();

		var obj = URLTools.toJson(MTGConstants.MTG_DESKTOP_SETS_FILE.openStream()).getAsJsonObject();
		obj.entrySet().forEach(e->
			e.getValue().getAsJsonArray().forEach(je->equiv.put(je.getAsString(),e.getKey()))
		);
		
		}

	private String getEquiv(String set)
	{
		if(equiv.get(set)!=null)
			return equiv.get(set);


		if(set.length()==4 && set.toLowerCase().startsWith("p")) //TODO remove this code
			set=set.substring(1);


		return set;
	}

	private void initCache() throws IOException {
		for (MTGEdition e : getEnabledPlugin(MTGCardsProvider.class).listEditions()) {
			var im = extract(e.getId().toUpperCase());
			cache24.put(e.getId(), new ImageIcon(im.getScaledInstance(24, 24, Image.SCALE_SMOOTH)) {
				private static final long serialVersionUID = 1L;
				@Override
				public String toString() {
					return e.getId();
				}
			});
			cache16.put(e.getId(), new ImageIcon(im.getScaledInstance(16, 16, Image.SCALE_SMOOTH)) {
					private static final long serialVersionUID = 1L;
					@Override
					public String toString() {
						return e.getId();
					}
			});
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
	
	public Image getManaSymbol(String el) {
		var val = 0;
		try {
			val = Integer.parseInt(el);
		} catch (NumberFormatException ne) {
			if(map.get(el)!=null)
				val= map.get(el);
			else
			{
				logger.error("can't find icon for mana={}",el);
				val=21;
			}
		}

		var imgs = ImageTools.splitManaImage();
		
		if (val == 100)// mox lotus
			return ImageTools.joinBufferedImage(List.of(imgs[65],imgs[66]));

		if (val == 1_000_000)// gleemax
			return ImageTools.joinBufferedImage(List.of(imgs[60],imgs[61],imgs[62],imgs[63],imgs[64]));

		return imgs[val];
	}

}
