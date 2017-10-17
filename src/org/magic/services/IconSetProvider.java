package org.magic.services;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicEdition;

public class IconSetProvider {

	private static IconSetProvider inst;
	
	private Map<String,ImageIcon> cache24;
	private Map<String,ImageIcon> cache16;
	static final Logger logger = LogManager.getLogger(IconSetProvider.class.getName());
	
	private IconSetProvider()
	{
		cache24 = new HashMap<String,ImageIcon>();
		cache16 = new HashMap<String,ImageIcon>();
		try {
			logger.debug("Init IconSet cache");
			long time_1 = System.currentTimeMillis();
			initCache();
			long time_2 = System.currentTimeMillis();
			logger.debug("Init IconSet cache : done " + (time_2-time_1)/1000+ " sec");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static IconSetProvider getInstance()
	{
		if(inst == null)
			inst= new IconSetProvider();
		
		return inst;
	}
	
	
	private void initCache() throws Exception {
		
		for(MagicEdition e : MTGControler.getInstance().getEnabledProviders().loadEditions())
			try
			{
			BufferedImage im = ImageIO.read(IconSetProvider.class.getResource("/res/set/icons/"+e.getId()+"_set.png"));
				cache24.put(e.getId(),new ImageIcon(im.getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
				cache16.put(e.getId(),new ImageIcon(im.getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
			}
			catch(Exception ex)
			{
				BufferedImage im = ImageIO.read(IconSetProvider.class.getResource("/res/set/icons/PMTG1_set.png"));
				cache16.put(e.getId(), new ImageIcon(im.getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
				cache24.put(e.getId(), new ImageIcon(im.getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
			}
	}
	
	
	
	
	public ImageIcon get24(String id)
	{
		return cache24.get(id);
	}
	
	public ImageIcon get16(String id)
	{
		return cache16.get(id);
	}
	
}
