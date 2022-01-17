package org.magic.api.pool.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.magic.api.interfaces.abstracts.AbstractPoolProvider;

public class NoPool extends AbstractPoolProvider {

	private String url;
	private String user;
	private String pass;
	
	@Override
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, user, pass);
	}

	@Override
	public void close() throws SQLException {
		// do nothing

	}

	@Override
	public void init(String url, String user, String pass, boolean enable) {
		this.url=url;
		this.user=user;
		this.pass=pass;

	}

	@Override
	public String getName() {
		return "No Pool";

	}
}
