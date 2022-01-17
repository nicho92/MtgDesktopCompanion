package org.magic.api.dao.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.magic.api.interfaces.abstracts.extra.AbstractMagicSQLDAO;
import org.magic.services.MTGConstants;
import org.magic.tools.FileTools;

public class SQLLiteDAO extends AbstractMagicSQLDAO {

	@Override
	public Map<String,Long> getDBSize() {
		
		var map = new HashMap<String,Long>();
			map.put("file",FileUtils.sizeOf(getFile(SERVERNAME)));
			return map;
	}

	@Override
	public void backup(File dir) throws IOException {
		FileTools.zip(getFile(SERVERNAME), new File(dir, "backup.zip"));
	}
	
	
	
	@Override
	public Map<String, String> getDefaultAttributes() {
		
		var m = super.getDefaultAttributes();
		
		m.put(SERVERNAME, Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(),"sqlite-db").toFile().getAbsolutePath());
		m.put(LOGIN, "SA");
		m.put(DB_NAME, "");
		
		return m;
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
	protected String longTextStorage() {
		return "TEXT";
	}
	
	@Override
	protected String createListStockSQL() {
		return "select * from stocks where collection=? and JSON_EXTRACT(mcard,'$.name')=?";

	}
	
}
