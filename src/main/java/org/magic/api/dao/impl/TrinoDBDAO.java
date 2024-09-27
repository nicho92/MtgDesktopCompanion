package org.magic.api.dao.impl;

import java.sql.SQLException;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.jooq.SQLDialect;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.abstracts.extra.AbstractMagicSQLDAO;
import org.magic.services.MTGControler;
import org.magic.services.logging.MTGLogger;
import org.magic.services.tools.MTG;

public class TrinoDBDAO extends AbstractMagicSQLDAO {


	public static void main(String[] args) throws SQLException {
		MTGLogger.changeLevel(Level.DEBUG);
		
		MTGControler.getInstance();
		
		MTG.getEnabledPlugin(MTGDao.class).init();
	}
	
	
	@Override
	protected String getdbSizeQuery() {
		return "";
	}

	@Override
	protected SQLDialect getDialect() {
		return SQLDialect.TRINO;
	}

	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	
	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var m = super.getDefaultAttributes();
		m.get(SERVERPORT).setDefaultValue("8080");
		m.get(DB_NAME).setDefaultValue("catalog/schema");
		m.get(LOGIN).setDefaultValue("trino");
		m.get(PASS).setDefaultValue("");
		return m;
	}

	@Override
	public String getName() {
		return "TrinoDB";
	}

	@Override
	protected String getjdbcnamedb() {
		return "trino";
	}
	
}
