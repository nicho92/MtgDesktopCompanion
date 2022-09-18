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
import org.magic.api.interfaces.abstracts.AbstractMTGPlugin;
import org.magic.api.interfaces.abstracts.AbstractPoolProvider;
import org.magic.tools.POMReader;

import com.mchange.v2.c3p0.cfg.C3P0Config;

public class DBCPPool extends AbstractPoolProvider {

	
	private BasicDataSource dataSource;
	
	
	@Override
	public Map<String, String> getDefaultAttributes() {
			
		var map = new HashMap<String,String>();
		
		map.put("defaultAutoCommit",TRUE);
		map.put("cacheState",TRUE);
		map.put("lifo",String.valueOf(BaseObjectPoolConfig.DEFAULT_LIFO));
		map.put("maxTotal",String.valueOf(GenericObjectPoolConfig.DEFAULT_MAX_TOTAL));
		map.put("maxIdle",String.valueOf(GenericObjectPoolConfig.DEFAULT_MAX_IDLE));
		map.put("minIdle",String.valueOf(GenericObjectPoolConfig.DEFAULT_MIN_IDLE));
		map.put("initialSize","3");
		map.put("maxWaitMILLIS",String.valueOf(BaseObjectPoolConfig.DEFAULT_MAX_WAIT_MILLIS));
		map.put("poolPreparedStatements",FALSE);
		map.put("maxOpenPreparedStatements",String.valueOf(GenericKeyedObjectPoolConfig.DEFAULT_MAX_TOTAL));
		map.put("timeBetweenEvictionRunsMILLIS",String.valueOf(BaseObjectPoolConfig.DEFAULT_TIME_BETWEEN_EVICTION_RUNS));
		map.put("numTestsPerEvictionRun",String.valueOf(BaseObjectPoolConfig.DEFAULT_NUM_TESTS_PER_EVICTION_RUN));
		map.put("minEvictableIdleTimeMILLIS",String.valueOf(BaseObjectPoolConfig.DEFAULT_MIN_EVICTABLE_IDLE_TIME));
		map.put("softMinEvictableIdleTimeMILLIS",String.valueOf(BaseObjectPoolConfig.DEFAULT_SOFT_MIN_EVICTABLE_IDLE_TIME));
		map.put("evictionPolicyClassName",String.valueOf(BaseObjectPoolConfig.DEFAULT_EVICTION_POLICY_CLASS_NAME));
		map.put("testWhileIdle",FALSE);
		map.put("validationQuery","");
		map.put("validationQueryTimeoutSeconds","-1");
		map.put("accessToUnderlyingConnectionAllowed",FALSE);
		map.put("maxConnLifetimeMILLIS","-1");
		map.put("logExpiredConnections",TRUE);
		map.put("jmxName","org.magic.api:type=Pool,name="+getName());
		map.put("autoCommitOnReturn",TRUE);
		map.put("rollbackOnReturn",TRUE);
		map.put("defaultReadOnly",FALSE);
		map.put("defaultQueryTimeoutSeconds","");
		map.put("testOnCreate",FALSE);
		map.put("testOnBorrow",TRUE);
		map.put("testOnReturn",FALSE);
		map.put("fastFailValidation",FALSE);
		
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