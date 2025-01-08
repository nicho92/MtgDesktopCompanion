package org.magic.api.pool.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractPoolProvider;

public class TomcatPool extends AbstractPoolProvider {

	private DataSource pool;
	
	
	@Override
	public Connection getConnection() throws SQLException {
		return pool.getConnection();
	}

	@Override
	public void close() throws SQLException {
		if(pool!=null)
			pool.close();

	}

	@Override
	public void init(String url, String user, String pass) {
		
		pool = new DataSource();
		var dbProperty = new PoolProperties();
		dbProperty.setUrl(url);
		dbProperty.setUsername(user);
		dbProperty.setPassword(pass);
		
		
		try {
			dbProperty.setDriverClassName(DriverManager.getDriver(url).getClass().getName());
		} catch (SQLException e) {
			//do nothing
		}
		
		
		dbProperty.setInitialSize(getInt("initialSize"));
		dbProperty.setMaxActive(getInt("maxActive"));
		dbProperty.setMaxIdle(getInt("maxIdle"));
		dbProperty.setMinIdle(getInt("minIdle"));
		dbProperty.setDefaultAutoCommit(getBoolean("defaultAutoCommit"));
		dbProperty.setMinEvictableIdleTimeMillis(getInt("minEvictableIdleTimeMillis"));
		pool.setPoolProperties(dbProperty);
	}
	
	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var map = new HashMap<String,MTGProperty>();
		
		map.put("defaultAutoCommit", MTGProperty.newBooleanProperty("true", "The default auto-commit state of connections created by this pool. If not set, default is JDBC driver default"));
		map.put("initialSize",MTGProperty.newIntegerProperty("10","The initial number of connections that are created when the pool is started.",1,-1));
		map.put("maxActive",MTGProperty.newIntegerProperty("10","The maximum number of active connections that can be allocated from this pool at the same time.",1,-1));
		map.put("maxIdle",MTGProperty.newIntegerProperty("5","The maximum number of connections that should be kept in the pool at all times. Idle connections are checked periodically (if enabled) and connections that been idle for longer than minEvictableIdleTimeMillis will be released.",1,-1));
		map.put("minIdle",MTGProperty.newIntegerProperty("2","The minimum number of established connections that should be kept in the pool at all times. The connection pool can shrink below this number if validation queries fail.",1,-1));
		map.put("minEvictableIdleTimeMillis",MTGProperty.newIntegerProperty("6000","The minimum amount of time an object may sit idle in the pool before it is eligible for eviction. The value is in millisecond.",1000,-1));

		return map;
	}

	
	
	@Override
	public String getVersion() {
		return "11.0.2";
	}
	

	@Override
	public String getName() {
		return "Tomcat";
	}

}
