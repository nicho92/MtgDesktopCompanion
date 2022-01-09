package org.magic.api.beans.audit;

import java.io.Serializable;
import java.time.Instant;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ThreadInfo implements Serializable {

	/**
	 * 
	 */
	
	public enum STATE {NEW,STARTED,CANCELED,FINISHED}
	public enum TYPE {WORKER,RUNNABLE}
	
	private static final long serialVersionUID = 1L;
	private Instant createdDate;
	private Instant startDate;
	private Instant endDate;
	private long duration;
	private String name;
	
	public JsonElement toJson()
	{
		var obj = new JsonObject();
		obj.addProperty("name", getName());
		obj.addProperty("status", getStatus().name());
		obj.addProperty("type", getType().name());
		obj.addProperty("created", getCreatedDate().toEpochMilli());
		obj.addProperty("start",getStartDate().toEpochMilli());
		obj.addProperty("end", getEndDate().toEpochMilli());
		obj.addProperty("durationInMillis", getDuration());
		return obj;
	}
	
	
	public Instant getCreatedDate() {
		return createdDate;
	}


	public void setCreatedDate(Instant createdDate) {
		this.createdDate = createdDate;
	}


	public Instant getStartDate() {
		return startDate;
	}


	public void setStartDate(Instant startDate) {
		this.startDate = startDate;
	}


	public Instant getEndDate() {
		return endDate;
	}


	public void setEndDate(Instant endDate) {
		this.endDate = endDate;
	}

	private STATE status = STATE.NEW;
	private TYPE type=TYPE.RUNNABLE;
	private transient Runnable runnable;
	
	
	public ThreadInfo(Runnable r) {
		this.runnable=r;
		createdDate = Instant.now();
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

	public void setRunnable(Runnable runnable) {
		this.runnable=runnable;
	}
	
	public Runnable getRunnable() {
		return runnable;
	}
	
	
}