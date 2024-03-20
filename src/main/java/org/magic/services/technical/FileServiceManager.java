package org.magic.services.technical;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.magic.api.beans.abstracts.AbstractAuditableItem;
import org.magic.api.beans.messages.TalkMessage;
import org.magic.api.beans.technical.audit.DAOInfo;
import org.magic.api.beans.technical.audit.DiscordInfo;
import org.magic.api.beans.technical.audit.FileAccessInfo;
import org.magic.api.beans.technical.audit.JsonQueryInfo;
import org.magic.api.beans.technical.audit.NetworkInfo;
import org.magic.api.beans.technical.audit.TaskInfo;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.abstracts.AbstractTechnicalServiceManager;
import org.magic.services.MTGConstants;
import org.magic.services.threads.MTGRunnable;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.FileTools;

public class FileServiceManager extends AbstractTechnicalServiceManager {

	private List<JsonQueryInfo> jsonInfo;
	private List<DAOInfo> daoInfos;
	private List<NetworkInfo> networkInfos;
	private List<TaskInfo> tasksInfos;
	private List<DiscordInfo> discordInfos;
	private List<FileAccessInfo> fileInfos;
	
	
	
	private File logsDirectory = new File(MTGConstants.DATA_DIR,"audits");


	private List<TalkMessage> jsonMessages;

	public static final int SCHEDULE_TIMER_MS=1;


	public FileServiceManager() {
		super();
		jsonInfo= new ArrayList<>();
		networkInfos = new ArrayList<>();
		daoInfos = new ArrayList<>();
		tasksInfos = new ArrayList<>();
		fileInfos = new ArrayList<>();
		discordInfos = new ArrayList<>();
		jsonMessages=new ArrayList<>();
		
		
		if(!logsDirectory.exists())
		{
			try {
				FileTools.forceMkdir(logsDirectory);
			} catch (IOException e) {
				logger.error("error creating {} : {}",logsDirectory.getAbsolutePath(),e);
			}
		}


		logger.debug("Starting Log backup timer scheduled at {}ms",TimeUnit.HOURS.toMillis(SCHEDULE_TIMER_MS));
		ThreadManager.getInstance().timer(new MTGRunnable() {

			@Override
			protected void auditedRun() {
				storeAll();

			}
		},"TechnicalService Timer",SCHEDULE_TIMER_MS,TimeUnit.HOURS);

	}


	public void storeAll()
	{

		if(isEnable())
		{
				try {
					storeItems(JsonQueryInfo.class,jsonInfo.stream().filter(Objects::nonNull).toList());
					storeItems(DAOInfo.class,daoInfos.stream().filter(Objects::nonNull).toList());
					storeItems(NetworkInfo.class,networkInfos.stream().filter(Objects::nonNull).toList());
					storeItems(TaskInfo.class,tasksInfos.stream().filter(Objects::nonNull).toList());
					storeItems(DiscordInfo.class,discordInfos.stream().filter(Objects::nonNull).toList());
					storeItems(FileAccessInfo.class,fileInfos.stream().filter(Objects::nonNull).toList());
					storeItems(TalkMessage.class,jsonMessages.stream().filter(Objects::nonNull).toList());
				}
				catch(Exception e)
				{
					logger.error(e);
				}
		}

	}

	public void restore() throws IOException
	{

		if(isEnable())
		{
			ThreadManager.getInstance().executeThread(new MTGRunnable() {
				
				@Override
				protected void auditedRun() {

					for(File f : FileTools.listFiles(logsDirectory))
					{
						if(f.getName().startsWith(JsonQueryInfo.class.getSimpleName()))
							try {
								jsonInfo.addAll(restore(f,JsonQueryInfo.class).stream().distinct().toList());
							} catch (IOException e) {
								logger.error(e);
							}
						else if(f.getName().startsWith(DAOInfo.class.getSimpleName()))
							try {
								daoInfos.addAll(restore(f,DAOInfo.class).stream().distinct().toList());
							} catch (IOException e) {
								logger.error(e);							}
						else if(f.getName().startsWith(TaskInfo.class.getSimpleName()))
							try {
								tasksInfos.addAll(restore(f,TaskInfo.class).stream().distinct().toList());
							} catch (IOException e) {
								logger.error(e);							}
						else if(f.getName().startsWith(NetworkInfo.class.getSimpleName()))
							try {
								networkInfos.addAll(restore(f,NetworkInfo.class).stream().distinct().toList());
							} catch (IOException e) {
								logger.error(e);							}
						else if(f.getName().startsWith(DiscordInfo.class.getSimpleName()))
							try {
								discordInfos.addAll(restore(f,DiscordInfo.class).stream().distinct().toList());
							} catch (IOException e) {
								logger.error(e);							}
						else if(f.getName().startsWith(FileAccessInfo.class.getSimpleName()))
							try {
								fileInfos.addAll(restore(f,FileAccessInfo.class).stream().distinct().toList());
							} catch (IOException e) {
								logger.error(e);							}
						else if(f.getName().startsWith(TalkMessage.class.getSimpleName()))
							try {
								jsonMessages.addAll(restore(f,TalkMessage.class).stream().distinct().toList());
							} catch (IOException e) {
								logger.error(e);							}
					}
					logger.info("Technical data are loaded");
					
				}
			}, "Loading technical data");
			
			
			logger.info("TechnicalService is enable");
		}
		else
		{
			logger.warn("TechnicalService is disabled");
		}


	}

	private <T  extends AbstractAuditableItem> List<T> restore(File f, Class<T> classe) throws IOException {
		
		if(serializer==null)
			serializer = new JsonExport();
		
		return serializer.fromJsonList(FileTools.readFile(f), classe);
	}

	private <T extends AbstractAuditableItem> void storeItems(Class<T> classe, List<T> items) throws IOException
	{
		if(isEnable())
		{

			if(serializer==null)
				serializer = new JsonExport();
			
			
			FileTools.saveLargeFile(Paths.get(logsDirectory.getAbsolutePath(),classe.getSimpleName()+".json").toFile(), serializer.toJson(items),MTGConstants.DEFAULT_ENCODING);
		}
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
	
	public void store(JsonQueryInfo info)
	{
		info.setLocation(translator.getLocationFor(info.getIp()));
		jsonInfo.add(info);
	}

	public void store(DiscordInfo info) {
		discordInfos.add(info);
	}
	
	public void store(FileAccessInfo info)
	{
		fileInfos.add(info);
	}

	public void store(NetworkInfo info)
	{
		networkInfos.add(info);
	}

	public void store(TaskInfo info)
	{
		tasksInfos.add(info);
	}

	public void store(DAOInfo info)
	{
		daoInfos.add(info);
	}
	


	public void store(TalkMessage msg) {
		jsonMessages.add(msg);
		
	}


}
