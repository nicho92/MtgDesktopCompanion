package org.magic.tools;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.magic.services.MTGLogger;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

public class DAOCache<T>{

	private Cache<String, T> loader;
	protected Logger logger = MTGLogger.getLogger(this.getClass());

	public DAOCache() {
		loader = CacheBuilder.newBuilder()
			//	.recordStats()
				.build(new CacheLoader<String, T>() {
							@Override
							public T load(String key) throws Exception {
								return loader.getIfPresent(key);
							}
					  });
	}
	
	public Cache<String, T> getCache()
	{
		return loader;
	}
	
	public void put(String k, T value)
	{
		getCache().put(k, value);
	}
	
	public void remove(String k)
	{
		getCache().invalidate(k);
	}
	
	public T get(String k)
	{
		return getCache().getIfPresent(k);
	}
	
	public List<T> values()
	{
		return new ArrayList<>(getCache().asMap().values());
	}
	
	public List<String> keys()
	{
		return new ArrayList<>(getCache().asMap().keySet());
	}
	
	public int size()
	{
		return getCache().asMap().size();
	}
	
	public boolean isEmpty()
	{
		return getCache().asMap().isEmpty();
	}

	public void put(Integer id, T state) {
		put(String.valueOf(id),state);
		
	}


}
