package org.magic.api.beans;

import java.io.Serializable;
import java.time.Instant;

import org.magic.api.interfaces.MTGDao;

import com.google.gson.JsonObject;

public class DAOInfo implements Serializable {

	
	private static final long serialVersionUID = 1L;
	private String query;
	private Instant creationDate;
	private Instant endDate;
	private long duration;
	private transient MTGDao dao;
	private String canonicalName;
	private String message;
	
	
	public MTGDao getDao() {
		return dao;
	}

	public void setDao(MTGDao dao) {
		this.dao = dao;
	}

	public DAOInfo() {
		creationDate= Instant.now();
	}
	
	public void setQuery(String sql) {
		this.query = sql;
	}
	
	public String getQuery() {
		return query;
	}
	
	public Instant getCreationDate() {
		return creationDate;
	}

	public Instant getEndDate() {
		return endDate;
	}

	public void setEndDate(Instant endDate) {
		this.endDate = endDate;
		setDuration(getEndDate().toEpochMilli()-getCreationDate().toEpochMilli());
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public void setCreationDate(Instant creationDate) {
		this.creationDate = creationDate;
	}

	public JsonObject toJson() {
		var obj = new JsonObject();
		obj.addProperty("creationDate",getCreationDate().toEpochMilli());
		
		if(getEndDate()!=null)
			obj.addProperty("endDate",getEndDate().toEpochMilli());
		else
			obj.addProperty("endDate","");
		
		obj.addProperty("duration",getDuration());
		obj.addProperty("statement", getClasseName());
		obj.addProperty("sql",getQuery());
		return obj;
	
	}

	public void setClasseName(String canonicalName) {
		this.canonicalName=canonicalName;
	}
	
	public String getClasseName() {
		return canonicalName;
	}

	public void setMessage(String message) {
		this.message=message;
	}
	
	public String getMessage() {
		return message;
	}
	
	
}
