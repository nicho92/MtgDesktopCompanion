package org.magic.api.pool.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.pool2.impl.BaseObjectPoolConfig;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.magic.api.interfaces.abstracts.AbstractMTGPlugin;
import org.magic.api.interfaces.abstracts.AbstractPool;

public class DBCPPool extends AbstractPool {

	
	private BasicDataSource dataSource;
	
	

	@Override
	public void initDefault() {
		setProperty("defaultAutoCommit","true");
		setProperty("defaultQueryTimeoutSeconds","");
		setProperty("cacheState","true");
		setProperty("lifo",String.valueOf(BaseObjectPoolConfig.DEFAULT_LIFO));
		setProperty("maxTotal",String.valueOf(GenericObjectPoolConfig.DEFAULT_MAX_TOTAL));
		setProperty("maxIdle",String.valueOf(GenericObjectPoolConfig.DEFAULT_MAX_IDLE));
		setProperty("minIdle",String.valueOf(GenericObjectPoolConfig.DEFAULT_MIN_IDLE));
		setProperty("initialSize","3");
		setProperty("maxWaitMILLIS",String.valueOf(BaseObjectPoolConfig.DEFAULT_MAX_WAIT_MILLIS));
		setProperty("poolPreparedStatements","false");
		setProperty("maxOpenPreparedStatements",String.valueOf(GenericKeyedObjectPoolConfig.DEFAULT_MAX_TOTAL));
		setProperty("testOnCreate","false");
		setProperty("testOnBorrow","true");
		setProperty("testOnReturn","false");
		setProperty("timeBetweenEvictionRunsMILLIS",String.valueOf(BaseObjectPoolConfig.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS));
		setProperty("numTestsPerEvictionRun",String.valueOf(BaseObjectPoolConfig.DEFAULT_NUM_TESTS_PER_EVICTION_RUN));
		setProperty("minEvictableIdleTimeMILLIS",String.valueOf(BaseObjectPoolConfig.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS));
		setProperty("softMinEvictableIdleTimeMILLIS",String.valueOf(BaseObjectPoolConfig.DEFAULT_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS));
		setProperty("evictionPolicyClassName",String.valueOf(BaseObjectPoolConfig.DEFAULT_EVICTION_POLICY_CLASS_NAME));
		setProperty("testWhileIdle","false");
		setProperty("validationQuery","");
		setProperty("validationQueryTimeoutSeconds","-1");
		setProperty("accessToUnderlyingConnectionAllowed","false");
		setProperty("maxConnLifetimeMILLIS","-1");
		setProperty("logExpiredConnections","true");
		setProperty("jmxName","org.magic.api:type=Pool,name="+getName());
		setProperty("autoCommitOnReturn","true");
		setProperty("rollbackOnReturn","true");
		setProperty("defaultAutoCommit","true");
		setProperty("defaultReadOnly","false");
		setProperty("defaultQueryTimeoutSeconds","");
		setProperty("cacheState","true");
		setProperty("lifo",String.valueOf(BaseObjectPoolConfig.DEFAULT_LIFO));
		setProperty("maxTotal",String.valueOf(GenericObjectPoolConfig.DEFAULT_MAX_TOTAL));
		setProperty("maxIdle",String.valueOf(GenericObjectPoolConfig.DEFAULT_MAX_IDLE));
		setProperty("minIdle",String.valueOf(GenericObjectPoolConfig.DEFAULT_MIN_IDLE));
		setProperty("initialSize","0");
		setProperty("maxWaitMILLIS",String.valueOf(BaseObjectPoolConfig.DEFAULT_MAX_WAIT_MILLIS));
		setProperty("poolPreparedStatements","false");
		setProperty("maxOpenPreparedStatements",String.valueOf(GenericKeyedObjectPoolConfig.DEFAULT_MAX_TOTAL));
		setProperty("testOnCreate","false");
		setProperty("testOnBorrow","true");
		setProperty("testOnReturn","false");
		setProperty("timeBetweenEvictionRunsMILLIS",String.valueOf(BaseObjectPoolConfig.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS));
		setProperty("numTestsPerEvictionRun",String.valueOf(BaseObjectPoolConfig.DEFAULT_NUM_TESTS_PER_EVICTION_RUN));
		setProperty("minEvictableIdleTimeMILLIS",String.valueOf(BaseObjectPoolConfig.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS));
		setProperty("softMinEvictableIdleTimeMILLIS",String.valueOf(BaseObjectPoolConfig.DEFAULT_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS));
		setProperty("evictionPolicyClassName",String.valueOf(BaseObjectPoolConfig.DEFAULT_EVICTION_POLICY_CLASS_NAME));
		setProperty("testWhileIdle","false");
		setProperty("validationQuery","");
		setProperty("validationQueryTimeoutSeconds","-1");
		setProperty("accessToUnderlyingConnectionAllowed","false");
		setProperty("maxConnLifetimeMILLIS","-1");
		setProperty("logExpiredConnections","true");
		setProperty("autoCommitOnReturn","true");
		setProperty("rollbackOnReturn","true");
		setProperty("fastFailValidation","false");
	}
	
	@Override
	public void init(String url, String user, String pass, boolean enable) {

		logger.debug("init connection to " + url + ", Pooling="+enable);
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