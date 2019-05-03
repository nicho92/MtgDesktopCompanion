package org.magic.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.magic.services.MTGLogger;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class TCache<T>{

	private Cache<String, T> loader;
	protected Logger logger = MTGLogger.getLogger(this.getClass());

	public TCache() {
		loader = CacheBuilder.newBuilder()
				.recordStats()
				.build();
	}
	
	
	private Cache<String, T> getCache()
	{
		logger.debug(loader.stats());
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
	
	@Deprecated
	public T get(String k)
	{
		return getCache().getIfPresent(k);
	}
	
	public boolean has(String k)
	{
		return getCache().getIfPresent(k)!=null;
	}
	
	public T get(String k, Callable<T> call) throws ExecutionException
	{
		return getCache().get(k,call);
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
