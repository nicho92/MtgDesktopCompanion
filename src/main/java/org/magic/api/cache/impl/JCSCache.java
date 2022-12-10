package org.magic.api.cache.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.jcs.JCS;
import org.apache.commons.jcs.access.CacheAccess;
import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.abstracts.AbstractCacheProvider;
import org.magic.api.interfaces.abstracts.AbstractMTGPlugin;
import org.magic.services.MTGConstants;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.POMReader;

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
	public Map<String, String> getDefaultAttributes() {
		var m = new HashMap<String, String>();
				m.put("jcs.default","DC");
				m.put("jcs.default.cacheattributes", "org.apache.commons.jcs.engine.CompositeCacheAttributes");
				m.put("jcs.default.cacheattributes.MaxObjects","200");
				m.put("jcs.default.cacheattributes.MemoryCacheName","org.apache.commons.jcs.engine.memory.lru.LRUMemoryCache");
				m.put("jcs.default.cacheattributes.UseMemoryShrinker","true");
				m.put("jcs.default.cacheattributes.MaxMemoryIdleTimeSeconds","3600");
				m.put("jcs.default.cacheattributes.ShrinkerIntervalSeconds","60");
				m.put("jcs.default.elementattributes","org.apache.commons.jcs.engine.ElementAttributes");
				m.put("jcs.default.elementattributes.IsEternal","false");
				m.put("jcs.default.elementattributes.MaxLife","21600");
				m.put("jcs.default.elementattributes.IdleTime","1800");
				m.put("jcs.default.elementattributes.IsSpool","true");
				m.put("jcs.default.elementattributes.IsRemote","true");
				m.put("jcs.default.elementattributes.IsLateral","true");
				m.put("jcs.default.outputCache.cacheattributes.DiskUsagePatternName","UPDATE");
				m.put("jcs.auxiliary.DC","org.apache.commons.jcs.auxiliary.disk.indexed.IndexedDiskCacheFactory");
				m.put("jcs.auxiliary.DC.attributes","org.apache.commons.jcs.auxiliary.disk.indexed.IndexedDiskCacheAttributes");
				m.put("jcs.auxiliary.DC.attributes.DiskPath",MTGConstants.DATA_DIR + "/jcsCache");
				m.put("jcs.auxiliary.DC.attributes.MaxPurgatorySize","10000");
				m.put("jcs.auxiliary.DC.attributes.MaxKeySize","10000");
				m.put("jcs.auxiliary.DC.attributes.OptimizeAtRemoveCount","300000");
				m.put("jcs.auxiliary.DC.attributes.ShutdownSpoolTimeLimit","60");
		return m;
	}

	@Override
	public BufferedImage getItem(MagicCard mc) {
		return ImageTools.fromByteArray(picturesCache.get(generateIdIndex(mc)));
	}

	@Override
	public void put(BufferedImage im, MagicCard mc) throws IOException {
		logger.debug("put {} in cache",mc);

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
	public void clear() {
		picturesCache.clear();

	}

	@Override
	public String getName() {
		return "JCS";
	}

	@Override
	public long size() {
		return FileTools.sizeOfDirectory(getFile("jcs.auxiliary.DC.attributes.DiskPath"));
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

