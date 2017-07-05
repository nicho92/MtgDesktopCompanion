package org.magic.services;

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
			try{
			ImageIcon im;
			if(e.getId().startsWith("p"))
			{
				im=new ImageIcon(ImageIO.read(IconSetProvider.class.getResource("/res/set/icons/VAN_set.png")));
			}
			else
			{	
				im = new ImageIcon(ImageIO.read(IconSetProvider.class.getResource("/res/set/icons/"+e.getId()+"_set.png")));
			}
			
			cache.put(e.getId(),im);
			//new ImageIcon(ImageIO.read(MagicCollectionTableCellRenderer.class.getResource("/res/set/icons/"+e.getId()+"_set.png")).getSubimage(12, 11, 55, 42).getScaledInstance(26, 24, Image.SCALE_SMOOTH));
			
			}
			catch(Exception ex)
			{
				cache.put(e.getId(), new ImageIcon());
			}
	}
	
	
	public ImageIcon get(String id)
	{
		return cache.get(id);
	}
	
	
	public static void main(String[] args) {
		MTGControler.getInstance().getEnabledProviders().init();
		IconSetProvider.getInstance().get("CMA");
	}
	
}
