package org.magic.services;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.commons.lang3.ThreadUtils;
import org.apache.log4j.Logger;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import de.vandermeer.asciitable.AsciiTable;

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
	
	
	
	public Future executeAsFuture(Runnable task)
	{
		Future<?> f = executor.submit(task);
		log();
		return f;
	}
	
	
	public Future executeAsFuture(Callable task)
	{
		Future f = executor.submit(task);
		log();
		return f;
	}
	
		
	public void execute(Runnable task, String name) {
		this.name=name;
		executor.submit(task);
		log();
	}

	public void execute(SwingWorker<?, ?> sw,String name) {
		 sw.addPropertyChangeListener((PropertyChangeEvent pce)->
			logger.debug(pce.getSource().getClass() + ":" + pce.getOldValue()+"->"+pce.getNewValue())
		);
		runInEdt(sw,name);
	}
	
	
	
	public void runInEdt(Runnable runnable) {
		runInEdt(runnable, "EDT-Thread");
	}
	
		

	public void runInEdt(Runnable runnable,String name) {
		this.name=name;
		if (SwingUtilities.isEventDispatchThread())
			executor.execute(runnable);
		else
			SwingUtilities.invokeLater(runnable);
		
		log();
	}
	
	private void log() {
		logger.debug(String.format("[Monitor] [%d/%d] Active: %d, Completed: %d, Task: %d", 
				executor.getPoolSize(),
				executor.getCorePoolSize(), 
				executor.getActiveCount(), 
				executor.getCompletedTaskCount(),
				executor.getTaskCount()));
	}

	private ThreadManager() {
		factory = new ThreadFactoryBuilder().setNameFormat("mtg-threadpool-%d").setDaemon(true).build();
		executor = (ThreadPoolExecutor) Executors.newCachedThreadPool(factory);
	}
	
	
}


