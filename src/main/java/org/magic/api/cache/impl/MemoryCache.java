package org.magic.api.cache.impl;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractMTGPicturesCache;
import org.magic.tools.IDGenerator;

public class MemoryCache extends AbstractMTGPicturesCache {

	Map<String, BufferedImage> cache;

	private String generateIdIndex(MagicCard mc, MagicEdition ed) {
		return IDGenerator.generate(mc, ed);
	}

	public MemoryCache() {
		super();
		cache = new HashMap<>();
	}

	@Override
	public String getName() {
		return "Memory Cache";
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
	public String getVersion() {
		return "1";
	}

}
