package org.magic.api.dao.impl;

import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.jooq.SQLDialect;
import org.magic.api.interfaces.abstracts.extra.AbstractMagicSQLDAO;
import org.magic.services.MTGConstants;
import org.magic.services.tools.FileTools;

public class SQLLiteDAO extends AbstractMagicSQLDAO {

	@Override
	public Map<String,Long> getDBSize() {

		var map = new HashMap<String,Long>();
			map.put("file",FileTools.sizeOf(getFile(SERVERNAME)));
			return map;
	}

	@Override
	public Map<String, String> getDefaultAttributes() {

		var m = super.getDefaultAttributes();

		m.put(SERVERNAME, Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(),"mtgcompanion.sqlite").toFile().getAbsolutePath());
		m.put(LOGIN, "SA");
		m.put(DB_NAME, "");

		return m;
	}

	@Override
	protected int getGeneratedKey(PreparedStatement pst) {
		try (var c = pool.getConnection(); var ps = c.prepareStatement("select last_insert_rowid();"))
		{
			var rs = ps.executeQuery();
			return rs.getInt(1);
			
			
		} catch (SQLException e) {
			logger.error("error getting last id {}",e.getMessage());
			return -1;
		}
	}
	
	@Override
	protected String getdbSizeQuery() {
		return null;
	}


	@Override
	protected SQLDialect getDialect() {
		return SQLDialect.SQLITE;
	}

	
	@Override
	public String getName() {
		return "SQLite";
	}


	@Override
	protected String getjdbcnamedb() {
		return getName().toLowerCase();
	}

}
