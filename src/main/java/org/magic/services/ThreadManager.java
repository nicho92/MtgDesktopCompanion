package org.magic.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.SwingUtilities;

public class ThreadManager {

	private static ThreadManager inst;

	private String info;
	private ThreadPoolExecutor executor;

	public static ThreadManager getInstance() {
		if (inst == null)
			inst = new ThreadManager();

		return inst;
	}

	public String getInfo() {
		return info;
	}
	
	public void execute(Runnable task, String name) {
		
		executor.execute(task);
		info = String.format("Execution:  [%d/%d] Active: %d, Completed: %d, Task: %d %s", 
							executor.getPoolSize(),
							executor.getCorePoolSize(), 
							executor.getActiveCount(), 
							executor.getCompletedTaskCount(),
							executor.getTaskCount(), 
							name);
	
		
		
	}
	
	public void runInEdt(Runnable runnable) {
		if (SwingUtilities.isEventDispatchThread())
			runnable.run();
		else
			SwingUtilities.invokeLater(runnable);
	}

	private ThreadManager() {
		executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
	}

	public ExecutorService getExecutor() {
		return executor;
	}

}
