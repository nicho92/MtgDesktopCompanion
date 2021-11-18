package org.magic.services.threads;

import java.io.Serializable;
import java.util.Date;

public class ThreadInfo implements Serializable {

	/**
	 * 
	 */
	
	public enum STATE {NEW,STARTED,CANCELED,FINISHED}
	public enum TYPE {WORKER,RUNNABLE}
	
	private static final long serialVersionUID = 1L;
	private Date startDate;
	private Date endDate;
	private long duration;
	private String name;
	private STATE status = STATE.NEW;
	private TYPE type=TYPE.RUNNABLE;
	
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
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public TYPE getType() {
		return type;
	}
	public void setType(TYPE type) {
		this.type = type;
	}
	
	
}
