package org.magic.api.dao.impl;

import java.io.File;
import java.util.Map;

import org.jooq.SQLDialect;
import org.magic.api.interfaces.abstracts.extra.AbstractMagicSQLDAO;
import org.magic.services.MTGConstants;

public class DerbyDAO extends AbstractMagicSQLDAO {

	@Override
	public String getName() {
		return "Derby";
	}

	@Override
	protected String getjdbcnamedb() {
		return "derby";
	}

	@Override
	protected String getjdbcUrl() {
		return "jdbc:derby:"+MTGConstants.DATA_DIR.getAbsolutePath()+"\\"+getString(DB_NAME)+";create="+!new File(getFile(SERVERNAME),getString(DB_NAME)).exists();
	}
	
	@Override
	protected String createListStockSQL() {
		return "select * from stocks where collection=? and mcard like ?";
	}

	@Override
	protected String getdbSizeQuery() {
		return "SELECT T2.CONGLOMERATENAME, (T2.PAGESIZE * T2.NUMALLOCATEDPAGES) FROM SYS.SYSTABLES systabs, TABLE (SYSCS_DIAG.SPACE_TABLE(systabs.tablename)) AS T2 WHERE systabs.tabletype = 'T'   AND T2.CONGLOMERATENAME NOT LIKE 'IDX%' AND T2.CONGLOMERATENAME NOT LIKE 'SQL%'";
	}

	@Override
	protected SQLDialect getDialect() {
		return SQLDialect.DERBY;
	}
	
	@Override
	public Map<String, String> getDefaultAttributes() {
		var m = super.getDefaultAttributes();
		m.put(DB_NAME, "derbyDB");
		m.put(SERVERNAME, new File(MTGConstants.DATA_DIR,m.get(DB_NAME)).getAbsolutePath());
		return m;
	}
	

}
