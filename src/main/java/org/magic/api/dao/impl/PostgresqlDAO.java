package org.magic.api.dao.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.abstracts.AbstractSQLMagicDAO;
import org.postgresql.util.PGobject;

public class PostgresqlDAO extends AbstractSQLMagicDAO {

	private static final String URL_PGDUMP = "URL_PGDUMP";

	@Override
	public String getAutoIncrementKeyWord() {
		return "SERIAL";
	}

	@Override
	public String getjdbcnamedb() {
		return "postgresql";
	}

	@Override
	public String cardStorage() {
		return "json";
	}

	
	@Override
	public String createListStockSQL(MagicCard mc) {
		return "SELECT * FROM  stocks WHERE mcard->>'name' = ? and collection = ?";
	}
	
	@Override
	public void storeCard(PreparedStatement pst, int position, MagicCard mc) throws SQLException {
		
		PGobject jsonObject = new PGobject();
		jsonObject.setType("json");
		jsonObject.setValue(serialiser.toJsonElement(mc).toString());
		pst.setObject(position, jsonObject);
	}

	@Override
	public MagicCard readCard(ResultSet rs) throws SQLException {
		return serialiser.fromJson(((PGobject)rs.getObject("mcard")).getValue(), MagicCard.class);
	}

	@Override
	public void createIndex(Statement stat) throws SQLException {
		stat.executeUpdate("CREATE INDEX idx_id ON cards (ID);");
		stat.executeUpdate("CREATE INDEX idx_ed ON cards (edition);");
		stat.executeUpdate("CREATE INDEX idx_col ON cards (collection);");
		stat.executeUpdate("CREATE INDEX idx_cprov ON cards (cardprovider);");
		stat.executeUpdate("ALTER TABLE cards ADD PRIMARY KEY (ID,edition,collection);");
		
	}

	@Override
	public long getDBSize() {
		return 0;

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
