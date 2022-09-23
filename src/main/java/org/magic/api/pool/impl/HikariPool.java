package org.magic.api.pool.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.magic.api.beans.MTGDocumentation;
import org.magic.api.beans.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.interfaces.abstracts.AbstractPoolProvider;
import org.magic.services.threads.ThreadManager;
import org.magic.tools.POMReader;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HikariPool extends AbstractPoolProvider {

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
	public Map<String, String> getDefaultAttributes() {

		var map = new HashMap<String,String>();

		map.put("autoCommit", TRUE);
		map.put("connectionTimeout", "30000");
		map.put("poolName", "mtg-hikari-pool");
		map.put("initializationFailTimeout", "1");
		map.put("readOnly", FALSE);
		map.put("registerMbeans", TRUE);
		map.put("minimumIdle", "1");
		map.put("isolateInternalQueries", FALSE);
		map.put("leakDetectionThreshold", "0");
		map.put("validationTimeout", "5000");
		map.put("maxLifetime", "1800000");
		map.put("allowPoolSuspension", FALSE);
		map.put("connectionTestQuery", "");
		map.put("idleTimeout", "600000");
		map.put("maximumPoolSize", "10");
		map.put("dataSource.prepStmtCacheSqlLimit","2048");
		map.put("dataSource.cachePrepStmts",TRUE);
		map.put("dataSource.allowMultiQueries",TRUE);
		map.put("dataSource.prepStmtCacheSize","250");
		map.put("dataSource.useServerPrepStmts",TRUE);
		map.put("dataSource.useLocalSessionState",TRUE);
		map.put("connectionInitSql", "");
		map.put("dataSource.cacheCallableStmts",TRUE);
		map.put("dataSource.cacheServerConfiguration",TRUE);
		return map;

	}


}
