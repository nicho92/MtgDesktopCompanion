package org.magic.api.pool.impl;

import java.net.MalformedURLException;
import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.magic.api.beans.technical.MTGDocumentation;
import org.magic.api.beans.technical.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractPoolProvider;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.POMReader;

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
		try{
			datasource.close();
		}
		catch(Exception e)
		{
			throw new SQLException(e);
		}

	}

	@Override
	public MTGDocumentation getDocumentation() {
		try {
			return new MTGDocumentation(URI.create("https://raw.githubusercontent.com/brettwooldridge/HikariCP/dev/README.md").toURL(),FORMAT_NOTIFICATION.MARKDOWN);
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
	public Map<String, MTGProperty> getDefaultAttributes() {

		var map = new HashMap<String,MTGProperty>();

		map.put("autoCommit", MTGProperty.newBooleanProperty(TRUE, "This property controls the default auto-commit behavior of connections returned from the pool. It is a boolean value."));
		map.put("readOnly", MTGProperty.newBooleanProperty(FALSE, "This property controls whether Connections obtained from the pool are in read-only mode by default. Note some databases do not support the concept of read-only mode, while others provide query optimizations when the Connection is set to read-only. Whether you need this property or not will depend largely on your application and"));
		map.put("connectionTimeout", MTGProperty.newBooleanProperty("30000","This property controls the maximum number of milliseconds that a client (that's you) will wait for a connection from the pool. If this time is exceeded without a connection becoming available, a SQLException will be thrown. Lowest acceptable connection timeout is 250 ms."));
		map.put("poolName", MTGProperty.newBooleanProperty("mtg-hikari-pool","This property represents a user-defined name for the connection pool and appears mainly in logging and JMX management consoles to identify pools and pool configurations"));
		map.put("initializationFailTimeout", new MTGProperty("1","his property controls whether the pool will \"fail fast\" if the pool cannot be seeded with an initial connection successfully. Any positive number is taken to be the number of milliseconds to attempt to acquire an initial connection; the application thread will be blocked during this period. If a connection cannot be acquired before this timeout occurs, an exception will be thrown.A value less than zero will bypass any initial connection attempt, and the pool will start immediately while trying to obtain connections in the backgroun","-1","0","1"));
		map.put("registerMbeans", MTGProperty.newBooleanProperty(TRUE,"This property controls whether or not JMX Management Beans (\"MBeans\") are registered or not."));
		map.put("isolateInternalQueries", MTGProperty.newBooleanProperty(FALSE,"This property determines whether HikariCP isolates internal pool queries, such as the connection alive test, in their own transaction. Since these are typically read-only queries, it is rarely necessary to encapsulate them in their own transaction. This property only applies if autoCommit is disabled"));
		map.put("allowPoolSuspension", MTGProperty.newBooleanProperty(FALSE,"This property controls whether the pool can be suspended and resumed through JMX. This is useful for certain failover automation scenarios. When the pool is suspended, calls to getConnection() will not timeout and will be held until the pool is resumed"));
		map.put("maximumPoolSize", MTGProperty.newIntegerProperty("10","This property controls the maximum size that the pool is allowed to reach, including both idle and in-use connections. Basically this value will determine the maximum number of actual connections to the database backend. A reasonable value for this is best determined by your execution environment. When the pool reaches this size, and no idle connections are available, calls to getConnection() will block for up to connectionTimeout milliseconds before timing out.",1,-1));
		map.put("minimumIdle", MTGProperty.newIntegerProperty("10","This property controls the minimum number of idle connections that HikariCP tries to maintain in the pool. If the idle connections dip below this value and total connections in the pool are less than maximumPoolSize, HikariCP will make a best effort to add additional connections quickly and efficiently. However, for maximum performance and responsiveness to spike demands, we recommend not setting this value and instead allowing HikariCP to act as a fixed size connection pool. Default: same as maximumPoolSize",1,-1));
		map.put("leakDetectionThreshold", MTGProperty.newIntegerProperty("0", "This property controls the amount of time that a connection can be out of the pool before a message is logged indicating a possible connection leak. A value of 0 means leak detection is disabled.Lowest acceptable value for enabling leak detection is 2000 (2 seconds)", 0, 3000));
		map.put("validationTimeout", MTGProperty.newIntegerProperty("5000","This property controls the maximum amount of time that a connection will be tested for aliveness. This value must be less than the connectionTimeout. Lowest acceptable validation timeout is 250 ms.",250,-1));
		map.put("maxLifetime", MTGProperty.newIntegerProperty("1800000","This property controls the maximum lifetime of a connection in the pool. An in-use connection will never be retired, only when it is closed will it then be removed. On a connection-by-connection basis, minor negative attenuation is applied to avoid mass-extinction in the pool. We strongly recommend setting this value, and it should be several seconds shorter than any database or infrastructure imposed connection time limit. A value of 0 indicates no maximum lifetime (infinite lifetime), subject of course to the idleTimeout setting. The minimum allowed value is 30000ms (30 seconds).",30000,-1));
		map.put("idleTimeout", MTGProperty.newIntegerProperty("600000","his property controls the maximum amount of time that a connection is allowed to sit idle in the pool. This setting only applies when minimumIdle is defined to be less than maximumPoolSize. Idle connections will not be retired once the pool reaches minimumIdle connections. Whether a connection is retired as idle or not is subject to a maximum variation of +30 seconds, and average variation of +15 seconds. A connection will never be retired as idle before this timeout. A value of 0 means that idle connections are never removed from the pool. The minimum allowed value is 10000ms (10 seconds)",10000,-1));
		map.put("connectionInitSql", new MTGProperty("","his property sets a SQL statement that will be executed after every new connection creation before adding it to the pool. If this SQL is not valid or throws an exception, it will be treated as a connection failure and the standard retry logic will be followed"));
		
		
		map.put("dataSource.prepStmtCacheSqlLimit",MTGProperty.newIntegerProperty("2048","Only for MYSQL/MariaDB dao. This is the maximum length of a prepared SQL statement that the driver will cache. The MySQL default is 256. In our experience, especially with ORM frameworks like Hibernate, this default is well below the threshold of generated statement lengths.",256,-1));
		map.put("dataSource.prepStmtCacheSize",MTGProperty.newIntegerProperty("250","Only for MYSQL/MariaDB dao. This sets the number of prepared statements that the MySQL driver will cache per connection",250,500));
		map.put("dataSource.cachePrepStmts",MTGProperty.newBooleanProperty(TRUE,"Only for MYSQL/MariaDB dao. Neither of the above parameters have any effect if the cache is in fact disabled, as it is by default. You must set this parameter to true."));
		map.put("dataSource.useServerPrepStmts",MTGProperty.newBooleanProperty(TRUE,"Only for MYSQL/MariaDB dao. Newer versions of MySQL support server-side prepared statements, this can provide a substantial performance boost"));
		map.put("dataSource.useLocalSessionState",MTGProperty.newBooleanProperty(TRUE,"Only for MYSQL/MariaDB dao. Should the driver refer to the internal values of auto-commit and transaction isolation that are set by 'Connection.setAutoCommit()' and 'Connection.setTransactionIsolation()' and transaction state as maintained by the protocol, rather than querying the database or blindly sending commands to the database for 'commit()' or 'rollback()' method calls"));
		map.put("dataSource.cacheServerConfiguration",MTGProperty.newBooleanProperty(TRUE,"Only for MYSQL/MariaDB dao. Should the driver cache the results of \"SHOW VARIABLES\" and \"SHOW COLLATION\" on a per-URL basis"));
		
		
		
		return map;

	}


}
