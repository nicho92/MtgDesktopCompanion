package org.magic.api.dao.impl;

import java.util.Map;

import org.jooq.SQLDialect;
import org.magic.api.interfaces.abstracts.extra.AbstractMagicSQLDAO;

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
		return " SELECT RDB$RELATION_NAME, 1 FROM rdb$relations WHERE RDB$FLAGS = 1;";
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
}
