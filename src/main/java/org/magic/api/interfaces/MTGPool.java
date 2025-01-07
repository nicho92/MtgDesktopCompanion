package org.magic.api.interfaces;

import java.sql.Connection;
import java.sql.SQLException;

public interface MTGPool  extends MTGPlugin{

	Connection getConnection() throws SQLException;

	void close() throws SQLException;

	void init(String url, String user, String pass);

}