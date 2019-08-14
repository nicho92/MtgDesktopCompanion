package org.magic.api.pool.impl;

import java.sql.Connection;
import java.sql.SQLException;

import org.magic.api.interfaces.abstracts.AbstractPool;
import org.magic.services.ThreadManager;

import com.zaxxer.hikari.HikariDataSource;

public class HikariPool extends AbstractPool {

	private HikariDataSource datasource;
	
	
	@Override
	public Connection getConnection() throws SQLException {
		return datasource.getConnection();
	}

	@Override
	public void close() throws SQLException {
		datasource.close();
		
	}

	@Override
	public void init(String url, String user, String pass, boolean enable) {
		datasource = new HikariDataSource();
		
		datasource.setJdbcUrl(url);
		datasource.setUsername(user);
		datasource.setPassword(pass);
		datasource.setThreadFactory(ThreadManager.getInstance().getFactory());
		
		
		datasource.setMaximumPoolSize(getInt("POOL_MAX_SIZE"));
		datasource.setMinimumIdle(getInt("POOL_MIN_IDLE"));
		datasource.addDataSourceProperty("cachePrepStmts", getBoolean("POOL_PREPARED_STATEMENT"));
		
		datasource.setRegisterMbeans(true);
		datasource.setPoolName(getName());
		datasource.addDataSourceProperty("prepStmtCacheSize", getString("STMT_CACHE_SIZE"));
		datasource.addDataSourceProperty("prepStmtCacheSqlLimit", getString("STMT_CACHE_LIMIT"));
		
		
		  if(!enable) {
			  datasource.setMinimumIdle(1);
	          datasource.setMaximumPoolSize(1);
		  }
		
	}

	@Override
	public String getName() {
		return "Hikari";
	}

	
	@Override
	public String getVersion() {
		return "3.3.1";
	}
	
	@Override
	public void initDefault() {
		super.initDefault();
		setProperty("STMT_CACHE_SIZE", "250");
		setProperty("STMT_CACHE_LIMIT", "2048");
		
	}
	

}
