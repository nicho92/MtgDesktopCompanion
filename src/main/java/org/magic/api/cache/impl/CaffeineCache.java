package org.magic.api.cache.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractCacheProvider;
import org.magic.services.tools.MemoryTools;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

public class CaffeineCache extends AbstractCacheProvider {

	Cache<String, BufferedImage> cache;

	public CaffeineCache() {
		cache = Caffeine.newBuilder()
			    .maximumSize(10_000)
			    .expireAfterWrite(Duration.ofMinutes(getInt("EXPIRATION_MINUTE")))
			    .build();
	}


	@Override
	public BufferedImage getItem(MTGCard mc) {
		return cache.getIfPresent(generateIdIndex(mc));
	}




	@Override
	public void put(BufferedImage im, MTGCard mc) throws IOException {
		cache.put(generateIdIndex(mc), im);

	}

	@Override
	public void clear() {
		cache.invalidateAll();

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
		return "Caffeine";
	}

}
