package org.magic.api.beans.technical.audit;

import java.time.Instant;

public class TaskInfo extends AbstractAuditableItem {

	public enum STATE {NEW,STARTED,CANCELED,FINISHED}
	public enum TYPE {WORKER,RUNNABLE}
	
	private static final long serialVersionUID = 1L;
	private Instant created;
	private String name;
	private STATE status = STATE.NEW;
	private TYPE type=TYPE.RUNNABLE;
	private transient Runnable runnable;
	
	

	
	public Instant getCreated() {
		return created;
	}


	public void setCreated(Instant createdDate) {
		this.created = createdDate;
	}

	public TaskInfo(Runnable r) {
		this.runnable=r;
		created = Instant.now();
	}
	
	
	public void setStatus(STATE status) {
		this.status = status;
	}
	
	public STATE getStatus() {
		return status;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public TYPE getType() {
		return type;
	}
	public void setType(TYPE type) {
		this.type = type;
	}

	public void setRunnable(Runnable runnable) {
		this.runnable=runnable;
	}
	
	public Runnable getRunnable() {
		return runnable;
	}
	
	
}
