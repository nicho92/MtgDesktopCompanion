package org.magic.tools;

import java.util.ArrayList;
import java.util.List;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

public class DAOCache<T>{

	private Cache<String, T> loader;
	
	public DAOCache() {
		loader = CacheBuilder.newBuilder()
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
		loader.put(k, value);
	}
	
	public void remove(String k)
	{
		loader.invalidate(k);
	}
	
	public void get(String k)
	{
		loader.getIfPresent(k);
	}
	
	public List<T> values()
	{
		return new ArrayList<>(loader.asMap().values());
	}
	
	public List<String> keys()
	{
		return new ArrayList<>(loader.asMap().keySet());
	}
	
	public int size()
	{
		return loader.asMap().size();
	}
	
	public boolean isEmpty()
	{
		return loader.asMap().isEmpty();
	}

	public void put(Integer id, T state) {
		put(String.valueOf(id),state);
		
	}

}
