package org.magic.services;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.log4j.Logger;

public class ThreadManager {

	private static ThreadManager inst;
	private ThreadPoolExecutor executor;
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	private ThreadFactory factory;
	private String name="";

	public static ThreadManager getInstance() {
		if (inst == null)
			inst = new ThreadManager();

		return inst;
	}
	
	
	
	public Future<?> toFuture(Runnable task)
	{
		return executor.submit(task);
	}
		
	public void execute(Runnable task, String name) {
		this.name=name;
		executor.execute(task);
		logger.debug(String.format("Execution:  [%d/%d] Active: %d, Completed: %d, Task: %d %s", 
				executor.getPoolSize(),
				executor.getCorePoolSize(), 
				executor.getActiveCount(), 
				executor.getCompletedTaskCount(),
				executor.getTaskCount(), 
				name));
	}
	
	public void execute(SwingWorker<?, ?> sw) {
		sw.execute();
	}
	
	public void runInEdt(Runnable runnable) {
		if (SwingUtilities.isEventDispatchThread())
			runnable.run();
		else
			SwingUtilities.invokeLater(runnable);
	}

	private ThreadManager() {
		
		ThreadFactory factory = new ThreadFactory() {
			
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r,"mtgThread-"+name);
				t.setDaemon(true);
				return t;
			}
		};
		
		executor = (ThreadPoolExecutor) Executors.newCachedThreadPool(factory);
	}
}


