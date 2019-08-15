package org.magic.api.pool.impl;

import java.sql.Connection;
import java.sql.SQLException;

import org.magic.api.interfaces.abstracts.AbstractPool;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class C3P0Pool extends AbstractPool {

	
	private static final String FALSE = "false";
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
		return "0.9.5.4";
	}
	
	@Override
	public void initDefault() {
		setProperty("maxConnectionAge","0");
		setProperty("idleConnectionTestPeriod","0");
		setProperty("initialPoolSize","3");
		setProperty("statementCacheNumCheckedOutStatementsAllUsers","0");
		setProperty("privilegeSpawnedThreads",FALSE);
		setProperty("debugUnreturnedConnectionStackTraces",FALSE);
		setProperty("maxStatements","0");
		setProperty("breakAfterAcquireFailure",FALSE);
		setProperty("maxIdleTime","0");
		setProperty("minPoolSize","3");
		setProperty("threadPoolSize","3");
		setProperty("maxPoolSize","15");
		setProperty("maxStatementsPerConnection","0");
		setProperty("forceSynchronousCheckins",FALSE);
		setProperty("forceIgnoreUnresolvedTransactions",FALSE);
		setProperty("lastCheckinFailureDefaultUser","null");
		setProperty("numIdleConnectionsAllUsers","2");
		setProperty("threadPoolNumIdleThreads","3");
		setProperty("maxIdleTimeExcessConnections","0");
		setProperty("preferredTestQuery","");
		setProperty("testConnectionOnCheckout",FALSE);
		setProperty("connectionTesterClassName","com.mchange.v2.c3p0.impl.DefaultConnectionTester");
		setProperty("testConnectionOnCheckin",FALSE);
		setProperty("forceUseNamedDriverClass",FALSE);
		setProperty("statementDestroyerNumConnectionsInUseAllUsers","-1");
		setProperty("usesTraditionalReflectiveProxies",FALSE);
		setProperty("acquireRetryDelay","1000");
		setProperty("checkoutTimeout","0");
		setProperty("statementCacheNumStatementsAllUsers","0");
		setProperty("loginTimeout","0");
		setProperty("threadPoolNumTasksPending","0");
		setProperty("unreturnedConnectionTimeout","0");
		setProperty("autoCommitOnClose",FALSE);
		setProperty("effectivePropertyCycleDefaultUser","0.0");
		setProperty("acquireRetryAttempts","30");
		setProperty("maxAdministrativeTaskTime","0");
		setProperty("overrideDefaultPassword","null");
		setProperty("statementCacheNumDeferredCloseThreads","0");
	}

}
