package org.magic.services;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.magic.api.beans.audit.AbstractAuditableItem;
import org.magic.api.beans.audit.DAOInfo;
import org.magic.api.beans.audit.DiscordInfo;
import org.magic.api.beans.audit.JsonQueryInfo;
import org.magic.api.beans.audit.NetworkInfo;
import org.magic.api.beans.audit.TaskInfo;
import org.magic.api.beans.audit.TaskInfo.STATE;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.abstracts.AbstractEmbeddedCacheProvider;
import org.magic.tools.FileTools;
import org.magic.tools.UITools;

public class TechnicalServiceManager {

	private static TechnicalServiceManager inst;
	
	
	AbstractEmbeddedCacheProvider<AbstractAuditableItem, List<AbstractAuditableItem>> cache;
	
	
	private List<JsonQueryInfo> jsonInfo;
	private List<DAOInfo> daoInfos;
	private List<NetworkInfo> networkInfos;
	private List<TaskInfo> tasksInfos;
	private List<DiscordInfo> discordInfos;
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	private JsonExport export;
	private File logsDirectory = new File(MTGConstants.DATA_DIR,"audits");
	
	
	public static TechnicalServiceManager inst()
	{
		if(inst==null)
			inst = new TechnicalServiceManager();
		
		return inst;
	}
	
	public void store()
	{
		try {
			storeItems(JsonQueryInfo.class,jsonInfo);
			storeItems(DAOInfo.class,daoInfos);
			storeItems(NetworkInfo.class,networkInfos);
			storeItems(TaskInfo.class,tasksInfos);
			storeItems(DiscordInfo.class,discordInfos);
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		
	}
	
	
	public void restore() throws IOException
	{
		
		for(File f : FileTools.listFiles(logsDirectory))
		{
			if(f.getName().startsWith(JsonQueryInfo.class.getSimpleName()+"_"))
				jsonInfo.addAll(export.fromJsonList(FileTools.readFile(f), JsonQueryInfo.class));
			else if(f.getName().startsWith(DAOInfo.class.getSimpleName()+"_"))
				daoInfos.addAll(export.fromJsonList(FileTools.readFile(f), DAOInfo.class));
			else if(f.getName().startsWith(TaskInfo.class.getSimpleName()+"_"))
				tasksInfos.addAll(export.fromJsonList(FileTools.readFile(f), TaskInfo.class));		
			else if(f.getName().startsWith(NetworkInfo.class.getSimpleName()+"_"))
				networkInfos.addAll(export.fromJsonList(FileTools.readFile(f), NetworkInfo.class));
			else if(f.getName().startsWith(DiscordInfo.class.getSimpleName()+"_"))
				discordInfos.addAll(export.fromJsonList(FileTools.readFile(f), DiscordInfo.class));	
			
		}
	}

	private <T extends AbstractAuditableItem> void storeItems(Class<T> classe, List<T> items) throws IOException
	{
		FileTools.saveFile(Paths.get(logsDirectory.getAbsolutePath(),classe.getSimpleName()+"_"+UITools.formatDate(new Date(),"ddMMYYYY")+"_.json").toFile(), export.toJson(items));
	}
	
	
	public TechnicalServiceManager() {
		jsonInfo= new ArrayList<>();
		networkInfos = new ArrayList<>();
		daoInfos = new ArrayList<>();
		tasksInfos = new ArrayList<>();
		discordInfos = new ArrayList<>();
		export = new JsonExport();
		
		
		try {
			restore();
		} catch (IOException e) {
		logger.error("error restore previous log",e);
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
		jsonInfo.add(info);
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
	
	public void cleanAll() {
		tasksInfos.removeIf(t->t.getStatus()==STATE.FINISHED);
		networkInfos.removeIf(t->t.getEnd()!=null);
		daoInfos.removeIf(t->t.getEnd()!=null);
		jsonInfo.removeIf(t->t.getEnd()!=null);
	}

	public Set<Entry<Object, Object>> getSystemInfo() {
		return System.getProperties().entrySet();
	}

	public ThreadInfo[] getThreadsInfos() {
		return ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
	}

	public void store(DiscordInfo info) {
		discordInfos.add(info);
		
	}
	
	
}
