package org.magic.api.beans.audit;

import java.time.Instant;

import com.google.gson.JsonObject;

public class TaskInfo extends AbstractAuditableItem {

	public enum STATE {NEW,STARTED,CANCELED,FINISHED}
	public enum TYPE {WORKER,RUNNABLE}
	
	private static final long serialVersionUID = 1L;
	private Instant createdDate;
	private String name;
	
	@Override
	public JsonObject toJson()
	{
		var obj = new JsonObject();
		obj.addProperty("name", getName());
		obj.addProperty("status", getStatus().name());
		obj.addProperty("type", getType().name());
		obj.addProperty("created", getCreatedDate().toEpochMilli());
		obj.addProperty("start",getStart().toEpochMilli());
		obj.addProperty("end", getEnd().toEpochMilli());
		obj.addProperty("durationInMillis", getDuration());
		return obj;
	}
	
	
	public Instant getCreatedDate() {
		return createdDate;
	}


	public void setCreatedDate(Instant createdDate) {
		this.createdDate = createdDate;
	}

	private STATE status = STATE.NEW;
	private TYPE type=TYPE.RUNNABLE;
	private transient Runnable runnable;
	
	
	public TaskInfo(Runnable r) {
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
