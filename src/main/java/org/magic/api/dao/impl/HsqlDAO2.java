package org.magic.api.dao.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.abstracts.AbstractSQLMagicDAO;
import org.magic.services.MTGConstants;
import org.magic.tools.FileTools;

public class HsqlDAO2 extends AbstractSQLMagicDAO {

	@Override
	protected String getAutoIncrementKeyWord() {
		return "IDENTITY";
	}
	
	@Override
	protected String cardStorage() {
		return "LONGVARCHAR";
	}
	
	@Override
	protected String getjdbcnamedb() {
		return "hsqldb";
	}
	
	@Override
	protected MagicCard readCard(ResultSet rs) throws SQLException {
		return serialiser.fromJson( rs.getObject("mcard").toString(), MagicCard.class);
	}
	
	@Override
	protected void storeCard(PreparedStatement pst, int position, MagicCard mc) throws SQLException {
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
		FileTools.zip(getFile(SERVERNAME), new File(dir, "backup.zip"));
	}

	@Override
	public String createListStockSQL(MagicCard mc) {
		return "select * from stocks where collection=? and mcard like '{\"name\":\""+mc.getName().replaceAll("'", "\\\\'")+"\"%'";
	}

	@Override
	public void initDefault() {
		super.initDefault();
		setProperty(SERVERNAME, Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(),"hsqldao").toFile().getAbsolutePath());
		setProperty(LOGIN, "SA");
		setProperty(PASS, "");
	}
	
}
