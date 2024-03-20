package org.magic.api.interfaces.abstracts;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.magic.api.exports.impl.JsonExport;
import org.magic.services.logging.MTGLogger;
import org.magic.services.providers.IPTranslator;
import org.magic.services.technical.FileServiceManager;

public abstract class AbstractTechnicalServiceManager {

	private boolean enable =true;
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	protected IPTranslator translator;
	protected  JsonExport serializer;
	
	
	private static FileServiceManager inst;
	
	public static FileServiceManager inst()
	{
		if(inst==null)
			inst = new FileServiceManager();

		return inst;
	}

	protected AbstractTechnicalServiceManager() {
		translator = new IPTranslator();
	//	serializer = new JsonExport();
	}
	
	
	public Set<Entry<Object, Object>> getSystemInfo() {
		return System.getProperties().entrySet();
	}

	public ThreadInfo[] getThreadsInfos() {
		return ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
	}


	
	public boolean isEnable() {
		return enable;
	}

	public void enable(boolean enable)
	{
		this.enable =enable;
	}
	
	
}