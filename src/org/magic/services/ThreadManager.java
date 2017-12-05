package org.magic.services;

import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.magic.tools.MTGLogger;


public class ThreadManager {

	static ThreadManager inst;
	Logger logger = MTGLogger.getLogger(this.getClass());

	private String name;
	private String info;
	ThreadPoolExecutor executor;
	ThreadFactory threadFactory ;
	
	public static ThreadManager getInstance()
	{
		if (inst ==null)
			inst=new ThreadManager();
		
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
	

	private ThreadManager()
	{
		threadFactory = Executors.defaultThreadFactory();
		//LinkedBlockingQueue // ArrayBlockingQueue
		 executor = new ThreadPoolExecutor(40, 40,  80, TimeUnit.MILLISECONDS,  new LinkedBlockingQueue<Runnable>(10))
		 {   
			    protected void beforeExecute(Thread t, Runnable r) { 
			         t.setName(name);
			    }

			    protected void afterExecute(Runnable r, Throwable t) { 
			        // Thread.currentThread().setName("");
			    } 

			    protected <V> RunnableFuture<V> newTaskFor(final Runnable runnable, V v) {
			         return new FutureTask<V>(runnable, v) {
			             public String toString() {
			                return runnable.toString();
			             }
			         };
			     };
		 };
	}
	
	public void execute(Runnable task,String name)
	{
		this.name=name;
		executor.execute(task);
		
		info =(String.format("Execution:  [%d/%d] Active: %d, Completed: %d, Task: %d " + name,
                executor.getPoolSize(),
                executor.getCorePoolSize(),
                executor.getActiveCount(),
                executor.getCompletedTaskCount(),
                executor.getTaskCount()));
		
		//logger.trace(info);
		
	}
	
	public void execute(Runnable task)
	{
		this.name="Thread";
		executor.execute(task);
		info =(String.format("Execution:  [%d/%d] Active: %d, Completed: %d, Task: %d " + name,
                executor.getPoolSize(),
                executor.getCorePoolSize(),
                executor.getActiveCount(),
                executor.getCompletedTaskCount(),
                executor.getTaskCount()));
	}

	public ThreadPoolExecutor getExecutor() {
		return executor;
	}
	
	public void setCorePoolSize(int core)
	{
		executor.setCorePoolSize(core);
	}
	
	public void setMaximumPoolSize(int core)
	{
		executor.setMaximumPoolSize(core);
		
	}
	
}

class RejectedExecutionHandlerImpl implements RejectedExecutionHandler {

	static final Logger logger = MTGLogger.getLogger(RejectedExecutionHandlerImpl.class);

    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
       logger.error(r.toString() + " is rejected");
    }
} 
