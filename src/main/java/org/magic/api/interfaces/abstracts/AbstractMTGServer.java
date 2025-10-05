package org.magic.api.interfaces.abstracts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.MTGServer;
import org.magic.api.interfaces.abstracts.extra.AbstractEmbeddedCacheProvider;
import org.magic.services.ReportsService;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalNotification;

public abstract class AbstractMTGServer extends AbstractMTGPlugin implements MTGServer {

	protected ReportsService notifFormater;
	private AbstractEmbeddedCacheProvider<String, Object> cache;


	public AbstractEmbeddedCacheProvider<String, Object> getCache() {
		return cache;
	}

	protected Object getCached(String k, Callable<Object> call)
	{
		if(cache.getItem(k)==null)
			try {
				cache.put(call.call(),k);
			} catch (Exception e) {
				logger.error(e);
				return new ArrayList<>();
			}

		return cache.getItem(k);
	}

 
	public void preinit()
	{
		//do nothing;
	}
	

	public void clearCache() {
		if(cache!=null)
			cache.clear();
	}


	protected AbstractMTGServer() {
		notifFormater = new ReportsService();
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var m = super.getDefaultAttributes();
		m.put("TIMEOUT_CACHE_MINUTES", MTGProperty.newIntegerProperty("720", "Timeout in minute for server cache management", 0, -1));
		return m;
	}


	protected void initCache()
	{
		var cacheTime = getInt("TIMEOUT_CACHE_MINUTES");

		cache = new AbstractEmbeddedCacheProvider<>() {
			Cache<String, Object> guava = CacheBuilder.newBuilder()
									  .expireAfterWrite(cacheTime, TimeUnit.MINUTES)
									  .removalListener((RemovalNotification<String, Object> notification)->
											logger.debug("{} is removed {}",notification.getKey(),notification.getCause())
									  )
									  .recordStats()
									  .build();

			public String getName() {
				return "Guava";
			}

			@Override
			public void clear() {
				guava.invalidateAll();
			}

			@Override
			public long size() {
				return 0;
			}

			public Object getItem(String k) {
				return guava.getIfPresent(k);
			}

			@Override
			public Map<String, Object> entries() {
				return guava.asMap();
			}

			@Override
			public void put(Object value, String key) throws IOException {
				guava.put(key, value);

			}

			@Override
			public Object getStat() {
				return guava.stats();
			}

		};
	}

	@Override
	public PLUGINS getType() {
		return PLUGINS.SERVER;
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
