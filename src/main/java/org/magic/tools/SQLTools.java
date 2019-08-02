package org.magic.tools;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.Logger;
import org.magic.services.MTGLogger;

public class SQLTools
{
    private BasicDataSource dataSource;
	private static Logger logger = MTGLogger.getLogger(SQLTools.class);

	
	public SQLTools(String url, String user,String pass, boolean enable)
	{
		
		  logger.debug("init connection to " + url + ", Pooling="+enable);
		  dataSource =  new BasicDataSource();
          dataSource.setUrl(url);
          dataSource.setUsername(user);
          dataSource.setPassword(pass);
          dataSource.setJmxName("org.magic.api:type=Pool,name=DBCP2");
          dataSource.setMaxWaitMillis(5000);
          dataSource.setPoolPreparedStatements(true);
          dataSource.setMinIdle(1);
          dataSource.setMaxIdle(8);
          dataSource.setInitialSize(3);
          dataSource.setMaxTotal(10);
          
          if(!enable) {
			  dataSource.setMinIdle(1);
	          dataSource.setMaxIdle(1);
	          dataSource.setInitialSize(0);
	          dataSource.setMaxTotal(1);
		  }
 	}
	
	public void close()
	{
		try {
			dataSource.close();
		} catch (SQLException e) {
			logger.error(e);
		}
	}
	
	public Connection getConnection() throws SQLException {
		Connection c = dataSource.getConnection();
		logger.trace("Idle:"+dataSource.getNumIdle() +", Active :"+dataSource.getNumActive() + "/"  + dataSource.getMaxTotal());
		return c;
	}

}
