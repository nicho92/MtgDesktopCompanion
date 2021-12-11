package org.magic.api.beans;

import java.io.Serializable;
import java.time.Instant;

import org.magic.api.interfaces.MTGDao;

import com.google.gson.JsonObject;

public class DAOInfo implements Serializable {

	
	private static final long serialVersionUID = 1L;
	private String sql;
	private Instant creationDate;
	private Instant endDate;
	private long duration;
	private transient MTGDao dao;
	private String canonicalName;
	
	
	public MTGDao getDao() {
		return dao;
	}

	public void setDao(MTGDao dao) {
		this.dao = dao;
	}

	public DAOInfo() {
		creationDate= Instant.now();
	}
	
	public void setSql(String sql) {
		this.sql = sql;
	}
	
	public String getSql() {
		return sql;
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
		obj.addProperty("statement", getStatementClass());
		obj.addProperty("sql",getSql());
		return obj;
	
	}

	public void setStatementClass(String canonicalName) {
		this.canonicalName=canonicalName;
	}
	
	public String getStatementClass() {
		return canonicalName;
	}
	
	
}
