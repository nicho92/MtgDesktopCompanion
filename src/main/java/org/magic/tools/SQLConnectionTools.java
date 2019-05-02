package org.magic.tools;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.Logger;
import org.magic.services.MTGLogger;

public class SQLConnectionTools
{
    private BasicDataSource dataSource;
	private static Logger logger = MTGLogger.getLogger(SQLConnectionTools.class);
	
	
	public SQLConnectionTools(String url, String user,String pass)
	{
		  dataSource =  new BasicDataSource();
          dataSource.setUrl(url);
          dataSource.setUsername(user);
          dataSource.setPassword(pass);
          
          dataSource.setMinIdle(0);
          dataSource.setMaxIdle(8);
          dataSource.setInitialSize(3);
   
          dataSource.setJmxName("org.magic.api:type=Pool,name=DBCP2");
          dataSource.setMaxTotal(10);
          dataSource.setMaxWaitMillis(5000);
 	}
	
	public Connection getConnection() throws SQLException {
		Connection c = dataSource.getConnection();
		logger.trace("Idle:"+dataSource.getNumIdle() +", Active :"+dataSource.getNumActive() + "/"  + dataSource.getMaxTotal());
		return c;
	}
}
