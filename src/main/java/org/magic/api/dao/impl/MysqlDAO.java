package org.magic.api.dao.impl;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Map;

import org.magic.api.interfaces.abstracts.extra.AbstractMagicSQLDAO;

public class MysqlDAO extends AbstractMagicSQLDAO {

	private static final String MYSQL_DUMP_PATH = "MYSQL_DUMP_PATH";

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
	public String getdbSizeQuery() {
		return "SELECT table_name AS 'Table', (data_length + index_length) as size FROM information_schema.TABLES WHERE table_schema = '"+getString(DB_NAME)+"' ORDER BY size DESC";
	}


	@Override
	public String getName() {
		return "MySQL";
	}

	@Override
	public void backup(File f) throws SQLException, IOException {

		if (getString(MYSQL_DUMP_PATH).length() <= 0)
			throw new NullPointerException("Please fill MYSQL_DUMP_PATH var");

		if (!getFile(MYSQL_DUMP_PATH).exists())
			throw new IOException(getString(MYSQL_DUMP_PATH) + " doesn't exist");


		var pb = new ProcessBuilder(getString(MYSQL_DUMP_PATH) + "/mysqldump",getString(DB_NAME), "-h",getString(SERVERNAME),"-u",getString(LOGIN),"-p",getString(PASS),"--port",getString(SERVERPORT));

		logger.info("begin Backup {}",getString(DB_NAME));
		Process child;

		child = pb.start();
		try (var ps = new PrintStream(f)) {
			var in = child.getInputStream();
			int ch;
			while ((ch = in.read()) != -1) {
				ps.write(ch);
			}
			logger.info("Backup {} done",getString(DB_NAME));
		}

	}


	@Override
	public Map<String, String> getDefaultAttributes() {
		var m = super.getDefaultAttributes();

		m.put(SERVERPORT, "3306");
		m.put(PARAMS, "?autoDeserialize=true&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&autoReconnect=true");
		m.put(MYSQL_DUMP_PATH, "C:\\Program Files (x86)\\Mysql\\bin");
		return m;
	}



	@Override
	public String createListStockSQL() {
		return "select * from stocks where collection=? and JSON_EXTRACT(mcard,'$.name')=?";
	}


}