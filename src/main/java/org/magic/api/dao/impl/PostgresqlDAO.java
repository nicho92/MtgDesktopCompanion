package org.magic.api.dao.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.abstracts.AbstractSQLMagicDAO;
import org.magic.tools.SQLConnectionTools;
import org.postgresql.util.PGobject;

public class PostgresqlDAO extends AbstractSQLMagicDAO {

	private static final String URL_PGDUMP = "URL_PGDUMP";

	@Override
	protected String getAutoIncrementKeyWord() {
		return "SERIAL";
	}

	
	@Override
	protected String getjdbcnamedb() {
		return "postgresql";
	}

	@Override
	protected String cardStorage() {
		return "json";
	}

	
	@Override
	protected String createListStockSQL(MagicCard mc) {
		return "SELECT * FROM  stocks WHERE mcard->>'name' = ? and collection = ?";
	}
	
	@Override
	protected void storeCard(PreparedStatement pst, int position, MagicCard mc) throws SQLException {
		
		PGobject jsonObject = new PGobject();
		jsonObject.setType("json");
		jsonObject.setValue(serialiser.toJsonElement(mc).toString());
		pst.setObject(position, jsonObject);
	}

	@Override
	protected MagicCard readCard(ResultSet rs) throws SQLException {
		return serialiser.fromJson(((PGobject)rs.getObject("mcard")).getValue(), MagicCard.class);
	}

	@Override
	protected String getdbSizeQuery() {
		return "SELECT pg_database_size('"+getString(DB_NAME)+"');";
	}

	@Override
	public long getDBSize() {
		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(getdbSizeQuery(),ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY,ResultSet.HOLD_CURSORS_OVER_COMMIT); ResultSet rs = pst.executeQuery();) {
			rs.first();
			return (long) rs.getDouble(1);
		} catch (SQLException e) {
			logger.error(e);
			return 0;
		}
	}

	@Override
	public String getName() {
		return "PostGreSQL";
	}

	@Override
	public void backup(File f) throws IOException {

		if (getString(URL_PGDUMP).length() <= 0) {
			throw new NullPointerException("Please fill URL_PGDUMP var");
		}

		String dumpCommand = getString(URL_PGDUMP) + "/pg_dump" + " -d" + getString(DB_NAME)
				+ " -h" + getString(SERVERNAME) + " -U" + getString(LOGIN) + " -p"
				+ getString(SERVERPORT);

		Runtime rt = Runtime.getRuntime();
		logger.info("begin Backup :" + dumpCommand);

		Process child = rt.exec(dumpCommand);
		try (PrintStream ps = new PrintStream(f)) {
			InputStream in = child.getInputStream();
			int ch;
			while ((ch = in.read()) != -1) {
				ps.write(ch);
			}
			logger.info("Backup " + getString(DB_NAME) + " done");
		}

	}

	
	
	@Override
	public void initDefault() {
		super.initDefault();
		setProperty(SERVERPORT, "5432");
		setProperty(LOGIN, "postgres");
		setProperty(PASS, "postgres");
		setProperty(URL_PGDUMP, "C:/Program Files (x86)/PostgreSQL/9.5/bin");

	}


}
