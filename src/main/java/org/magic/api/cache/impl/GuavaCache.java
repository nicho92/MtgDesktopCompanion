package org.magic.api.cache.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractCacheProvider;
import org.magic.tools.MemoryTools;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class GuavaCache extends AbstractCacheProvider {

	Cache<String, BufferedImage> cache;
	
	
	public GuavaCache() {
		 cache = CacheBuilder.newBuilder()
				 			 .maximumSize(getInt("MAX_ITEM"))
				 			 .expireAfterAccess(getInt("EXPIRATION_MINUTE"), TimeUnit.MINUTES)
				 			 .build();
	}
	
	
	@Override
	public long size() {
		return cache.asMap().entrySet().stream().mapToLong(MemoryTools::sizeOf).sum();
	}
	
	
	@Override
	public BufferedImage getPic(MagicCard mc, MagicEdition ed) {

		if (ed == null)
			ed = mc.getCurrentSet();

		return cache.getIfPresent(generateIdIndex(mc, ed));
	}

	@Override
	public void put(BufferedImage im, MagicCard mc, MagicEdition ed) throws IOException {
		logger.debug("put " + mc + " in cache");
		if (ed == null)
			cache.put(generateIdIndex(mc, mc.getCurrentSet()), im);
		else
			cache.put(generateIdIndex(mc, ed), im);

	}
	
	@Override
	public void initDefault() {
		super.initDefault();
		setProperty("MAX_ITEM", "1000");
		setProperty("EXPIRATION_MINUTE", "10");
	}

	@Override
	public void clear() {
		cache.invalidateAll();

	}

	@Override
	public String getName() {
		return "Guava";
	}
	
	@Override
	public String getVersion() {
		return "27.0";
	}
	

}
