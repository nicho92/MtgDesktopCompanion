package org.magic.tools;

import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.SwingWorker;

public class ThreadMonitor extends SwingWorker<Void, String>
{
    private ThreadPoolExecutor executor;
    private int seconds;
    private boolean run=true;
    private String info="";
    public ThreadMonitor(ThreadPoolExecutor executor, int delay)
    {
        this.executor = executor;
        this.seconds=delay;
    }
    public void shutdown(){
        this.run=false;
    }
    
    public String getInfo()
    {
    	return info;
    }
    
    
     public void runs() {
    	while(run){
            info=
                String.format("[monitor] [%d/%d] Active: %d, Completed: %d, Task: %d, isShutdown: %s, isTerminated: %s",
                    this.executor.getPoolSize(),
                    this.executor.getCorePoolSize(),
                    this.executor.getActiveCount(),
                    this.executor.getCompletedTaskCount(),
                    this.executor.getTaskCount(),
                    this.executor.isShutdown(),
                    this.executor.isTerminated());
            try {
                Thread.sleep(seconds*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }
		
	}
	@Override
	protected Void doInBackground() throws Exception {
		publish(info);
		return null;
		
	}
}