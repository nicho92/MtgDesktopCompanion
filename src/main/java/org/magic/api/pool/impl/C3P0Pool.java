package org.magic.api.pool.impl;

import java.sql.Connection;
import java.sql.SQLException;

import org.magic.api.interfaces.abstracts.AbstractPool;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class C3P0Pool extends AbstractPool {

	
	ComboPooledDataSource datasource;
	
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

}
