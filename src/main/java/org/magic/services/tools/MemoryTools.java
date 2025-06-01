package org.magic.services.tools;

import org.openjdk.jol.info.ClassLayout;

public class MemoryTools {

	private MemoryTools() {	}

	public static String statInstanceToString(Object classe)
	{
		return ClassLayout.parseInstance(classe).toPrintable();
	}

	public static long sizeOf(Object classe)
	{
		return ClassLayout.parseInstance(classe).instanceSize();
	}


}