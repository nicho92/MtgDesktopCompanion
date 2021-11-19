package org.magic.services.threads;

import java.util.Date;

import org.magic.services.threads.ThreadInfo.STATE;
import org.magic.tools.Chrono;

public abstract class MTGRunnable implements Runnable{
	
	private ThreadInfo info;
	private Chrono chrono;
	
	protected MTGRunnable() {
		info = new ThreadInfo(this);
		chrono= new Chrono();
	}
	
	
	public ThreadInfo getInfo() {
		return info;
	}
	
	public void start() {
		info.setStartDate(new Date());
		info.setStatus(STATE.STARTED);
		chrono.start();
	}
	
	public void end() {
		info.setEndDate(new Date());
		info.setStatus(STATE.FINISHED);
		info.setDuration(chrono.stop());
	}
	
	
	protected abstract void auditedRun();
	
	
	@Override
	public void run() {
		start();
		auditedRun();
		end();
		
	}

}
