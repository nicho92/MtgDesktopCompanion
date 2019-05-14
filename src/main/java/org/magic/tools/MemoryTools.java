package org.magic.tools;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.magic.api.beans.MemoryInfo;
import org.magic.services.MTGLogger;
import org.openjdk.jol.info.GraphLayout;
import org.openjdk.jol.util.Multiset;

public class MemoryTools {
	
	protected static Logger logger = MTGLogger.getLogger(MemoryTools.class);

	private MemoryTools() {	}
	
	public static List<MemoryInfo> getStat(Object classe)
	{
		logger.trace("Analyze " + classe);
		Multiset<Class<?>> size = GraphLayout.parseInstance(classe).getClassSizes();
		Multiset<Class<?>> count =GraphLayout.parseInstance(classe).getClassCounts(); 
		return size.keys().stream().map(c->new MemoryInfo(c, count.count(c), size.count(c))).sorted().collect(Collectors.toList());
	}
	
	
	
	
}