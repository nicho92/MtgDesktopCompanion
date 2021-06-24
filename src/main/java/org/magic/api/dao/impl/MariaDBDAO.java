package org.magic.api.dao.impl;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.Grading;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.interfaces.abstracts.AbstractMagicSQLDAO;

public class MariaDBDAO extends AbstractMagicSQLDAO {

	private static final String MARIA_DUMP_PATH = "MARIA_DUMP_PATH";

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
	protected void storeCard(PreparedStatement pst, int position, MagicCard mc) throws SQLException {
		pst.setString(position, serialiser.toJsonElement(mc).toString());
	}

	@Override
	protected Grading readGrading(ResultSet rs) throws SQLException {
		return serialiser.fromJson(rs.getString("grading"), Grading.class);
	}
	
	@Override
	protected void storeGrade(PreparedStatement pst, int position, Grading grd) throws SQLException {
		pst.setString(position, serialiser.toJsonElement(grd).toString());
	}
	
	@Override
	protected List<MagicCardStock> readTransactionItems(ResultSet rs) throws SQLException {
		return serialiser.fromJsonList(rs.getString("stocksItem"), MagicCardStock.class);
	}
	
	@Override
	protected void storeTransactionItems(PreparedStatement pst, int position, List<MagicCardStock> grd) throws SQLException {
		pst.setString(position, serialiser.toJsonElement(grd).toString());
		
	}
	

	@Override
	protected Map<String, String> readTiersApps(ResultSet rs) throws SQLException {
		return  serialiser.fromJsonCollection(rs.getString("tiersAppIds"));
	}
	
	@Override
	protected void storeTiersApps(PreparedStatement pst, int i, Map<String, String> tiersAppIds) throws SQLException {
		pst.setString(i, serialiser.toJsonElement(tiersAppIds).toString());
	}
	
	@Override
	protected MagicCard readCard(ResultSet rs) throws SQLException {
		return serialiser.fromJson(rs.getString("mcard"), MagicCard.class);
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
	public void backup(File f) throws SQLException, IOException {

		if (getString(MARIA_DUMP_PATH).length() <= 0)
			throw new NullPointerException("Please fill "+MARIA_DUMP_PATH+" var");

		if (!getFile(MARIA_DUMP_PATH).exists())
			throw new IOException(getString(MARIA_DUMP_PATH) + " doesn't exist");

		var dumpCommand = new StringBuilder();
		dumpCommand.append(getString(MARIA_DUMP_PATH)).append("/mysqldump ").append(getString(DB_NAME))
				   .append(" -h ").append(getString(SERVERNAME))
				   .append(" -u ").append(getString(LOGIN))
				   .append(" -p").append(getString(PASS))
				   .append(" --port ").append(getString(SERVERPORT));
		
		var rt = Runtime.getRuntime();
		logger.info("begin Backup " + getString(DB_NAME));
		Process child;

		child = rt.exec(dumpCommand.toString());
		try (var ps = new PrintStream(f)) {
			var in = child.getInputStream();
			int ch;
			while ((ch = in.read()) != -1) {
				ps.write(ch);
			}
			logger.info("Backup " + getString(DB_NAME) + " done");
		}

	}


	@Override
	public void initDefault() {
		setProperty(SERVERPORT, "3306");
		setProperty(LOGIN, "mariadb");
		setProperty(PASS, "mariadb");
		setProperty(PARAMS, "?autoDeserialize=true&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&autoReconnect=true");
		setProperty(MARIA_DUMP_PATH, "C:\\Program Files (x86)\\Mysql\\bin");

	}



	@Override
	public String createListStockSQL() {
		return "select * from stocks where collection=? and JSON_EXTRACT(mcard,'$.name')=?";
	}


}