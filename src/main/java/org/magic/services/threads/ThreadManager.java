package org.magic.services.threads;

import java.beans.PropertyChangeEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.tools.Chrono;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class ThreadManager {

	private static ThreadManager inst;
	private ThreadPoolExecutor executor;
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	private ThreadFactory factory;
	private String name="";
	private Map<SwingWorker<?, ?>, ThreadInfo> tasksMap;
	
	
	public static ThreadManager getInstance() {
		if (inst == null)
			inst = new ThreadManager();

		return inst;
	}
	
	public void executeThread(Runnable task, String name) {
		
		if(task==null)
		{
			logger.error("task is null for " + name);
			return;
		}
		
		this.name=name;
		executor.execute(task);
		log();
	}
	
	public Future<?> submitThread(Runnable task, String name) {
		return submitCallable(Executors.callable(task), name);
	}
	
	
	public <V> Future<V> submitCallable(Callable<V> task,String name) {
		this.name=name;
		log();
		
		return executor.submit(task);
	}
	
	public void invokeLater(Runnable task) {
		SwingUtilities.invokeLater(task);
	}
	
	public void runInEdt(SwingWorker<?, ?> runnable,String name) {
		var info = new ThreadInfo();
		info.setStartDate(new Date());
		info.setName(name);
		tasksMap.put(runnable, info);
		logger.trace("running EDT : " + name);
		runnable.execute();
		var c = new Chrono();
		
		runnable.addPropertyChangeListener((PropertyChangeEvent ev)->{
			if(ev.getNewValue().toString().equals("STARTED"))
			{ 
				c.start();
				logger.trace(name+"\t"+ev.getSource()+"\t STARTED");
			}
			
			if(ev.getNewValue().toString().equals("DONE")) {
				info.setEndDate(new Date());
				info.setDuration(c.stopInMillisecond());
				logger.trace(name+"\t"+ev.getSource().getClass().getName()+"\t FINISHED IN "+c.stopInMillisecond()+"ms.");
			}
		});
	}
	
	public String log() {
		var s = String.format("[Monitor] [%d/%d] Active: %d, Completed: %d, Task: %d : %s", 
				executor.getPoolSize(),
				executor.getCorePoolSize(), 
				executor.getActiveCount(), 
				executor.getCompletedTaskCount(),
				executor.getTaskCount(),
				name);
		logger.trace(s);
		return s;
	}

	private ThreadManager() {
		
		var tpc = MTGControler.getInstance().getThreadPoolConfig();
		
		tasksMap = new HashMap<>();
		
		
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
			default :  Executors.newCachedThreadPool(factory);break;
		}
		logger.debug("init ThreadManager config="+tpc);
	}
	
	
	public void stop()
	{
		executor.shutdown();
	}
	
	public void launchMonitor()
	{
		executor.execute(()->{
			while(true) {
				logger.debug(log());
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					logger.error(e);
				}
			}
				
		});
		
		
	}
	
	
	public Map<SwingWorker<?,?>,ThreadInfo> listTasks()
	{
		return tasksMap;
	}

	public ThreadFactory getFactory() {
		return factory;
	}
	
	
}


