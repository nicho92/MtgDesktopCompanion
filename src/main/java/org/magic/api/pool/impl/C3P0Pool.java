package org.magic.api.pool.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.magic.api.interfaces.abstracts.AbstractPoolProvider;

import com.mchange.v2.c3p0.ComboPooledDataSource;


public class C3P0Pool extends AbstractPoolProvider {

	private ComboPooledDataSource datasource;
	
	
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
		datasource = new ComboPooledDataSource();
		datasource.setProperties(props);
		datasource.setUser(user);
		datasource.setPassword(pass);
		datasource.setJdbcUrl(url);
		
	}
	
	@Override
	public String getName() {
		return "C3P0";
	}
	
	@Override
	public String getVersion() {
		return ComboPooledDataSource.class.getPackage().getImplementationVersion();
	}
	
	
	@Override
	public Map<String, String> getDefaultAttributes() {
		var map = new HashMap<String,String>();
		
		map.put("maxConnectionAge","0");
		map.put("idleConnectionTestPeriod","0");
		map.put("initialPoolSize","3");
		map.put("statementCacheNumCheckedOutStatementsAllUsers","0");
		map.put("privilegeSpawnedThreads",FALSE);
		map.put("debugUnreturnedConnectionStackTraces",FALSE);
		map.put("maxStatements","0");
		map.put("breakAfterAcquireFailure",FALSE);
		map.put("maxIdleTime","60");
		map.put("minPoolSize","3");
		map.put("threadPoolSize","3");
		map.put("maxPoolSize","15");
		map.put("maxStatementsPerConnection","0");
		map.put("forceSynchronousCheckins",FALSE);
		map.put("forceIgnoreUnresolvedTransactions",FALSE);
		map.put("lastCheckinFailureDefaultUser","null");
		map.put("numIdleConnectionsAllUsers","2");
		map.put("threadPoolNumIdleThreads","3");
		map.put("maxIdleTimeExcessConnections","0");
		map.put("preferredTestQuery","");
		map.put("testConnectionOnCheckout",FALSE);
		map.put("connectionTesterClassName","com.mchange.v2.c3p0.impl.DefaultConnectionTester");
		map.put("testConnectionOnCheckin",FALSE);
		map.put("forceUseNamedDriverClass",FALSE);
		map.put("statementDestroyerNumConnectionsInUseAllUsers","-1");
		map.put("usesTraditionalReflectiveProxies",FALSE);
		map.put("acquireRetryDelay","1000");
		map.put("checkoutTimeout","0");
		map.put("statementCacheNumStatementsAllUsers","0");
		map.put("loginTimeout","0");
		map.put("threadPoolNumTasksPending","0");
		map.put("unreturnedConnectionTimeout","0");
		map.put("autoCommitOnClose",FALSE);
		map.put("effectivePropertyCycleDefaultUser","0.0");
		map.put("acquireRetryAttempts","30");
		map.put("maxAdministrativeTaskTime","0");
		map.put("overrideDefaultPassword","null");
		map.put("statementCacheNumDeferredCloseThreads","0");
		
		return map;
	}

}
