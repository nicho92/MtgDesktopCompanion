package org.magic.services.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.Logger;
import org.magic.services.logging.MTGLogger;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class TCache<T>{

	private Cache<String, T> loader;
	protected Logger logger = MTGLogger.getLogger(this.getClass());

	private String name;

	public TCache(String name) {
		this.name=name;
		init();
	}

	public void init() {
		loader = CacheBuilder.newBuilder()
				.recordStats()
				.build();
	}


	private Cache<String, T> getCache()
	{
		logger.trace("{}-{}",name,loader.stats());
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


	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(loader,ToStringStyle.MULTI_LINE_STYLE);
	}

	public ObjectName getObjectName() {
		try {
			return new ObjectName("org.magic.api:type=cache,name="+name);
		} catch (MalformedObjectNameException e) {
			return null;
		}
	}

	public void clean() {
		loader.invalidateAll();

	}



}
