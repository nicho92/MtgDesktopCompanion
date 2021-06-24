package org.magic.api.dao.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.Grading;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.interfaces.abstracts.AbstractMagicSQLDAO;
import org.magic.services.MTGConstants;
import org.magic.tools.FileTools;

public class SQLLiteDAO extends AbstractMagicSQLDAO {

	@Override
	public long getDBSize() {
		return FileUtils.sizeOf(getFile(SERVERNAME));
	}

	@Override
	public void backup(File dir) throws IOException {
		FileTools.zip(getFile(SERVERNAME), new File(dir, "backup.zip"));
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
	protected String getdbSizeQuery() {
		return null;
	}

	@Override
	public String getName() {
		return "SQLite";
	}

	@Override
	protected String getAutoIncrementKeyWord() {
		return "INTEGER";// primary key column is autoincrement
	}

	@Override
	protected String getjdbcnamedb() {
		return getName().toLowerCase();
	}

	@Override
	protected String beanStorage() {
		return "json";
	}
	
	@Override
	protected List<MagicCardStock> readTransactionItems(ResultSet rs) throws SQLException {
		return serialiser.fromJsonList(rs.getObject("stocksItem").toString(), MagicCardStock.class);
	}
	
	@Override
	protected void storeTransactionItems(PreparedStatement pst, int position, List<MagicCardStock> grd) throws SQLException {
		pst.setString(position, serialiser.toJsonElement(grd).toString());
		
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
	protected String createListStockSQL() {
		return "select * from stocks where collection=? and JSON_EXTRACT(mcard,'$.name')=?";

	}
	
}
