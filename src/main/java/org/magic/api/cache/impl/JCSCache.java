package org.magic.api.cache.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.jcs.JCS;
import org.apache.commons.jcs.access.CacheAccess;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractCacheProvider;
import org.magic.api.interfaces.abstracts.AbstractMTGPlugin;

public class JCSCache extends AbstractCacheProvider {
	
	
	private CacheAccess<String,BufferedImage> cache;
	
	public JCSCache() {
		
		JCS.setConfigProperties(props);
		cache = JCS.getInstance("default");
	}
	
	
	@Override
	public void initDefault() {

		setProperty("jcs.default","DC");
		setProperty("jcs.default.cacheattributes", "org.apache.commons.jcs.engine.CompositeCacheAttributes");
		setProperty("jcs.default.cacheattributes.MaxObjects","1000");
		setProperty("jcs.default.cacheattributes.MemoryCacheName","org.apache.commons.jcs.engine.memory.lru.LRUMemoryCache");
		setProperty("jcs.default.cacheattributes.UseMemoryShrinker","false");
		setProperty("jcs.default.cacheattributes.MaxMemoryIdleTimeSeconds","3600");
		setProperty("jcs.default.cacheattributes.ShrinkerIntervalSeconds","60");
		setProperty("jcs.default.elementattributes","org.apache.commons.jcs.engine.ElementAttributes");
		setProperty("jcs.default.elementattributes.IsEternal","false");
		setProperty("jcs.default.elementattributes.MaxLife","21600");
		setProperty("jcs.default.elementattributes.IdleTime","1800");
		setProperty("jcs.default.elementattributes.IsSpool","true");
		setProperty("jcs.default.elementattributes.IsRemote","true");
		setProperty("jcs.default.elementattributes.IsLateral","true");
	
	}
	
	@Override
	public BufferedImage getPic(MagicCard mc, MagicEdition ed) {
		if (ed == null)
			ed = mc.getCurrentSet();

		return cache.get(generateIdIndex(mc, ed));
	}

	@Override
	public void put(BufferedImage im, MagicCard mc, MagicEdition ed) throws IOException {
		logger.debug("put " + mc + " in cache");
		if (ed == null)
			cache.put(generateIdIndex(mc, mc.getCurrentSet()), im);
		else
			cache.put(generateIdIndex(mc, ed), im);

	}
	

	@Override
	public Icon getIcon() {
		return new ImageIcon(AbstractMTGPlugin.class.getResource("/icons/plugins/apache.png"));
	}
	

	@Override
	public void clear() {
		cache.clear();

	}

	@Override
	public String getName() {
		return "JCS";
	}

}
