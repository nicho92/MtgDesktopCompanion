package org.magic.api.cache.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.jcs.JCS;
import org.apache.commons.jcs.access.CacheAccess;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.technical.MTGProperty;
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
	public Map<String, MTGProperty> getDefaultAttributes() {
		var m = new HashMap<String, MTGProperty>();
				m.put("jcs.default",MTGProperty.newStringProperty("DC"));
				m.put("jcs.default.cacheattributes", MTGProperty.newStringProperty("org.apache.commons.jcs.engine.CompositeCacheAttributes"));
				m.put("jcs.default.cacheattributes.MaxObjects",MTGProperty.newIntegerProperty("200", "The maximum number of items allowed in memory. Eviction of elements in excess of this number is determined by the memory cache. By default JCS uses the LRU memory cache.", 1, 1000));
				m.put("jcs.default.cacheattributes.MemoryCacheName",MTGProperty.newStringProperty("org.apache.commons.jcs.engine.memory.lru.LRUMemoryCache"));
				m.put("jcs.default.cacheattributes.UseMemoryShrinker",MTGProperty.newBooleanProperty("true","By default, the memory shrinker is shared by all regions that use the LRU memory cache. The memory shrinker iterates through the items in memory, looking for items that have expired or that have exceeded their max memory idle time."));
				m.put("jcs.default.cacheattributes.MaxMemoryIdleTimeSeconds",MTGProperty.newIntegerProperty("3600","This is only used if you are using the memory shrinker. If this value is set above -1, then if an item has not been accessed in this number of seconds, it will be spooled to disk if the disk is available. You can register an event handler on this event.",3600,-1));
				m.put("jcs.default.cacheattributes.ShrinkerIntervalSeconds",MTGProperty.newIntegerProperty("60","This specifies how often the shrinker should run, if it has been activated. If you set UseMemoryShrinker to false, then this setting has no effect.",30,-1));
				m.put("jcs.default.elementattributes",MTGProperty.newStringProperty("org.apache.commons.jcs.engine.ElementAttributes"));
				m.put("jcs.default.elementattributes.IsEternal",MTGProperty.newBooleanProperty("false","If an element is specified as eternal, then it will never be subject to removal for exceeding its max life."));
				m.put("jcs.default.elementattributes.MaxLife",MTGProperty.newIntegerProperty("21600","If you specify that elements within a region are not eternal, then you can set the max life seconds. If this is exceeded the elements will be removed passively when a client tries to retrieve them. If you are using a memory shrinker, then the items can be removed actively",-1,-1));
				m.put("jcs.default.elementattributes.IsSpool",MTGProperty.newBooleanProperty("true","By default, can elements in this region be sent to a disk cache if one is available."));
				m.put("jcs.default.elementattributes.IsRemote",MTGProperty.newBooleanProperty("true","By default, can elements in this region be sent to a remote cache if one is available."));
				m.put("jcs.default.elementattributes.IsLateral",MTGProperty.newBooleanProperty("true","By default, can elements in this region be sent to a lateral cache if one is available."));
				m.put("jcs.default.outputCache.cacheattributes.DiskUsagePatternName",new MTGProperty("org.apache.commons.jcs.engine.ElementAttributes","Under the swap pattern, data is only put to disk when the max memory size is reached. Since items puled from disk are put into memory, if the memory cache is full and you get an item off disk, the lest recently used item will be spooled to disk. If you have a low memory hit ration, you end up thrashing. The UPDATE usage pattern allows items to go to disk on an update. It disables the swap. This allows you to persist all items to disk. If you are using the JDBC disk cache for instance, you can put all the items on disk while using the memory cache for performance, and not worry about losing data from a system crash or improper shutdown. Also, since all items are on disk, there is no need to swap to disk. This prevents the possibility of thrashing.","UPDATE","SWAP"));
				m.put("jcs.auxiliary.DC",MTGProperty.newStringProperty("org.apache.commons.jcs.auxiliary.disk.indexed.IndexedDiskCacheFactory"));
				m.put("jcs.auxiliary.DC.attributes",MTGProperty.newStringProperty("org.apache.commons.jcs.auxiliary.disk.indexed.IndexedDiskCacheAttributes"));
				m.put("jcs.auxiliary.DC.attributes.DiskPath",MTGProperty.newDirectoryProperty(new File(MTGConstants.DATA_DIR + "/jcsCache")));
				m.put("jcs.auxiliary.DC.attributes.MaxPurgatorySize", MTGProperty.newIntegerProperty("10000","The maximum number of items allowed in the queue of items to be written to disk.",1,-1));
				m.put("jcs.auxiliary.DC.attributes.MaxKeySize",MTGProperty.newIntegerProperty("10000","The maximum number of keys that the indexed disk cache can have. Since the keys are stored in memory, you may want to limit this number to something reasonable.",5000,-1));
				m.put("jcs.auxiliary.DC.attributes.OptimizeAtRemoveCount",MTGProperty.newIntegerProperty("300000","At how many removes should the cache try to defragment the data file. Since we recycle empty spots, defragmentation is usually not needed. To prevent the cache from defragmenting the data file, you can set this to -1.",-1,-1));
				return m;
	}

	@Override
	public BufferedImage getItem(MTGCard mc) {
		return ImageTools.fromByteArray(picturesCache.get(generateIdIndex(mc)));
	}

	@Override
	public void put(BufferedImage im, MTGCard mc) throws IOException {
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

