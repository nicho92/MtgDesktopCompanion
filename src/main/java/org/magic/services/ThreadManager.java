package org.magic.services;

import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

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

	public void execute(Runnable task) {
		execute(task,"Thread");
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

	public ThreadPoolExecutor getExecutor() {
		return executor;
	}

	public void setCorePoolSize(int core) {
		executor.setCorePoolSize(core);
	}

	public void setMaximumPoolSize(int core) {
		executor.setMaximumPoolSize(core);

	}

}

class RejectedExecutionHandlerImpl implements RejectedExecutionHandler {

	Logger logger = MTGLogger.getLogger(this.getClass());

	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		logger.error(r.toString() + " is rejected");
	}
}
