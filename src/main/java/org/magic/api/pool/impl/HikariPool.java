package org.magic.api.pool.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

import org.magic.api.beans.MTGDocumentation;
import org.magic.api.beans.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.interfaces.abstracts.AbstractPool;
import org.magic.services.threads.ThreadManager;
import org.magic.tools.POMReader;

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
	public MTGDocumentation getDocumentation() {
		try {
			return new MTGDocumentation(new URL("https://raw.githubusercontent.com/brettwooldridge/HikariCP/dev/README.md"),FORMAT_NOTIFICATION.MARKDOWN);
		} catch (MalformedURLException e) {
			return super.getDocumentation();
		}
	}
	
	
	@Override
	public void init(String url, String user, String pass, boolean enable) {
		var c = new HikariConfig(props);
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
		return POMReader.readVersionFromPom(com.zaxxer.hikari.pool.HikariPool.class, "/META-INF/maven/com.zaxxer/HikariCP/pom.properties");
	}
	
	@Override
	public void initDefault() {
		setProperty("autoCommit", TRUE);
		setProperty("connectionTimeout", "30000");
		setProperty("poolName", "mtg-hikari-pool");
		setProperty("initializationFailTimeout", "1");
		setProperty("readOnly", FALSE);
		setProperty("registerMbeans", TRUE);
		setProperty("minimumIdle", "1");
		setProperty("isolateInternalQueries", FALSE);
		setProperty("leakDetectionThreshold", "0");
		setProperty("validationTimeout", "5000");
		setProperty("maxLifetime", "1800000");
		setProperty("allowPoolSuspension", FALSE);
		setProperty("connectionTestQuery", "");
		setProperty("idleTimeout", "600000");
		setProperty("maximumPoolSize", "10");
		setProperty("dataSource.prepStmtCacheSqlLimit","2048");
		setProperty("dataSource.cachePrepStmts",TRUE);
		setProperty("dataSource.allowMultiQueries",TRUE);
		setProperty("dataSource.prepStmtCacheSize","250");
		setProperty("dataSource.useServerPrepStmts",TRUE);
		setProperty("dataSource.useLocalSessionState",TRUE);
		setProperty("connectionInitSql", "");
		setProperty("datasource.keepaliveTime","0");
		setProperty("dataSource.cacheCallableStmts",TRUE);
		setProperty("dataSource.cacheServerConfiguration",TRUE);

		
	}
	

}
