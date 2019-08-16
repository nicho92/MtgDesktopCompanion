package org.magic.api.pool.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

import org.magic.api.interfaces.abstracts.AbstractPool;
import org.magic.services.ThreadManager;

import com.zaxxer.hikari.HikariConfig;
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
	public URL getDocumentation() {
		try {
			return new URL("https://raw.githubusercontent.com/brettwooldridge/HikariCP/dev/README.md");
		} catch (MalformedURLException e) {
			return super.getDocumentation();
		}
	}
	
	
	@Override
	public void init(String url, String user, String pass, boolean enable) {
		HikariConfig c = new HikariConfig(props);
					 c.setJdbcUrl(url);
					 c.setUsername(user);
					 c.setPassword(pass);
					 c.setThreadFactory(ThreadManager.getInstance().getFactory());
		datasource = new HikariDataSource(c);
		
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
		setProperty("validationTimeout", "5000");
		setProperty("connectionInitSql", "");
		setProperty("minimumIdle", "1");
		setProperty("autoCommit", "true");
		setProperty("connectionTimeout", "30000");
		setProperty("poolName", "hikari-pool");
		setProperty("initializationFailTimeout", "1");
		setProperty("readOnly", "false");
		setProperty("registerMbeans", "true");
		setProperty("isolateInternalQueries", "false");
		setProperty("leakDetectionThreshold", "0");
		setProperty("maxLifetime", "1800000");
		setProperty("allowPoolSuspension", "false");
		setProperty("connectionTestQuery", "");
		setProperty("idleTimeout", "600000");
		setProperty("maximumPoolSize", "10");
		setProperty("dataSource.prepStmtCacheSqlLimit","2048");
		setProperty("dataSource.cachePrepStmts","true");
		setProperty("dataSource.allowMultiQueries","true");
		setProperty("dataSource.prepStmtCacheSize","250");
		setProperty("dataSource.useServerPrepStmts","true");
		setProperty("dataSource.useLocalSessionState","true");
		
	}
	

}
