package org.magic.api.cache.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.magic.api.beans.MagicCard;
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
	public BufferedImage getItem(MagicCard mc) {
		return cache.getIfPresent(generateIdIndex(mc));
	}

	@Override
	public void put(BufferedImage im, MagicCard mc) throws IOException {
		logger.debug("put {} in cache ",mc);
		cache.put(generateIdIndex(mc), im);

	}
	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of("MAX_ITEM","1000","EXPIRATION_MINUTE","10");
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
