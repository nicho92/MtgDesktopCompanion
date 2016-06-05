package org.magic.tools;

import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.JLabel;
import javax.swing.SwingWorker;

public class ThreadMonitor implements Runnable
{
    private ThreadPoolExecutor executor;
    private int seconds;
    private boolean run=true;
    private String info="";
    private JLabel lab;
    
    public ThreadMonitor(ThreadPoolExecutor executor, int delay, JLabel lab)
    {
        this.executor = executor;
        this.seconds=delay;
        this.lab=lab;
    }
    public void shutdown(){
        this.run=false;
    }
    
    public String getInfo()
    {
    	return info;
    }
    
    
     public void run() {
    	while(run)
    	{
            info=
                String.format("[monitor] [%d/%d] Active: %d, Completed: %d, Task: %d",
                    this.executor.getPoolSize(),
                    this.executor.getCorePoolSize(),
                    this.executor.getActiveCount(),
                    this.executor.getCompletedTaskCount(),
                    this.executor.getTaskCount()
                   );
            
            lab.setText(info);
            try {
                Thread.sleep(seconds*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
           }
		
	}
	
}