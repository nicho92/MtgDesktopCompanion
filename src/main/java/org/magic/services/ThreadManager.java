package org.magic.services;

import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

public class ThreadManager {

	static ThreadManager inst;
	Logger logger = MTGLogger.getLogger(this.getClass());

	private String name;
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

	public void runInEdt(Runnable runnable) {
		if (SwingUtilities.isEventDispatchThread())
			runnable.run();
		else
			SwingUtilities.invokeLater(runnable);
	}

	public void runInEdt(Runnable runnable, String name) {
		this.name = name;
		if (SwingUtilities.isEventDispatchThread())
			runnable.run();
		else
			SwingUtilities.invokeLater(runnable);
	}

	private ThreadManager() {
		// LinkedBlockingQueue // ArrayBlockingQueue
		executor = new ThreadPoolExecutor(50, 50, 80, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(10)) {
			@Override
			protected void beforeExecute(Thread t, Runnable r) {
				t.setName(name);
			}

			@Override
			protected void afterExecute(Runnable r, Throwable t) {
				// do nothing
			}

			@Override
			protected <V> RunnableFuture<V> newTaskFor(final Runnable runnable, V v) {
				return new FutureTask<V>(runnable, v) {
					public String toString() {
						return runnable.toString();
					}
				};
			}
		};
	}

	public void execute(Runnable task, String name) {
		this.name = name;
		executor.execute(task);

		info = (String.format("Execution:  [%d/%d] Active: %d, Completed: %d, Task: %d %s", executor.getPoolSize(),
				executor.getCorePoolSize(), executor.getActiveCount(), executor.getCompletedTaskCount(),
				executor.getTaskCount(), name));
	}

	public void execute(Runnable task) {
		this.name = "Thread";
		executor.execute(task);
		info = (String.format("Execution:  [%d/%d] Active: %d, Completed: %d, Task: %d %s", executor.getPoolSize(),
				executor.getCorePoolSize(), executor.getActiveCount(), executor.getCompletedTaskCount(),
				executor.getTaskCount(), name));
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
