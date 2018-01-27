package org.magic.api.cache.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MagicCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractMTGPicturesCache;
import org.magic.tools.IDGenerator;

public class MemoryCache extends AbstractMTGPicturesCache {
	
	Map<String,BufferedImage> cache;

	
	@Override
	public STATUT getStatut() {
		return STATUT.STABLE;
	}
	
	
	private String generateIdIndex(MagicCard mc,MagicEdition ed)
	{
		return IDGenerator.generate(mc, ed);
	}
	
	
	public MemoryCache() {
		super();
		if(!new File(CONFDIR, getName()+".conf").exists()){
		
		save();
		}
		cache=new HashMap<>();
	}
	
	
	@Override
	public String getName() {
		return "Memory Cache";
	}

	@Override
	public BufferedImage getPic(MagicCard mc,MagicEdition ed) {
		
		if(ed==null)
			ed=mc.getEditions().get(0);
		
		return cache.get(generateIdIndex(mc,ed));
	}

	@Override
	public void put(BufferedImage im, MagicCard mc,MagicEdition ed) throws Exception{
		logger.debug("put " + mc + " in cache");
		if(ed==null)
			cache.put(generateIdIndex(mc,mc.getEditions().get(0)), im);
		else
			cache.put(generateIdIndex(mc,ed), im);
	}


	@Override
	public void clear() {
		cache.clear();
		
	}

}
