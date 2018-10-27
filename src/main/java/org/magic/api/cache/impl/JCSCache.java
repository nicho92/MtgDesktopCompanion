package org.magic.api.cache.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.jcs.JCS;
import org.apache.commons.jcs.access.CacheAccess;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractCacheProvider;
import org.magic.api.interfaces.abstracts.AbstractMTGPlugin;
import org.magic.services.MTGConstants;
import org.magic.tools.ImageUtils;

public class JCSCache extends AbstractCacheProvider {
	
	
	private CacheAccess<String, byte[]> cache;
	
	public JCSCache() {
		super();
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
		setProperty("jcs.default.outputCache.cacheattributes.DiskUsagePatternName","UPDATE");
		setProperty("jcs.auxiliary.DC","org.apache.commons.jcs.auxiliary.disk.indexed.IndexedDiskCacheFactory");
		setProperty("jcs.auxiliary.DC.attributes","org.apache.commons.jcs.auxiliary.disk.indexed.IndexedDiskCacheAttributes");
		setProperty("jcs.auxiliary.DC.attributes.DiskPath",MTGConstants.CONF_DIR + "/caches/jcsCache");
		setProperty("jcs.auxiliary.DC.attributes.MaxPurgatorySize","10000000");
		setProperty("jcs.auxiliary.DC.attributes.MaxKeySize","1000000");
		setProperty("jcs.auxiliary.DC.attributes.OptimizeAtRemoveCount","300000");
		setProperty("jcs.auxiliary.DC.attributes.ShutdownSpoolTimeLimit","60");
	}
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	
	@Override
	public BufferedImage getPic(MagicCard mc, MagicEdition ed) {
		if (ed == null)
			ed = mc.getCurrentSet();

		return ImageUtils.fromByteArray(cache.get(generateIdIndex(mc, ed)));
	}

	@Override
	public void put(BufferedImage im, MagicCard mc, MagicEdition ed) throws IOException {
		logger.debug("put " + mc + " in cache");
		if (ed == null)
			cache.put(generateIdIndex(mc, mc.getCurrentSet()), ImageUtils.toByteArray(im));
		else
			cache.put(generateIdIndex(mc, ed), ImageUtils.toByteArray(im));

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

