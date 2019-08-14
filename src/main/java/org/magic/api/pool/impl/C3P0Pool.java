package org.magic.api.pool.impl;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.magic.api.interfaces.abstracts.AbstractPool;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class C3P0Pool extends AbstractPool {

	
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
		setProperty("statementCacheNumConnectionsWithCachedStatementsDefaultUser","0");
		setProperty("initialPoolSize","3");
		setProperty("statementCacheNumCheckedOutStatementsAllUsers","0");
		setProperty("privilegeSpawnedThreads","false");
		setProperty("statementCacheNumConnectionsWithCachedStatementsAllUsers","0");
		setProperty("debugUnreturnedConnectionStackTraces","false");
		setProperty("maxStatements","0");
		setProperty("breakAfterAcquireFailure","false");
		setProperty("maxIdleTime","0");
		setProperty("minPoolSize","3");
		setProperty("threadPoolSize","3");
		setProperty("maxPoolSize","15");
		setProperty("numBusyConnectionsDefaultUser","1");
		setProperty("maxStatementsPerConnection","0");
		setProperty("statementDestroyerNumDeferredDestroyStatementsAllUsers","-1");
		setProperty("statementDestroyerNumTasksPending","-1");
		setProperty("statementDestroyerNumConnectionsWithDeferredDestroyStatementsDefaultUser","-1");
		setProperty("numUnclosedOrphanedConnectionsAllUsers","0");
		setProperty("statementCacheNumCheckedOutDefaultUser","0");
		setProperty("forceSynchronousCheckins","false");
		setProperty("forceIgnoreUnresolvedTransactions","false");
		setProperty("factoryClassLocation","null");
		setProperty("lastCheckinFailureDefaultUser","null");
		setProperty("numIdleConnectionsAllUsers","2");
		setProperty("threadPoolNumIdleThreads","3");
		setProperty("maxIdleTimeExcessConnections","0");
		setProperty("preferredTestQuery","null");
		setProperty("testConnectionOnCheckout","false");
		setProperty("connectionTesterClassName","com.mchange.v2.c3p0.impl.DefaultConnectionTester");
		setProperty("testConnectionOnCheckin","false");
		setProperty("numFailedCheckinsDefaultUser","0");
		setProperty("numBusyConnectionsAllUsers","1");
		setProperty("numFailedCheckoutsDefaultUser","0");
		setProperty("lastAcquisitionFailureDefaultUser","null");
		setProperty("statementDestroyerNumThreads","-1");
		setProperty("numUnclosedOrphanedConnectionsDefaultUser","0");
		setProperty("forceUseNamedDriverClass","false");
		setProperty("upTimeMillisDefaultUser","108");
		setProperty("numUnclosedOrphanedConnections","0");
		setProperty("numHelperThreads","3");
		setProperty("numConnectionsDefaultUser","3");
		setProperty("statementDestroyerNumConnectionsInUseAllUsers","-1");
		setProperty("usesTraditionalReflectiveProxies","false");
		setProperty("statementDestroyerNumConnectionsInUseDefaultUser","-1");
		setProperty("statementDestroyerNumIdleThreads","-1");
		setProperty("statementDestroyerNumActiveThreads","-1");
		setProperty("statementDestroyerNumConnectionsWithDeferredDestroyStatementsAllUsers","-1");
		setProperty("statementCacheNumStatementsDefaultUser","0");
		setProperty("numConnectionsAllUsers","3");
		setProperty("acquireRetryDelay","1000");
		setProperty("checkoutTimeout","0");
		setProperty("statementCacheNumStatementsAllUsers","0");
		setProperty("numFailedIdleTestsDefaultUser","0");
		setProperty("lastConnectionTestFailureDefaultUser","null");
		setProperty("loginTimeout","0");
		setProperty("lastIdleTestFailureDefaultUser","null");
		setProperty("threadPoolNumTasksPending","0");
		setProperty("unreturnedConnectionTimeout","0");
		setProperty("autoCommitOnClose","false");
		setProperty("lastCheckoutFailureDefaultUser","null");
		setProperty("statementDestroyerNumDeferredDestroyStatementsDefaultUser","-1");
		setProperty("numConnections","3");
		setProperty("effectivePropertyCycleDefaultUser","0.0");
		setProperty("acquireRetryAttempts","30");
		setProperty("maxAdministrativeTaskTime","0");
		setProperty("overrideDefaultPassword","null");
		setProperty("statementCacheNumDeferredCloseThreads","0");
	}

}
