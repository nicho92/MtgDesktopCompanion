package org.magic.services.threads;

import java.beans.PropertyChangeEvent;
import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.magic.api.beans.audit.TaskInfo;
import org.magic.api.beans.audit.TaskInfo.STATE;
import org.magic.api.beans.audit.TaskInfo.TYPE;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.TechnicalServiceManager;
import org.magic.tools.Chrono;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ThreadManager {

	private static ThreadManager inst;
	private ThreadPoolExecutor executor;
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	private ThreadFactory factory;
	
	
	public static ThreadManager getInstance() {
		if (inst == null)
			inst = new ThreadManager();

		return inst;
	}

	public void executeThread(MTGRunnable task, String name) {
		
		if(task==null)
		{
			logger.error("task is null for " + name);
			return;
		}
		
		task.getInfo().setName(name);
		
		TechnicalServiceManager.inst().store(task.getInfo());
		
		executor.execute(task);
	}
	
	public Future<?> submitThread(MTGRunnable task, String name) {
		
		task.getInfo().setName(name);
		TechnicalServiceManager.inst().store(task.getInfo());
		return submitCallable(Executors.callable(task), name);
	}
	
	public <V> Future<V> submitCallable(Callable<V> task,String name) {
		return executor.submit(task);
	}
	
	
	public void invokeLater(MTGRunnable task, String name) {
		
		task.getInfo().setName(name);
		TechnicalServiceManager.inst().store(task.getInfo());
		SwingUtilities.invokeLater(task);
	}
	
	public void runInEdt(SwingWorker<?, ?> runnable,String name) {
		
		var info = new TaskInfo(runnable);
			  info.setName(name);
			  info.setType(TYPE.WORKER);
			  TechnicalServiceManager.inst().store(info);			
		
		runnable.execute();
	
		runnable.addPropertyChangeListener((PropertyChangeEvent ev)->{
			if(ev.getNewValue().toString().equals("STARTED"))
			{ 
				info.setStart(Instant.now());
				info.setStatus(STATE.STARTED);
			}
			
			if(ev.getNewValue().toString().equals("DONE")) {
				info.setEnd(Instant.now());
				info.setStatus(STATE.FINISHED);
			}
			
			if(ev.getNewValue().toString().equals("CANCELED")) {
				info.setEnd(Instant.now());
				info.setStatus(STATE.CANCELED);
			}
		});
	}
	
	private ThreadManager() {
		
		var tpc = MTGControler.getInstance().getThreadPoolConfig();
		
		factory = new ThreadFactoryBuilder()
						.setNameFormat(tpc.getNameFormat())
						.setDaemon(tpc.isDaemon())
						.build();
		
		switch (tpc.getThreadPool())
		{
			case CACHED:executor = (ThreadPoolExecutor) Executors.newCachedThreadPool(factory);break;
			case FIXED: executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(tpc.getCorePool(),factory);break;
			case SCHEDULE:executor = (ThreadPoolExecutor) Executors.newScheduledThreadPool(tpc.getCorePool(),factory);break;
			case SINGLE : executor = (ThreadPoolExecutor) Executors.newSingleThreadExecutor(factory);break;
			default :  executor = (ThreadPoolExecutor) Executors.newCachedThreadPool(factory);break;
		}
		logger.debug("init ThreadManager config="+tpc);
	}
	
	public void stop()
	{
		executor.shutdown();
	}
	
	public ThreadFactory getFactory() {
		return factory;
	}
	
	public JsonObject toJson() {
		var objExe = new JsonObject();
			objExe.addProperty("activeCount", executor.getActiveCount());
			objExe.addProperty("completeTaskCount", executor.getCompletedTaskCount());
			objExe.addProperty("corePoolSize", executor.getCorePoolSize());
			objExe.addProperty("poolSize", executor.getPoolSize());
			objExe.addProperty("taskCount", executor.getTaskCount());
			objExe.addProperty("factory", executor.getThreadFactory().toString());
			objExe.addProperty("executor", executor.getClass().getCanonicalName());
		
	var arr = new JsonArray();
	for(var e : TechnicalServiceManager.inst().getTasksInfos())
		arr.add(e.toJson());

	var objRet = new JsonObject();
		objRet.add("factory", objExe);
		objRet.add("tasks", arr);

		return objRet;
	}

	
	
	
}


