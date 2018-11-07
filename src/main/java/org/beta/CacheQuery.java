package org.beta;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.magic.api.beans.MagicEdition;


public class CacheQuery {

	private CacheManager cacheManager;
	private Cache<String, MagicEdition> cacheEditions;
	private int heapPool=10;
	
	
	public CacheQuery() 
	{
		cacheManager = CacheManagerBuilder.newCacheManagerBuilder() 
			    .withCache("editions",CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, MagicEdition.class, ResourcePoolsBuilder.heap(heapPool))) 
			    .build();
		
		cacheManager.init();
		cacheEditions = cacheManager.getCache("editions", String.class, MagicEdition.class);
	}
	
	
	public void putEdition(MagicEdition ed)
	{
		cacheEditions.put(ed.getId(), ed);
	}
	
	
	
	
}
