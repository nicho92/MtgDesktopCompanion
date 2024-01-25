package org.magic.services.providers;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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

public class IconSetProvider {

	private static IconSetProvider inst;
	private Map<String, ImageIcon> cache24;
	private Map<String, ImageIcon> cache16;
	private File localDirectory;
	private Logger logger = MTGLogger.getLogger(this.getClass());
	private static final String EXT = "_set.png";
	private Map<String,String> equiv;


	public void clean() throws IOException {
		FileTools.cleanDirectory(localDirectory);
	}

	private IconSetProvider() {
		cache24 = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		cache16 = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
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
			logger.trace("load from cache {}",iconFile);
			return ImageTools.read(iconFile);
		}
		else {
			BufferedImage im = null;
			logger.trace("load from jar {}",id);

			try {
				String equivSet = getEquiv(id);
				im = ImageTools.readLocal(IconSetProvider.class.getResource(MTGConstants.SET_ICON_DIR + equivSet + EXT));
				ImageTools.saveImage(im, iconFile, "png");
			} catch (Exception ex) {
				logger.trace("couldnt load icons for {}={}",id,getEquiv(id));
				im = ImageTools.readLocal(IconSetProvider.class.getResource(MTGConstants.SET_ICON_DIR+"PMTG1_set.png"));
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

}
