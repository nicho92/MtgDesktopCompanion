package org.magic.api.cache.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.io.FileUtils;
import org.apache.commons.jcs.JCS;
import org.apache.commons.jcs.access.CacheAccess;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractCacheProvider;
import org.magic.api.interfaces.abstracts.AbstractMTGPlugin;
import org.magic.services.MTGConstants;
import org.magic.tools.ImageTools;
import org.magic.tools.POMReader;

public class JCSCache extends AbstractCacheProvider {
	
	
	private CacheAccess<String, byte[]> picturesCache;
	
	public JCSCache() {
		super();
		JCS.setConfigProperties(props);
		picturesCache = JCS.getInstance("default");
	}
	
	@Override
	public void unload() {
		JCS.shutdown();
	}
	
	
	@Override
	public void initDefault() {

		setProperty("jcs.default","DC");
		setProperty("jcs.default.cacheattributes", "org.apache.commons.jcs.engine.CompositeCacheAttributes");
		setProperty("jcs.default.cacheattributes.MaxObjects","200");
		setProperty("jcs.default.cacheattributes.MemoryCacheName","org.apache.commons.jcs.engine.memory.lru.LRUMemoryCache");
		setProperty("jcs.default.cacheattributes.UseMemoryShrinker","true");
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
		setProperty("jcs.auxiliary.DC.attributes.DiskPath",MTGConstants.DATA_DIR + "/jcsCache");
		setProperty("jcs.auxiliary.DC.attributes.MaxPurgatorySize","10000");
		setProperty("jcs.auxiliary.DC.attributes.MaxKeySize","10000");
		setProperty("jcs.auxiliary.DC.attributes.OptimizeAtRemoveCount","300000");
		setProperty("jcs.auxiliary.DC.attributes.ShutdownSpoolTimeLimit","60");
	}

	@Override
	public BufferedImage getPic(MagicCard mc) {
		return ImageTools.fromByteArray(picturesCache.get(generateIdIndex(mc)));
	}

	@Override
	public void put(BufferedImage im, MagicCard mc) throws IOException {
		logger.debug("put " + mc + " in cache");
		
		picturesCache.put(generateIdIndex(mc), ImageTools.toByteArray(im));
		
		logger.debug(picturesCache.getStats());
	}
	
	@Override
	public String getVersion() {
		return POMReader.readVersionFromPom(JCS.class, "/META-INF/maven/org.apache.commons/commons-jcs-core/pom.properties");
	}
	

	@Override
	public Icon getIcon() {
		return new ImageIcon(AbstractMTGPlugin.class.getResource("/icons/plugins/apache.png"));
	}
	
	
	@Override
	public void clear(MagicEdition ed) {
		logger.error("not implemented");
		
	}

	@Override
	public void clear() {
		picturesCache.clear();

	}

	@Override
	public String getName() {
		return "JCS";
	}
	
	@Override
	public long size() {
		return FileUtils.sizeOfDirectory(getFile("jcs.auxiliary.DC.attributes.DiskPath"));
	}
	

	@Override
	public int hashCode() {
		return getName().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj ==null)
			return false;
		
		return hashCode()==obj.hashCode();
	}
	
	

}

