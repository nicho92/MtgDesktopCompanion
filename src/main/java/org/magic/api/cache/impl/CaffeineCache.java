package org.magic.api.cache.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;

import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.abstracts.AbstractCacheProvider;
import org.magic.tools.MemoryTools;

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
	public BufferedImage getItem(MagicCard mc) {
		return cache.getIfPresent(generateIdIndex(mc));
	}




	@Override
	public void put(BufferedImage im, MagicCard mc) throws IOException {
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
	public Map<String, String> getDefaultAttributes() {
		return Map.of("EXPIRATION_MINUTE","10",
							    "CAPACITY","100");
	}


	@Override
	public String getName() {
		return "Caffeine";
	}

}
