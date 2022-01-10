package org.magic.services.threads;

import java.time.Instant;

import org.magic.api.beans.audit.TaskInfo;
import org.magic.api.beans.audit.TaskInfo.STATE;
import org.magic.tools.Chrono;

public abstract class MTGRunnable implements Runnable{
	
	private TaskInfo info;
	private Chrono chrono;
	
	protected MTGRunnable() {
		info = new TaskInfo(this);
		chrono= new Chrono();
	}
	
	
	public TaskInfo getInfo() {
		return info;
	}
	
	public void start() {
		info.setStart(Instant.now());
		info.setStatus(STATE.STARTED);
		chrono.start();
	}
	
	public void end() {
		info.setEnd(Instant.now());
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
