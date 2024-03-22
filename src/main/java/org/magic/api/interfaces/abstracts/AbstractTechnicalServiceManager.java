package org.magic.api.interfaces.abstracts;


import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.abstracts.AbstractAuditableItem;
import org.magic.api.beans.messages.TalkMessage;
import org.magic.api.beans.technical.audit.DAOInfo;
import org.magic.api.beans.technical.audit.DiscordInfo;
import org.magic.api.beans.technical.audit.FileAccessInfo;
import org.magic.api.beans.technical.audit.JsonQueryInfo;
import org.magic.api.beans.technical.audit.NetworkInfo;
import org.magic.api.beans.technical.audit.TaskInfo;
import org.magic.api.exports.impl.JsonExport;
import org.magic.services.logging.MTGLogger;
import org.magic.services.providers.IPTranslator;
import org.magic.services.technical.FileStorageTechnicalServiceManager;
import org.magic.services.threads.MTGRunnable;
import org.magic.services.threads.ThreadManager;

public abstract class AbstractTechnicalServiceManager {

	private boolean enable =true;
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	protected IPTranslator translator;
	protected  JsonExport serializer;

	private List<JsonQueryInfo> jsonInfo;
	private List<DAOInfo> daoInfos;
	private List<NetworkInfo> networkInfos;
	private List<TaskInfo> tasksInfos;
	private List<DiscordInfo> discordInfos;
	private List<FileAccessInfo> fileInfos;
	private List<TalkMessage> jsonMessages;
	
	
	
	public static final int SCHEDULE_TIMER_MS=1;
	
	
	private static AbstractTechnicalServiceManager inst;
	
	public static AbstractTechnicalServiceManager inst()
	{
		if(inst==null)
			inst = new FileStorageTechnicalServiceManager();

		return inst;
	}
	
	
	public abstract void restoreData();
	public abstract void persist() ;
	
	protected AbstractTechnicalServiceManager() {
		translator = new IPTranslator();
		jsonInfo= new ArrayList<>();
		networkInfos = new ArrayList<>();
		daoInfos = new ArrayList<>();
		tasksInfos = new ArrayList<>();
		fileInfos = new ArrayList<>();
		discordInfos = new ArrayList<>();
		jsonMessages=new ArrayList<>();
		
		
		if(isEnable())
		{	

			logger.debug("Starting Log backup timer scheduled at {}ms",TimeUnit.HOURS.toMillis(SCHEDULE_TIMER_MS));
			ThreadManager.getInstance().timer(new MTGRunnable() {
	
				@Override
				protected void auditedRun() {
					persist();
	
				}
			},"TechnicalService Timer",SCHEDULE_TIMER_MS,TimeUnit.HOURS);
		}
		
	}
	

	public void store(AbstractAuditableItem item) {
		if(item instanceof JsonQueryInfo info)
		{
			info.setLocation(translator.getLocationFor(info.getIp()));
			getJsonInfo().add(info);
		}
		else if (item instanceof DiscordInfo info)
		{
			getDiscordInfos().add(info);
		}
		else if (item instanceof FileAccessInfo info)
		{
			getFileInfos().add(info);
		}
		else if (item instanceof NetworkInfo info)
		{
			getNetworkInfos().add(info);
		}
		else if (item instanceof TaskInfo info)
		{
			getTasksInfos().add(info);
		}
		else if (item instanceof DAOInfo info)
		{
			getDaoInfos().add(info);
		}
		else if (item instanceof TalkMessage info)
		{
			getJsonMessages().add(info);
		}
		
	}
	
	

	public void restore()
	{

		if(isEnable())
		{
			ThreadManager.getInstance().executeThread(new MTGRunnable() {
				
				@Override
				protected void auditedRun() {
					restoreData();
				}
			}, "Loading technical data");
			logger.info("TechnicalService is enable");
		}
		else
		{
			logger.warn("TechnicalService is disabled");
		}
	}
	
	
	
	public Set<Entry<Object, Object>> getSystemInfo() {
		return System.getProperties().entrySet();
	}

	public ThreadInfo[] getThreadsInfos() {
		return ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
	}
	
	public List<FileAccessInfo> getFileInfos() {
		return fileInfos;
	}
	
	public List<DiscordInfo> getDiscordInfos() {
		return discordInfos;
	}

	public List<JsonQueryInfo> getJsonInfo() {
		return jsonInfo;
	}
	
	public List<NetworkInfo> getNetworkInfos() {
		return networkInfos;
	}

	public List<DAOInfo> getDaoInfos() {
		return daoInfos;
	}

	public List<TaskInfo> getTasksInfos() {
		return tasksInfos;
	}

	public List<TalkMessage> getJsonMessages() {
		return jsonMessages;
	}
	
	public boolean isEnable() {
		return enable;
	}

	public void enable(boolean enable)
	{
		this.enable =enable;
	}


	
}