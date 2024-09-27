package org.magic.api.pool.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.pool2.impl.BaseObjectPoolConfig;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractMTGPlugin;
import org.magic.api.interfaces.abstracts.AbstractPoolProvider;
import org.magic.services.tools.POMReader;

import com.mchange.v2.c3p0.cfg.C3P0Config;

public class DBCPPool extends AbstractPoolProvider {


	private BasicDataSource dataSource;


	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {

		var map = new HashMap<String,MTGProperty>();

		map.put("defaultAutoCommit",MTGProperty.newBooleanProperty(TRUE, "The default auto-commit state of connections created by this pool. If not set then the setAutoCommit method will not be called."));
		map.put("cacheState",MTGProperty.newBooleanProperty(TRUE, "If true, the pooled connection will cache the current readOnly and autoCommit settings when first read or written and on all subsequent writes. This removes the need for additional database queries for any further calls to the getter. If the underlying connection is accessed directly and the readOnly and/or autoCommit settings changed the cached values will not reflect the current state. In this case, caching should be disabled by setting this attribute to false"));
		map.put("testWhileIdle",MTGProperty.newBooleanProperty(FALSE, "The indication of whether objects will be validated by the idle object evictor (if any). If an object fails to validate, it will be dropped from the pool."));
		map.put("poolPreparedStatements",MTGProperty.newBooleanProperty(FALSE,"Enable prepared statement pooling for this pool"));
		map.put("logExpiredConnections",MTGProperty.newBooleanProperty(TRUE,"Flag to log a message indicating that a connection is being closed by the pool due to maxConnLifetimeMillis exceeded. Set this property to false to suppress expired connection logging that is turned on by default."));
		map.put("enableAutoCommitOnReturn",MTGProperty.newBooleanProperty(TRUE,"If true, connections being returned to the pool will be checked and configured with Connection.setAutoCommit(true) if the auto commit setting is false when the connection is returned."));
		map.put("rollbackOnReturn",MTGProperty.newBooleanProperty(TRUE,"True means a connection will be rolled back when returned to the pool if auto commit is not enabled and the connection is not read-only."));
		map.put("defaultReadOnly",MTGProperty.newBooleanProperty(FALSE,"The default read-only state of connections created by this pool. If not set then the setReadOnly method will not be called. (Some drivers don't support read only mode, ex: Informix)"));
		map.put("testOnCreate",MTGProperty.newBooleanProperty(FALSE,"The indication of whether objects will be validated after creation. If the object fails to validate, the borrow attempt that triggered the object creation will fail."));
		map.put("testOnBorrow",MTGProperty.newBooleanProperty(TRUE,"The indication of whether objects will be validated before being borrowed from the pool. If the object fails to validate, it will be dropped from the pool, and we will attempt to borrow another."));
		map.put("testOnReturn",MTGProperty.newBooleanProperty(FALSE,"The indication of whether objects will be validated before being returned to the pool."));
		map.put("fastFailValidation",MTGProperty.newBooleanProperty(FALSE,"When this property is true, validation fails fast for connections that have thrown \"fatal\" SQLExceptions. Requests to validate disconnected connections fail immediately, with no call to the driver's isValid method or attempt to execute a validation query."));
		map.put("lifo",MTGProperty.newBooleanProperty(String.valueOf(BaseObjectPoolConfig.DEFAULT_LIFO),"True means that borrowObject returns the most recently used (\"last in\") connection in the pool (if there are idle connections available). False means that the pool behaves as a FIFO queue - connections are taken from the idle instance pool in the order that they are returned to the pool."));
		map.put("maxTotal",MTGProperty.newIntegerProperty(String.valueOf(GenericObjectPoolConfig.DEFAULT_MAX_TOTAL),"The maximum number of active connections that can be allocated from this pool at the same time, or negative for no limit.",-1,-1));
		map.put("maxIdle",MTGProperty.newIntegerProperty(String.valueOf(GenericObjectPoolConfig.DEFAULT_MAX_IDLE),"The maximum number of connections that can remain idle in the pool, without extra ones being released, or negative for no limit.",-1,-1));
		map.put("minIdle",MTGProperty.newIntegerProperty(String.valueOf(GenericObjectPoolConfig.DEFAULT_MIN_IDLE),"The minimum number of connections that can remain idle in the pool, without extra ones being created, or zero to create none.",-1,-1));
		map.put("validationQuery",new MTGProperty("","The SQL query that will be used to validate connections from this pool before returning them to the caller. If specified, this query MUST be an SQL SELECT statement that returns at least one row. If not specified, connections will be validation by calling the isValid() method."));
		map.put("initialSize", MTGProperty.newIntegerProperty("3","The initial number of connections that are created when the pool is started.",1,-1));
		map.put("maxConnLifetimeMILLIS", MTGProperty.newIntegerProperty("-1","The maximum lifetime in milliseconds of a connection. After this time is exceeded the connection will fail the next activation, passivation or validation test. A value of zero or less means the connection has an infinite lifetime.",0,-1));
		map.put("maxWaitMILLIS",MTGProperty.newIntegerProperty(String.valueOf(BaseObjectPoolConfig.DEFAULT_MAX_WAIT),"The maximum number of milliseconds that the pool will wait (when there are no available connections) for a connection to be returned before throwing an exception, or -1 to wait indefinitely.",-1,-1));
		map.put("maxOpenPreparedStatements",MTGProperty.newIntegerProperty(String.valueOf(GenericKeyedObjectPoolConfig.DEFAULT_MAX_TOTAL),"The maximum number of open statements that can be allocated from the statement pool at the same time, or negative for no limit.",1,-1));
		map.put("timeBetweenEvictionRunsMILLIS",MTGProperty.newIntegerProperty(String.valueOf(BaseObjectPoolConfig.DEFAULT_DURATION_BETWEEN_EVICTION_RUNS.toMillis()),"The number of milliseconds to sleep between runs of the idle object evictor thread. When non-positive, no idle object evictor thread will be run.",1,-1));
		map.put("numTestsPerEvictionRun",MTGProperty.newIntegerProperty(String.valueOf(BaseObjectPoolConfig.DEFAULT_NUM_TESTS_PER_EVICTION_RUN),"The number of objects to examine during each run of the idle object evictor thread (if any).",1,-1));
		map.put("minEvictableIdleTimeMILLIS",MTGProperty.newIntegerProperty(String.valueOf(BaseObjectPoolConfig.DEFAULT_MIN_EVICTABLE_IDLE_DURATION.toMillis()),"The minimum amount of time in milisecond an object may sit idle in the pool before it is eligible for eviction by the idle object evictor (if any).",1000,-1));
		map.put("softMinEvictableIdleTimeMILLIS",MTGProperty.newIntegerProperty(String.valueOf(BaseObjectPoolConfig.DEFAULT_SOFT_MIN_EVICTABLE_IDLE_DURATION.toMillis()),"The minimum amount of time a connection may sit idle in the pool before it is eligible for eviction by the idle connection evictor, with the extra condition that at least \"minIdle\" connections remain in the pool. When minEvictableIdleTimeMillis is set to a positive value, minEvictableIdleTimeMillis is examined first by the idle connection evictor - i.e. when idle connections are visited by the evictor, idle time is first compared against minEvictableIdleTimeMillis (without considering the number of idle connections in the pool) and then against softMinEvictableIdleTimeMillis, including the minIdle constraint.",-1,-1));
		map.put("accessToUnderlyingConnectionAllowed", MTGProperty.newBooleanProperty(FALSE,"Controls if the PoolGuard allows access to the underlying connection."));
		map.put("jmxName", new MTGProperty("org.magic.api:type=Pool,name="+getName(),"Registers the DataSource as JMX MBean under specified name. The name has to conform to the JMX Object Name Syntax (see javadoc)."));

		return map;
	}

	@Override
	public void init(String url, String user, String pass, boolean enable) {

		logger.debug("init connection to {} pool={}",url,enable);
		dataSource =  new BasicDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(pass);

        props.entrySet().forEach(ks->{
        	try {
				BeanUtils.setProperty(dataSource, ks.getKey().toString(), ks.getValue());
			} catch (Exception e) {
				logger.error(e);
			}
        });

        if(!enable) {
			  dataSource.setMinIdle(1);
	          dataSource.setMaxIdle(1);
	          dataSource.setInitialSize(0);
	          dataSource.setMaxTotal(1);
		  }
  	}

	@Override
	public String getVersion() {
		return POMReader.readVersionFromPom(C3P0Config.class, "/META-INF/maven/org.apache.commons/commons-pool2/pom.properties");
	}

	@Override
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}


	@Override
	public void close() throws SQLException {
		dataSource.close();

	}

	@Override
	public String getName() {
		return "DBCP2";
	}


	@Override
	public Icon getIcon() {
		return new ImageIcon(AbstractMTGPlugin.class.getResource("/icons/plugins/apache.png"));
	}

}