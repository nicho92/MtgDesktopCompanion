package org.magic.services.threads;

import java.time.Instant;

import org.magic.api.beans.audit.TaskInfo;
import org.magic.api.beans.audit.TaskInfo.STATE;
import org.magic.tools.Chrono;

public abstract class MTGRunnable implements Runnable{
	
	private TaskInfo info;
	
	protected MTGRunnable() {
		info = new TaskInfo(this);
	}
	
	
	public TaskInfo getInfo() {
		return info;
	}
	
	public void start() {
		info.setStart(Instant.now());
		info.setStatus(STATE.STARTED);
	}
	
	public void end() {
		info.setEnd(Instant.now());
		info.setStatus(STATE.FINISHED);
	
	}
	
	
	protected abstract void auditedRun();
	
	
	@Override
	public void run() {
		start();
		auditedRun();
		end();
		
	}

}
