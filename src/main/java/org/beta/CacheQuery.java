package org.beta;

import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicCardStock;

public class CacheQuery {
	CacheManager cacheManager;
	public enum CACHE { CARDS,ALERTS,STOCKS,SHAKE}

	public CacheQuery() {
		cacheManager = CacheManagerBuilder.newCacheManagerBuilder() 
			    .withCache(CACHE.CARDS.name(),CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, MagicCard.class, ResourcePoolsBuilder.heap(10))) 
			    .withCache(CACHE.ALERTS.name(),CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, MagicCardAlert.class, ResourcePoolsBuilder.heap(10)))
			    .withCache(CACHE.STOCKS.name(),CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, MagicCardStock.class, ResourcePoolsBuilder.heap(10)))
			    .withCache(CACHE.SHAKE.name(),CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, CardShake.class, ResourcePoolsBuilder.heap(10)))
			    .build(); 
		
		cacheManager.init();
	}
	
	
	
	
	
	
}
