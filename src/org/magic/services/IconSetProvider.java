package org.magic.services;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.magic.api.beans.MagicEdition;

public class IconSetProvider {

	private static IconSetProvider inst;
	
	private Map<String,ImageIcon> cache;
	
	private IconSetProvider()
	{
		cache = new HashMap<String,ImageIcon>();
		try {
			initCache();
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
	
	public Map<String, ImageIcon> getCache() {
		return cache;
	}
	
	
	private void initCache() throws Exception {
		
		for(MagicEdition e : MTGControler.getInstance().getEnabledProviders().loadEditions())
			try
			{
			ImageIcon im = new ImageIcon(ImageIO.read(IconSetProvider.class.getResource("/res/set/icons/"+e.getId()+"_set.png")).getScaledInstance(24, 26, Image.SCALE_SMOOTH));
			cache.put(e.getId(),im);
			}
			catch(Exception ex)
			{
				cache.put(e.getId(), new ImageIcon(ImageIO.read(IconSetProvider.class.getResource("/res/set/icons/PMTG1_set.png")).getScaledInstance(24, 26, Image.SCALE_SMOOTH)));
			}
	}
	
	
	public ImageIcon get(String id)
	{
		return cache.get(id);
	}
	
}
