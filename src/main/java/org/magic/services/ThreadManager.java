package org.magic.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class ThreadManager {

	private static ThreadManager inst;
	private ThreadPoolExecutor executor;
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	private ThreadFactory factory;
	private List<Thread> threads;
	private String name="";

	public static ThreadManager getInstance() {
		if (inst == null)
			inst = new ThreadManager();

		return inst;
	}
	
	
	@Deprecated	
	public void execute(Runnable task, String name) {
		this.name=name;
		executor.submit(task);
		log();
	}


	public void executeThread(Runnable task, String name) {
		this.name=name;
		executor.submit(task);
		log();
		
	}
	
	public void invokeLater(Runnable task) {
		SwingUtilities.invokeLater(task);
	}
	
	
	public void runInEdt(Runnable runnable) {
		runInEdt(runnable, "MTGThread");
	}
	
		

	public void runInEdt(Runnable runnable,String name) {
		this.name="EDT-"+name;
		if (SwingUtilities.isEventDispatchThread())
			executor.execute(runnable);
		else
			SwingUtilities.invokeLater(runnable);
		
		log();
	}
	
	private void log() {
		logger.trace(String.format("[Monitor] [%d/%d] Active: %d, Completed: %d, Task: %d : %s", 
				executor.getPoolSize(),
				executor.getCorePoolSize(), 
				executor.getActiveCount(), 
				executor.getCompletedTaskCount(),
				executor.getTaskCount(),
				name));
		
		
		threads.forEach(t->logger.trace("THREAD-" + t.getId()+"\t"+t.getName() +"\t" + t.isAlive()));
	}

	private ThreadManager() {
		
		threads = new ArrayList<>();
//		factory = new ThreadFactory() {
//			
//			@Override
//			public Thread newThread(Runnable r) {
//				Thread t = new Thread(r,name);
//				threads.add(t);
//				return t;
//			}
//		};
		
		factory = new ThreadFactoryBuilder().setNameFormat("mtg-threadpool-%d").setDaemon(true).build();
		executor = (ThreadPoolExecutor) Executors.newCachedThreadPool(factory);
	}


	
}


