package org.magic.api.cache.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractCacheProvider;
import org.magic.services.tools.MemoryTools;
import org.magic.services.tools.POMReader;

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
	public BufferedImage getItem(MTGCard mc) {
		return cache.getIfPresent(generateIdIndex(mc));
	}

	@Override
	public void put(BufferedImage im, MTGCard mc) throws IOException {
		logger.debug("put {} in cache ",mc);
		cache.put(generateIdIndex(mc), im);

	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("EXPIRATION_MINUTE",  MTGProperty.newIntegerProperty("10", "timeout in minute when cache will remove expired items", 0, -1),
				 "MAX_ITEM",MTGProperty.newIntegerProperty("1000", "number of items stored in the cache", 0, -1));
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
		return POMReader.readVersionFromPom(GuavaCache.class, "/META-INF/maven/com.google.guava/guava/pom.properties");
	}


}
