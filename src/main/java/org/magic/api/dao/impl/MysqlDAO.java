package org.magic.api.dao.impl;

import java.util.Map;

import org.jooq.SQLDialect;
import org.magic.api.interfaces.abstracts.extra.AbstractMagicSQLDAO;

public class MysqlDAO extends AbstractMagicSQLDAO {


	@Override
	protected String getjdbcnamedb() {
		return getName().toLowerCase();
	}

	@Override
	protected SQLDialect getDialect() {
		return SQLDialect.MYSQL;
	}


	@Override
	public String getdbSizeQuery() {
		return "SELECT table_name AS 'Table', (data_length + index_length) as size FROM information_schema.TABLES WHERE table_schema = '"+getString(DB_NAME)+"' ORDER BY size DESC";
	}


	@Override
	public String getName() {
		return "MySQL";
	}

	
	@Override
	public Map<String, String> getDefaultAttributes() {
		var m = super.getDefaultAttributes();
			  m.put(SERVERPORT, "3306");
			  m.put(PARAMS, "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&autoReconnect=true&useServerPrepStmts=TRUE");
		return m;
	}



	@Override
	public String createListStockSQL() {
		return "select * from stocks where collection=? and JSON_EXTRACT(mcard,'$.name')=?";
	}


}