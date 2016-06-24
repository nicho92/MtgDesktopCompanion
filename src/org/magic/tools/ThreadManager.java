package org.magic.tools;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public class ThreadManager {

	static ThreadManager inst;
	static final Logger logger = LogManager.getLogger(ThreadManager.class.getName());

	ThreadPoolExecutor executor;
	ThreadFactory threadFactory ;
	
	public static ThreadManager getInstance()
	{
		if (inst ==null)
			inst=new ThreadManager();
		
		return inst;
	}
	
	private ThreadManager()
	{
		threadFactory = Executors.defaultThreadFactory();
		executor=new ThreadPoolExecutor(4, 5, 10, TimeUnit.MILLISECONDS,new ArrayBlockingQueue<Runnable>(2),threadFactory, new RejectedExecutionHandlerImpl());
	}
	
	public void execute(Runnable task,String name)
	{
		
		executor.execute(task);
		logger.info(String.format("Execution:  [%d/%d] Active: %d, Completed: %d, Task: %d " + name,
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

	static final Logger logger = LogManager.getLogger(RejectedExecutionHandlerImpl.class.getName());

    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
       logger.error(r.toString() + " is rejected");
    }
}


class NamedThreadFactory implements ThreadFactory
{
	
	
	@Override
	public Thread newThread(Runnable r) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
