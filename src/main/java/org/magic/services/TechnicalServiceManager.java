package org.magic.services;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.magic.api.beans.technical.audit.AbstractAuditableItem;
import org.magic.api.beans.technical.audit.DAOInfo;
import org.magic.api.beans.technical.audit.DiscordInfo;
import org.magic.api.beans.technical.audit.JsonQueryInfo;
import org.magic.api.beans.technical.audit.NetworkInfo;
import org.magic.api.beans.technical.audit.TaskInfo;
import org.magic.api.exports.impl.JsonExport;
import org.magic.services.logging.MTGLogger;
import org.magic.services.providers.IPTranslator;
import org.magic.services.threads.MTGRunnable;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.FileTools;

public class TechnicalServiceManager {

	private static TechnicalServiceManager inst;
	private List<JsonQueryInfo> jsonInfo;
	private List<DAOInfo> daoInfos;
	private List<NetworkInfo> networkInfos;
	private List<TaskInfo> tasksInfos;
	private List<DiscordInfo> discordInfos;
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	private JsonExport export;
	private File logsDirectory = new File(MTGConstants.DATA_DIR,"audits");
	private IPTranslator translator;
	private boolean enable =true;

	public static final int SCHEDULE_TIMER_MS=1;

	public static TechnicalServiceManager inst()
	{
		if(inst==null)
			inst = new TechnicalServiceManager();

		return inst;
	}


	public void enable(boolean enable)
	{
		this.enable =enable;
	}


	public TechnicalServiceManager() {
		jsonInfo= new ArrayList<>();
		networkInfos = new ArrayList<>();
		daoInfos = new ArrayList<>();
		tasksInfos = new ArrayList<>();
		discordInfos = new ArrayList<>();
		export = new JsonExport();
		translator = new IPTranslator();

		if(!logsDirectory.exists())
		{
			try {
				FileUtils.forceMkdir(logsDirectory);
			} catch (IOException e) {
				logger.error("error creating {} : {}",logsDirectory.getAbsolutePath(),e);
			}
		}


		logger.info("Starting Log backup timer scheduled at {}ms",TimeUnit.HOURS.toMillis(SCHEDULE_TIMER_MS));
		ThreadManager.getInstance().timer(new MTGRunnable() {

			@Override
			protected void auditedRun() {
				storeAll();

			}
		},"TechnicalService Timer",SCHEDULE_TIMER_MS,TimeUnit.HOURS);

	}

	public void storeAll()
	{

		if(enable)
		{
				try {
					storeItems(JsonQueryInfo.class,jsonInfo.stream().filter(Objects::nonNull).toList());
					storeItems(DAOInfo.class,daoInfos.stream().filter(Objects::nonNull).toList());
					storeItems(NetworkInfo.class,networkInfos.stream().filter(Objects::nonNull).toList());
					storeItems(TaskInfo.class,tasksInfos.stream().filter(Objects::nonNull).toList());
					storeItems(DiscordInfo.class,discordInfos.stream().filter(Objects::nonNull).toList());
				}
				catch(Exception e)
				{
					logger.error(e);
				}
		}

	}


	public void restore() throws IOException
	{

		if(enable)
		{
			for(File f : FileTools.listFiles(logsDirectory))
			{
				if(f.getName().startsWith(JsonQueryInfo.class.getSimpleName()))
					jsonInfo.addAll(restore(f,JsonQueryInfo.class).stream().distinct().toList());
				else if(f.getName().startsWith(DAOInfo.class.getSimpleName()))
					daoInfos.addAll(restore(f,DAOInfo.class).stream().distinct().toList());
				else if(f.getName().startsWith(TaskInfo.class.getSimpleName()))
					tasksInfos.addAll(restore(f,TaskInfo.class).stream().distinct().toList());
				else if(f.getName().startsWith(NetworkInfo.class.getSimpleName()))
					networkInfos.addAll(restore(f,NetworkInfo.class).stream().distinct().toList());
				else if(f.getName().startsWith(DiscordInfo.class.getSimpleName()))
					discordInfos.addAll(restore(f,DiscordInfo.class).stream().distinct().toList());
			}
		}
		else
		{
			logger.warn("TechnicalService is disabled");
		}


	}

	private <T  extends AbstractAuditableItem> List<T> restore(File f, Class<T> classe) throws IOException {
		return export.fromJsonList(FileTools.readFile(f), classe);
	}

	private <T extends AbstractAuditableItem> void storeItems(Class<T> classe, List<T> items) throws IOException
	{
		if(enable)
		{
			FileTools.saveLargeFile(Paths.get(logsDirectory.getAbsolutePath(),classe.getSimpleName()+".json").toFile(), export.toJson(items),MTGConstants.DEFAULT_ENCODING);
		}
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

	public void store(JsonQueryInfo info)
	{
		info.setLocation(translator.getLocationFor(info.getIp()));
		jsonInfo.add(info);
	}

	public void store(DiscordInfo info) {
		discordInfos.add(info);

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

	public Set<Entry<Object, Object>> getSystemInfo() {
		return System.getProperties().entrySet();
	}

	public ThreadInfo[] getThreadsInfos() {
		return ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
	}


}
