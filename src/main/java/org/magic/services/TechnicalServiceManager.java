package org.magic.services;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.magic.api.beans.audit.DAOInfo;
import org.magic.api.beans.audit.JsonQueryInfo;
import org.magic.api.beans.audit.NetworkInfo;
import org.magic.api.beans.audit.ThreadInfo;
import org.magic.api.beans.audit.ThreadInfo.STATE;

public class TechnicalServiceManager {

	private static TechnicalServiceManager inst;
	
	private List<JsonQueryInfo> jsonInfo;
	private List<DAOInfo> daoInfos;
	private List<NetworkInfo> networkInfos;
	private List<ThreadInfo> tasksInfos;

	public static TechnicalServiceManager inst()
	{
		if(inst==null)
			inst = new TechnicalServiceManager();
		
		return inst;
	}
	
	public TechnicalServiceManager() {
		jsonInfo= new ArrayList<>();
		networkInfos = new ArrayList<>();
		daoInfos = new ArrayList<>();
		tasksInfos = new ArrayList<>();
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
	
	public List<ThreadInfo> getTasksInfos() {
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
	
	public void store(ThreadInfo info)
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
		daoInfos.removeIf(t->t.getEndDate()!=null);
		jsonInfo.removeIf(t->t.getEnd()!=null);
	}

	public Set<Entry<Object, Object>> systemInfos() {
		return System.getProperties().entrySet();
	}

	public java.lang.management.ThreadInfo[] getThreadsInfos() {
		return ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
	}
	
	
}
