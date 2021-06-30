package org.magic.api.dao.impl;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.magic.api.interfaces.abstracts.AbstractMagicSQLDAO;

public class OracleDbDAO extends AbstractMagicSQLDAO{

	
	@Override
	public void initDefault() {
		setProperty(SERVERPORT, "1521");
		setProperty(LOGIN, "user");
		setProperty(PASS, "mypass");
		setProperty(PARAMS, "");
		setProperty("DUMP_PATH", "");
	}

	
	
	@Override
	public void backup(File dir) throws SQLException, IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		return "Oracle";
	}

	@Override
	protected String getAutoIncrementKeyWord() {
		return "IDENTITY";
	}

	@Override
	protected String getjdbcnamedb() {
		return getName().toLowerCase();
	}

	@Override
	protected String beanStorage() {
		return "JSON";
	}

	@Override
	protected String longTextStorage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String createListStockSQL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getdbSizeQuery() {
		return "select sum(bytes)/1024/1024 size_in_mb from dba_data_files;";
	}

}
