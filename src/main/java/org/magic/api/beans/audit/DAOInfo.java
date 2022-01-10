package org.magic.api.beans.audit;

import org.magic.api.interfaces.MTGDao;

import com.google.gson.JsonObject;

public class DAOInfo extends AbstractAuditableItem{

	private static final long serialVersionUID = 1L;
	private String query;
	private transient MTGDao dao;
	private String canonicalName;
	private String message;
	private String connectionName;
	
	
	public String getConnectionName() {
		return connectionName;
	}
	
	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}
	
	public MTGDao getDao() {
		return dao;
	}

	public void setDao(MTGDao dao) {
		this.dao = dao;
	}
	
	public void setQuery(String sql) {
		this.query = sql;
	}
	
	public String getQuery() {
		return query;
	}

	public long getDuration() {
		return duration;
	}

	@Override
	public JsonObject toJson() {
		var obj = new JsonObject();
		obj.addProperty("creationDate",getStart().toEpochMilli());
		
		if(getEnd()!=null)
			obj.addProperty("endDate",getEnd().toEpochMilli());
		else
			obj.addProperty("endDate","");
		
		obj.addProperty("duration",getDuration());
		obj.addProperty("statement", getClasseName());
		obj.addProperty("sql",getQuery());
		obj.addProperty("connection", getConnectionName());
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
