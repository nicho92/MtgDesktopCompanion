package org.magic.api.dao.impl;

import java.util.Map;

import org.magic.api.interfaces.abstracts.extra.AbstractMagicSQLDAO;

public class MariaDBDAO extends AbstractMagicSQLDAO {

	@Override
	protected String getAutoIncrementKeyWord() {
		return "INTEGER AUTO_INCREMENT";
	}

	@Override
	protected String getjdbcnamedb() {
		return getName().toLowerCase();
	}

	@Override
	protected String beanStorage() {
		return "LONGTEXT";
	}


	@Override
	protected String longTextStorage() {
		return "LONGTEXT";
	}


	@Override
	protected String getdbSizeQuery() {
		return "SELECT Round(Sum(data_length + index_length), 1) FROM information_schema.tables WHERE  table_schema = '"+getString(DB_NAME)+"'";
	}

	@Override
	public String getName() {
		return "MariaDB";
	}


	@Override
	public Map<String, String> getDefaultAttributes() {
		var m = super.getDefaultAttributes();

		m.put(SERVERPORT, "3306");
		m.put(PARAMS, "?autoDeserialize=true&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&autoReconnect=true");
		return m;
	}


	@Override
	public String createListStockSQL() {
		return "select * from stocks where collection=? and JSON_EXTRACT(mcard,'$.name')=?";
	}


}