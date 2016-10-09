package org.magic.api.cache;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.PicturesCache;

public class MemoryCache implements PicturesCache {

	
	Map<String,BufferedImage> cache;
	
	
	public MemoryCache() {
		cache=new HashMap<String,BufferedImage>();
	}
	
	
	@Override
	public String getName() {
		return "Memory Cache";
	}

	@Override
	public BufferedImage getPic(MagicCard mc) {
		return cache.get(mc.getId());
		
	}


	@Override
	public void put(BufferedImage im, MagicCard mc) throws Exception{
		if(cache.get(mc.getId())==null)
		{
			logger.debug("put " + mc + " in cache");
			cache.put(mc.getId(), im);
		}
			
	
		
	}

}
