package org.magic.api.dao.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.Grading;
import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.abstracts.AbstractSQLMagicDAO;
import org.magic.services.MTGConstants;
import org.magic.tools.FileTools;

public class HsqlDAO2 extends AbstractSQLMagicDAO {

	private static final String MODE = "MODE";

	@Override
	protected boolean enablePooling() {
		return !getString(MODE).equals("file");
	}
	
	@Override
	protected String getAutoIncrementKeyWord() {
		return "IDENTITY";
	}
	
	@Override
	protected String beanStorage() {
		return "LONGVARCHAR";
	}
	
	@Override
	protected String getjdbcnamedb() {
		return "hsqldb"+(getString(MODE).isEmpty()?"":":"+getString(MODE));
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
	protected Grading readGrading(ResultSet rs) throws SQLException {
		return serialiser.fromJson(rs.getString("grading"), Grading.class);
	}
	
	@Override
	protected void storeGrade(PreparedStatement pst, int position, Grading grd) throws SQLException {
		pst.setString(position, serialiser.toJsonElement(grd).toString());
	}
	

	@Override
	protected Map<String, String> readTiersApps(ResultSet rs) throws SQLException {
		return serialiser.fromJson(rs.getString("tiersAppIds"), Map.class);
	}
	
	@Override
	protected void storeTiersApps(PreparedStatement pst, int i, Map<String, String> tiersAppIds) throws SQLException {
		pst.setString(i, serialiser.toJsonElement(tiersAppIds).toString());
	}
	

	
	@Override
	protected String getdbSizeQuery() {
		return null;
	}
	
	@Override
	protected boolean isJsonCompatible()
	{
		return false;
	}

	@Override
	public String getDBLocation() {
		return getString(SERVERNAME);
	}

	@Override
	public long getDBSize() {
		
		if(getString(MODE).equals("mem"))
			return 0;
		
		if(getFile(SERVERNAME).exists())
			return FileUtils.sizeOfDirectory(getFile(SERVERNAME));
		else
			return 0;
		
		
	}


	public String getName() {
		return "hSQLdb2";
	}

	@Override
	public void backup(File dir) throws IOException {
		FileTools.zip(getFile(SERVERNAME), new File(dir, "backup.zip"));
	}

	@Override
	public String createListStockSQL() {
		return "select * from stocks where collection=? and mcard like ?";
	}

	@Override
	public void initDefault() {
		super.initDefault();
		setProperty(SERVERNAME, Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(),"hsqldao").toFile().getAbsolutePath());
		setProperty(LOGIN, "SA");
		setProperty(PASS, "");
		setProperty(MODE,"file");
	}
	
}
