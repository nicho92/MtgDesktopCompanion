package org.magic.services;

import java.beans.PropertyChangeEvent;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.magic.tools.Chrono;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

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
	
	public void executeThread(Runnable task, String name) {
		this.name=name;
		executor.execute(task);
		log();
	}
	
	public Future submitThread(Runnable task, String name) {
		this.name=name;
		log();
		return executor.submit(task);
	}
	
	public <V> Future<V> executeThread(Callable<V> task) {
		log();
		return executor.submit(task);
	}
	
	public void invokeLater(Runnable task) {
		SwingUtilities.invokeLater(task);
	}

	public void runInEdt(SwingWorker<?, ?> runnable,String name) {
		this.name=name;
		runnable.execute();
		Chrono c = new Chrono();
		
		runnable.addPropertyChangeListener((PropertyChangeEvent ev)->{
			if(ev.getNewValue().toString().equals("STARTED"))
			{ 
				c.start();
				logger.trace(name+"\t"+ev.getSource()+"\t STARTED");
			}
			
			if(ev.getNewValue().toString().equals("DONE"))
			{ 
				logger.trace(name+"\t"+ev.getSource().getClass().getName()+"\t FINISHED IN "+c.stopInMillisecond()+"ms.");
			}
		});
	}
	
	private void log() {
		logger.trace(String.format("[Monitor] [%d/%d] Active: %d, Completed: %d, Task: %d : %s", 
				executor.getPoolSize(),
				executor.getCorePoolSize(), 
				executor.getActiveCount(), 
				executor.getCompletedTaskCount(),
				executor.getTaskCount(),
				name));
	}

	private ThreadManager() {
		factory = new ThreadFactoryBuilder().setNameFormat("mtg-threadpool-%d").setDaemon(true).build();
		executor = (ThreadPoolExecutor) Executors.newCachedThreadPool(factory);
	}

	public ThreadFactory getFactory() {
		return factory;
	}

}


