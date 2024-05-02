package org.magic.api.dao.impl;

import java.sql.SQLException;
import java.util.Map;

import org.jooq.SQLDialect;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.abstracts.extra.AbstractMagicSQLDAO;
import org.magic.services.MTGControler;
import org.magic.services.tools.MTG;

public class FirebirdDAO extends AbstractMagicSQLDAO {

	@Override
	public String getName() {
		return "Firebird";
	}

	@Override
	protected String getjdbcnamedb() {
		return "firebird";
	}

	@Override
	protected String getdbSizeQuery() {
		return "";
	}

	@Override
	protected SQLDialect getDialect() {
		return SQLDialect.FIREBIRD;
	}

	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	
	@Override
	public Map<String, String> getDefaultAttributes() {
		var m = super.getDefaultAttributes();
			m.put(SERVERPORT, "3050");
			m.put(LOGIN, "SYSDBA");
			
		return m;
	}
	
	public static void main(String[] args) throws SQLException {
		MTGControler.getInstance();
		MTG.getEnabledPlugin(MTGDao.class).init();
	}
	
	
}
