package org.magic.services;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractMTGPicturesCache;

public class IconSetProvider {

	private static IconSetProvider inst;
	
	private Map<String,ImageIcon> cache24;
	private Map<String,ImageIcon> cache16;
	private File temp_file;
	static final Logger logger = LogManager.getLogger(IconSetProvider.class.getName());
	
	private IconSetProvider()
	{
		cache24 = new HashMap<String,ImageIcon>();
		cache16 = new HashMap<String,ImageIcon>();
		
		temp_file = new File(AbstractMTGPicturesCache.confdir,"sets_icons");
		
		if(!temp_file.exists())
			temp_file.mkdir();
		
		
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
	
	
	private BufferedImage extract(String id) throws IOException
	{
		/*File f = new File(temp_file,id+"_set.png");
		if(f.exists())
		{
			logger.trace("load from cache " + f);
			return ImageIO.read(f);
		}
		else*/
		{
			BufferedImage im=null;
			logger.trace("load from jar " + id);
			try
			{
				im = ImageIO.read(IconSetProvider.class.getResource("/res/set/icons/"+id+"_set.png"));
			}
			catch(Exception ex)
			{
				im = ImageIO.read(IconSetProvider.class.getResource("/res/set/icons/PMTG1_set.png"));
			}
			
			//ImageIO.write(im, "png", f);
			return im;
		}
		
	}
	
	
	private void initCache() throws Exception {
		for(MagicEdition e : MTGControler.getInstance().getEnabledProviders().loadEditions())
		{
				BufferedImage im = extract(e.getId());
				cache24.put(e.getId(),new ImageIcon(im.getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
				cache16.put(e.getId(),new ImageIcon(im.getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
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
