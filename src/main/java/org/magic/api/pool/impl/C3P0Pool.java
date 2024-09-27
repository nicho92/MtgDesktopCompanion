package org.magic.api.pool.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractPoolProvider;

import com.mchange.v2.c3p0.AbstractComboPooledDataSource;
import com.mchange.v2.c3p0.ComboPooledDataSource;


public class C3P0Pool extends AbstractPoolProvider {

	private AbstractComboPooledDataSource datasource;


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
	public Map<String, MTGProperty> getDefaultAttributes() {
		var map = new HashMap<String,MTGProperty>();

		map.put("maxConnectionAge",MTGProperty.newIntegerProperty("0", "Seconds, effectively a time to live. A Connection older than maxConnectionAge will be destroyed and purged from the pool. This differs from maxIdleTime in that it refers to absolute age. Even a Connection which has not been much idle will be purged from the pool if it exceeds maxConnectionAge. Zero means no maximum absolute age is enforced.", 0, -1));
		map.put("idleConnectionTestPeriod",MTGProperty.newIntegerProperty("0","If this is a number greater than 0, c3p0 will test all idle, pooled but unchecked-out connections, every this number of seconds",0,-1));
		map.put("initialPoolSize",MTGProperty.newIntegerProperty("3","Number of Connections a pool will try to acquire upon startup. Should be between minPoolSize and maxPoolSize.",0,-1));
		map.put("privilegeSpawnedThreads",MTGProperty.newBooleanProperty(FALSE, "If true, c3p0-spawned Threads will have the java.security.AccessControlContext associated with c3p0 library classes. By default, c3p0-spawned Threads (helper threads, java.util.Timer threads) inherit their AccessControlContext from the client Thread that provokes initialization of the pool. This can sometimes be a problem, especially in application servers that support hot redeployment of client apps. If c3p0's Threads hold a reference to an AccessControlContext from the first client that hits them, it may be impossible to garbage collect a ClassLoader associated with that client when it is undeployed in a running VM. Also, it is possible client Threads might lack sufficient permission to perform operations that c3p0 requires. Setting this to true can resolve these issues."));
		map.put("debugUnreturnedConnectionStackTraces",MTGProperty.newBooleanProperty(FALSE, "If true, and if unreturnedConnectionTimeout is set to a positive value, then the pool will capture the stack trace (via an Exception) of all Connection checkouts, and the stack traces will be printed when unreturned checked-out Connections timeout. This is intended to debug applications with Connection leaks, that is applications that occasionally fail to return Connections, leading to pool growth, and eventually exhaustion (when the pool hits maxPoolSize with all Connections checked-out and lost). This parameter should only be set while debugging, as capturing the stack trace will slow down every Connection check-out"));
		map.put("breakAfterAcquireFailure",MTGProperty.newBooleanProperty(FALSE, "If true, a pooled DataSource will declare itself broken and be permanently closed if a Connection cannot be obtained from the database after making acquireRetryAttempts to acquire one. If false, failure to obtain a Connection will cause all Threads waiting for the pool to acquire a Connection to throw an Exception, but the DataSource will remain valid, and will attempt to acquire again following a call to getConnection()"));
		map.put("forceSynchronousCheckins",MTGProperty.newBooleanProperty(FALSE, "Setting this to true forces Connections to be checked-in synchronously, which under some circumstances may improve performance. Ordinarily Connections are checked-in asynchronously so that clients avoid any overhead of testing or custom check-in logic. However, asynchronous check-in contributes to thread pool congestion, and very busy pools might find clients delayed waiting for check-ins to complete. Expanding numHelperThreads can help manage Thread pool congestion, but memory footprint and switching costs put limits on practical thread pool size. To reduce thread pool load, you can set forceSynchronousCheckins to true"));
		map.put("testConnectionOnCheckout",MTGProperty.newBooleanProperty(FALSE, "If true, an operation will be performed at every connection checkout to verify that the connection is valid. Be sure to set an efficient preferredTestQuery or automaticTestTable if you set this to true. Performing the (expensive) default Connection test on every client checkout will harm client performance. Testing Connections in checkout is the simplest and most reliable form of Connection testing, but for better performance, consider verifying connections periodically using idleConnectionTestPeriod."));
		map.put("testConnectionOnCheckin",MTGProperty.newBooleanProperty(FALSE, "If true, an operation will be performed asynchronously at every connection checkin to verify that the connection is valid. Use in combination with idleConnectionTestPeriod for quite reliable, always asynchronous Connection testing. Also, setting an automaticTestTable or preferredTestQuery will usually speed up all connection tests. "));
		map.put("forceUseNamedDriverClass",MTGProperty.newBooleanProperty(FALSE,"Setting the parameter driverClass causes that class to preload and register with java.sql.DriverManager. However, it does not on its own ensure that the driver used will be an instance of driverClass, as DriverManager may (in unusual cases) know of other driver classes which can handle the specified jdbcUrl. Setting this parameter to true causes c3p0 to ignore DriverManager and simply instantiate driverClass directly."));
		map.put("autoCommitOnClose",MTGProperty.newBooleanProperty(FALSE,"he JDBC spec is unforgivably silent on what should happen to unresolved, pending transactions on Connection close. C3P0's default policy is to rollback any uncommitted, pending work. (I think this is absolutely, undeniably the right policy, but there is no consensus among JDBC driver vendors.) Setting autoCommitOnClose to true causes uncommitted pending work to be committed, rather than rolled back on Connection close"));
		map.put("overrideDefaultPassword",new MTGProperty("null","Forces the password that should by PooledDataSources when a user calls the default getConnection() method. This is primarily useful when applications are pooling Connections from a non-c3p0 unpooled DataSource. Applications that use ComboPooledDataSource, or that wrap any c3p0-implemented unpooled DataSource can use the simple password property"));
		map.put("maxStatements",MTGProperty.newIntegerProperty("0","The size of c3p0's global PreparedStatement cache. If both maxStatements and maxStatementsPerConnection are zero, statement caching will not be enabled. If maxStatements is zero but maxStatementsPerConnection is a non-zero value, statement caching will be enabled, but no global limit will be enforced, only the per-connection maximum. maxStatements controls the total number of Statements cached, for all Connections. If set, it should be a fairly large number, as each pooled Connection requires its own, distinct flock of cached statements. As a guide, consider how many distinct PreparedStatements are used frequently in your application, and multiply that number by maxPoolSize to arrive at an appropriate value. Though maxStatements is the JDBC standard parameter for controlling statement caching, users may find c3p0's alternative maxStatementsPerConnection more intuitive to use",0,-1));
		map.put("maxIdleTime",MTGProperty.newIntegerProperty("60","Seconds a Connection can remain pooled but unused before being discarded. Zero means idle connections never expire. ",0,-1));
		map.put("minPoolSize",MTGProperty.newIntegerProperty("3","Minimum number of Connections a pool will maintain at any given time.",1,-1));
		map.put("maxPoolSize",MTGProperty.newIntegerProperty("15","Maximum number of Connections a pool will maintain at any given time.",1,-1));
		map.put("maxStatementsPerConnection",MTGProperty.newIntegerProperty("0","The number of PreparedStatements c3p0 will cache for a single pooled Connection. If both maxStatements and maxStatementsPerConnection are zero, statement caching will not be enabled. If maxStatementsPerConnection is zero but maxStatements is a non-zero value, statement caching will be enabled, and a global limit enforced, but otherwise no limit will be set on the number of cached statements for a single Connection. If set, maxStatementsPerConnection should be set to about the number distinct PreparedStatements that are used frequently in your application, plus two or three extra so infrequently statements don't force the more common cached statements to be culled. Though maxStatements is the JDBC standard parameter for controlling statement caching, users may find maxStatementsPerConnection more intuitive to use",0,-1));
		map.put("maxIdleTimeExcessConnections",MTGProperty.newIntegerProperty("0","Number of seconds that Connections in excess of minPoolSize should be permitted to remain idle in the pool before being culled. Intended for applications that wish to aggressively minimize the number of open Connections, shrinking the pool back towards minPoolSize if, following a spike, the load level diminishes and Connections acquired are no longer needed. If maxIdleTime is set, maxIdleTimeExcessConnections should be smaller if the parameter is to have any effect. Zero means no enforcement, excess Connections are not idled out.",0,-1));
		map.put("preferredTestQuery",new MTGProperty("","Defines the query that will be executed for all connection tests.His is rarely useful, and should be left as null except when using very old (pre Java 6) or broken JDBC drivers."));
		map.put("acquireRetryDelay",MTGProperty.newIntegerProperty("1000","Milliseconds, time c3p0 will wait between acquire attempts. ",1000,-1));
		map.put("checkoutTimeout",MTGProperty.newIntegerProperty("0","The number of milliseconds a client calling getConnection() will wait for a Connection to be checked-in or acquired when the pool is exhausted. Zero means wait indefinitely. Setting any positive value will cause the getConnection() call to time-out and break with an SQLException after the specified number of milliseconds",0,-1));
		map.put("unreturnedConnectionTimeout",MTGProperty.newIntegerProperty("0","Seconds. If set, if an application checks out but then fails to check-in [i.e. close()] a Connection within the specified period of time, the pool will unceremoniously destroy() the Connection. This permits applications with occasional Connection leaks to survive, rather than eventually exhausting the Connection pool. And that's a shame. Zero means no timeout, applications are expected to close() their own Connections. Obviously, if a non-zero value is set, it should be to a value longer than any Connection should reasonably be checked-out. Otherwise, the pool will occasionally kill Connections in active use, which is bad.",0,-1));
		map.put("acquireRetryAttempts",MTGProperty.newIntegerProperty("30","Defines how many times c3p0 will retry after a failure to acquire a new Connection from the database before giving up. If this value is less than zero, c3p0 will keep trying to fetch a Connection indefinitely.",0,-1));
		map.put("maxAdministrativeTaskTime", MTGProperty.newIntegerProperty("0","Seconds before c3p0's thread pool will try to interrupt an apparently hung task. Rarely useful. ",0,-1));
		map.put("statementCacheNumDeferredCloseThreads",MTGProperty.newIntegerProperty("0","If set to a value greater than 0, the statement cache will track when Connections are in use, and only destroy Statements when their parent Connections are not otherwise in use. Although closing of a Statement while the parent Connection is in use is formally within spec, some databases and/or JDBC drivers, most notably Oracle, do not handle the case well and freeze, leading to deadlocks. Setting this parameter to a positive value should eliminate the issue. This parameter should only be set if you observe that attempts by c3p0 to close() cached statements freeze (usually you'll see APPARENT DEADLOCKS in your logs). If set, this parameter should almost always be set to 1. Basically, if you need more than one Thread dedicated solely to destroying cached Statements, you should set maxStatements and/or maxStatementsPerConnection so that you don't churn through Statements so quickly.",0,-1));

		return map;
	}

}
