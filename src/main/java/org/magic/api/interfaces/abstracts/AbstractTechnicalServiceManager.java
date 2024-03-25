package org.magic.api.interfaces.abstracts;


import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
import org.magic.services.technical.DAOStorageTechnicalServiceManager;
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
	
	private static final int SCHEDULE_TIMER_MINS=10;
	
	
	private static AbstractTechnicalServiceManager inst;
	
	public static AbstractTechnicalServiceManager inst()
	{
		if(inst==null)
			inst = new DAOStorageTechnicalServiceManager();

		return inst;
	}

	protected abstract <T extends AbstractAuditableItem> void store(Class<T> c, List<T> list)  throws IOException;
	protected abstract <T extends AbstractAuditableItem> List<T> restore(Class<T> c)  throws IOException;


	protected <T extends AbstractAuditableItem> void storeItems(Class<T> classe, List<T> items) 
	{
		
		if(items.isEmpty())
			return;
		
		try {
			store(classe,items);
			items.stream().forEach(a->a.setStored(true));
			logger.info("Store {} new  items for {}",items.size(),classe.getSimpleName());
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	
	private <T extends AbstractAuditableItem> List<T> read(Class<T> c) throws IOException 
	{
		return restore(c).stream().map(o->{
			o.setStored(true);
			return o;
		}).toList();
	}
	
	
	public void restoreData() throws IOException
	{
			getJsonInfo().addAll(read(JsonQueryInfo.class));
			getDaoInfos().addAll(read(DAOInfo.class));
			getTasksInfos().addAll(read(TaskInfo.class));
			getNetworkInfos().addAll(read(NetworkInfo.class));
			getDiscordInfos().addAll(read(DiscordInfo.class));
			getFileInfos().addAll(read(FileAccessInfo.class));
			getJsonMessages().addAll(read(TalkMessage.class));
				
			logger.info("Technical data are loaded");
	}
	
	

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
			logger.info("TechnicalService is enable");
			ThreadManager.getInstance().timer(new MTGRunnable() {
	
				@Override
				protected void auditedRun() {
					persist();
				}
			},"TechnicalService Timer",SCHEDULE_TIMER_MINS,TimeUnit.MINUTES);
			
		}
		else
		{
			logger.warn("TechnicalService is disabled");
		}

		
	}
	
	public void persist()
	{

		if(isEnable())
		{
				try {
					storeItems(JsonQueryInfo.class,getJsonInfo().stream().filter(it->!it.isStored()).toList());
					storeItems(DAOInfo.class,getDaoInfos().stream().filter(it->!it.isStored()).toList());
					storeItems(NetworkInfo.class,getNetworkInfos().stream().filter(it->!it.isStored()).toList());
					storeItems(TaskInfo.class,getTasksInfos().stream().filter(it->!it.isStored()).toList());
					storeItems(DiscordInfo.class,getDiscordInfos().stream().filter(it->!it.isStored()).toList());
					storeItems(FileAccessInfo.class,getFileInfos().stream().filter(it->!it.isStored()).toList());
					storeItems(TalkMessage.class,getJsonMessages().stream().filter(it->!it.isStored()).toList());
				}
				catch(Exception e)
				{
					logger.error(e);
				}
		}

	}

	public void store(AbstractAuditableItem item) {
		
		item.setStored(false);
		
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