package org.magic.api.cache.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractCacheProvider;
import org.magic.services.tools.MemoryTools;

public class Cache2kCache extends AbstractCacheProvider {

	Cache<String, BufferedImage> cache;

	public Cache2kCache() {
		cache = new Cache2kBuilder<String, BufferedImage>() {}
	    .expireAfterWrite(getInt("EXPIRATION_MINUTE"), TimeUnit.MINUTES)
	    .entryCapacity(getLong("CAPACITY"))
	    .build();
	}


	@Override
	public BufferedImage getItem(MTGCard mc) {
		return cache.get(generateIdIndex(mc));
	}




	@Override
	public void put(BufferedImage im, MTGCard mc) throws IOException {
		cache.put(generateIdIndex(mc), im);

	}

	@Override
	public void clear() {
		cache.clear();

	}


	@Override
	public long size() {
		return cache.asMap().entrySet().stream().mapToLong(MemoryTools::sizeOf).sum();
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("EXPIRATION_MINUTE",  MTGProperty.newIntegerProperty("10", "timeout in minute when cache will remove expired items", 0, -1),
							 "CAPACITY",MTGProperty.newIntegerProperty("100", "number of items stored in the cache", 0, -1));
	}


	@Override
	public String getName() {
		return "Cache2k";
	}

}
