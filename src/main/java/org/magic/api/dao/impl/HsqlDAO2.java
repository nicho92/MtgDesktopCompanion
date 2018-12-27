package org.magic.api.dao.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

public class HsqlDAO2 extends AbstractSQLMagicDAO {

	
	@Override
	public void createIndex(Statement stat) throws SQLException {
		stat.executeUpdate("CREATE INDEX idx_id ON cards (ID);");
		stat.executeUpdate("CREATE INDEX idx_ed ON cards (edition);");
		stat.executeUpdate("CREATE INDEX idx_col ON cards (collection);");
		stat.executeUpdate("CREATE INDEX idx_cprov ON cards (cardprovider);");
		stat.executeUpdate("ALTER TABLE cards ADD PRIMARY KEY (ID,edition,collection);");
	}
	
	@Override
	public String getAutoIncrementKeyWord() {
		return "IDENTITY";
	}
	
	
	@Override
	public String cardStorage() {
		return "LONGVARCHAR";
	}
	
	@Override
	public String getjdbcnamedb() {
		return "hsqldb";
	}
	
	@Override
	public MagicCard readCard(ResultSet rs) throws SQLException {
		return serialiser.fromJson( rs.getObject("mcard").toString(), MagicCard.class);
	}
	
	@Override
	public void storeCard(PreparedStatement pst, int position, MagicCard mc) throws SQLException {
		pst.setString(position, serialiser.toJsonElement(mc).toString());
		
		
	}
	

	@Override
	public String getDBLocation() {
		return getString(SERVERNAME);
	}

	@Override
	public long getDBSize() {
		return FileUtils.sizeOfDirectory(getFile(SERVERNAME));
	}


	public String getName() {
		return "hSQLdb2";
	}

	@Override
	public void backup(File dir) throws IOException {
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
	public String createListStockSQL(MagicCard mc) {
		return "select * from stocks where collection=? and mcard like '{\"name\":\""+mc.getName().replaceAll("'", "\\\\'")+"\"%'";
	}

	@Override
	public void initDefault() {
		super.initDefault();
		setProperty(SERVERNAME, MTGConstants.DATA_DIR.getAbsolutePath() + "/hsqldao");
		setProperty(DB_NAME, "magicDB");
		setProperty(LOGIN, "SA");
		setProperty(PASS, "");
	}

	
}
