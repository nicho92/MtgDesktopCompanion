package org.magic.api.beans.audit;

public class DAOInfo extends AbstractAuditableItem{

	private static final long serialVersionUID = 1L;
	private String query;
	private String canonicalName;
	private String message;
	private String connectionName;
	private String daoName;
	
	public String getConnectionName() {
		return connectionName;
	}
	
	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}
	
	public void setQuery(String sql) {
		this.query = sql;
	}
	
	public String getQuery() {
		return query;
	}
	
	public void setDaoName(String daoName) {
		this.daoName = daoName;
	}
	
	public String getDaoName() {
		return daoName;
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
