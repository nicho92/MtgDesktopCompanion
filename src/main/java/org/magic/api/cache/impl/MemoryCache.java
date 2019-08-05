package org.magic.api.cache.impl;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractCacheProvider;
import org.magic.tools.MemoryTools;

public class MemoryCache extends AbstractCacheProvider {

	private Map<String, BufferedImage> cache;

	@Override
	public STATUT getStatut() {
		return STATUT.DEPRECATED;
	}
	
	public MemoryCache() {
		super();
		cache = new HashMap<>();
	}

	@Override
	public String getName() {
		return "Memory";
	}

	@Override
	public BufferedImage getPic(MagicCard mc, MagicEdition ed) {

		if (ed == null)
			ed = mc.getCurrentSet();

		return cache.get(generateIdIndex(mc, ed));
	}

	@Override
	public void put(BufferedImage im, MagicCard mc, MagicEdition ed) {
		logger.debug("put " + mc + " in cache");
		if (ed == null)
			cache.put(generateIdIndex(mc, mc.getCurrentSet()), im);
		else
			cache.put(generateIdIndex(mc, ed), im);
	}

	@Override
	public void clear() {
		cache.clear();
	}
	
	@Override
	public void clear(MagicEdition ed) {
		logger.debug("not implemented");
		
	}

	@Override
	public String getVersion() {
		return "1";
	}

	@Override
	public long size() {
		return cache.entrySet().stream().mapToLong(MemoryTools::sizeOf).sum();
	}
	

	@Override
	public boolean equals(Object obj) {
		
		if(obj ==null)
			return false;
		
		return hashCode()==obj.hashCode();
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode();
	}
}
