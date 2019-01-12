package org.magic.api.dao.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.abstracts.AbstractSQLMagicDAO;
import org.magic.services.MTGConstants;

public class SQLLiteDAO extends AbstractSQLMagicDAO {

	@Override
	public long getDBSize() {
		return FileUtils.sizeOf(getFile(SERVERNAME));
	}

	@Override
	public void backup(File dir) throws SQLException, IOException {
		File base = getFile(SERVERNAME);
		try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(new File(dir, "backup.zip")))) {
			for (File doc : base.listFiles()) {
				if (!doc.getName().endsWith(".tmp")) {
					try (FileInputStream in = new FileInputStream(doc)) {
						out.putNextEntry(new ZipEntry(doc.getName()));
						int len;
						while ((len = in.read(new byte[4096])) > 0) {
							out.write(new byte[4096], 0, len);
						}
						out.closeEntry();
					}
				}
			}
		}
	}
	
	@Override
	public void initDefault() {
		super.initDefault();
		setProperty(SERVERNAME, Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(),"sqlite-db").toFile().getAbsolutePath());
		setProperty(LOGIN, "SA");
		setProperty(PASS, "");
		setProperty(DB_NAME,"");
	}
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	

	public void createIndex(Statement stat) throws SQLException {
		stat.executeUpdate("CREATE INDEX idx_id ON cards (ID);");
		stat.executeUpdate("CREATE INDEX idx_ed ON cards (edition);");
		stat.executeUpdate("CREATE INDEX idx_col ON cards (collection);");
		stat.executeUpdate("CREATE INDEX idx_cprov ON cards (cardprovider);");
		stat.executeUpdate("CREATE UNIQUE INDEX pk_index_cards ON cards (ID,edition,collection);");
	}
	

	@Override
	public String getName() {
		return "SQLite";
	}

	@Override
	protected String getAutoIncrementKeyWord() {
		return "INTEGER"; // primary key column is autoincrement
	}

	@Override
	protected String getjdbcnamedb() {
		return getName().toLowerCase();
	}

	@Override
	protected String cardStorage() {
		return "json";
	}

	@Override
	protected void storeCard(PreparedStatement pst, int position, MagicCard mc) throws SQLException {
		pst.setString(position, serialiser.toJsonElement(mc).toString());
	}

	@Override
	protected MagicCard readCard(ResultSet rs) throws SQLException {
		return serialiser.fromJson( rs.getObject("mcard").toString(), MagicCard.class);
	}

	@Override
	protected String createListStockSQL(MagicCard mc) {
		return "select * from stocks where collection=? and JSON_EXTRACT(mcard,'$.name')=?";

	}
	
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		SQLLiteDAO dao = new SQLLiteDAO();
		dao.init();
	}

}
