package org.magic.api.pool.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.dbcp2.BasicDataSource;
import org.magic.api.interfaces.abstracts.AbstractMTGPlugin;
import org.magic.api.interfaces.abstracts.AbstractPool;

public class DBCPPool extends AbstractPool {

	
	private BasicDataSource dataSource;
	
	
	@Override
	public void init(String url, String user, String pass, boolean enable) {

		logger.debug("init connection to " + url + ", Pooling="+enable);
		dataSource =  new BasicDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(pass);
        dataSource.setJmxName("org.magic.api:type=Pool,name="+getName());
        dataSource.setMaxWaitMillis(5000);
    
        
        dataSource.setPoolPreparedStatements(getBoolean("POOL_PREPARED_STATEMENT"));
        dataSource.setMinIdle(getInt("POOL_MIN_IDLE"));
        dataSource.setMaxIdle(getInt("POOL_MAX_IDLE"));
        dataSource.setInitialSize(getInt("POOL_INIT_SIZE"));
        dataSource.setMaxTotal(getInt("POOL_MAX_SIZE"));
        
        
//        dataSource.setRemoveAbandonedOnBorrow(true);
//        dataSource.setRemoveAbandonedOnMaintenance(true);
//        dataSource.setRemoveAbandonedTimeout(3);
//        dataSource.setTimeBetweenEvictionRunsMillis(TimeUnit.MINUTES.toMicros(1L));
//        dataSource.setNumTestsPerEvictionRun(3);
//        dataSource.setMinEvictableIdleTimeMillis(TimeUnit.MINUTES.toMicros(1L));
//        dataSource.setValidationQuery("select 1");
//			dataSource.setAbandonedUsageTracking(true);
//        dataSource.setAbandonedLogWriter(new PrintWriter(System.err));
//        dataSource.setLogAbandoned(true);

        if(!enable) {
			  dataSource.setMinIdle(1);
	          dataSource.setMaxIdle(1);
	          dataSource.setInitialSize(0);
	          dataSource.setMaxTotal(1);
		  }
		
	}

	@Override
	public String getVersion() {
		return "2.7.0";
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