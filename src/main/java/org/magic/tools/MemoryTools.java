package org.magic.tools;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.technical.MemoryInfo;
import org.magic.services.logging.MTGLogger;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;
import org.openjdk.jol.util.Multiset;

public class MemoryTools {

	protected static Logger logger = MTGLogger.getLogger(MemoryTools.class);

	private MemoryTools() {	}

	public static List<MemoryInfo> getStats(Object classe)
	{
		logger.trace("Analyze {}",classe);
		Multiset<Class<?>> size = GraphLayout.parseInstance(classe).getClassSizes();
		Multiset<Class<?>> count =GraphLayout.parseInstance(classe).getClassCounts();
		return size.keys().stream().map(c->new MemoryInfo(c, count.count(c), size.count(c))).sorted().toList();
	}

	public static String statsToString(Object classe)
	{
		return GraphLayout.parseInstance(classe).toFootprint();
	}

	public static String statClassToString(Class<?> classe)
	{
		return ClassLayout.parseClass(classe).toPrintable();
	}

	public static String statInstanceToString(Object classe)
	{
		return ClassLayout.parseInstance(classe).toPrintable();
	}

	public static long sizeOf(Object classe)
	{
		return ClassLayout.parseInstance(classe).instanceSize();
	}


}