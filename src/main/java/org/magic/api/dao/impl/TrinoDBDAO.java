package org.magic.api.dao.impl;

import java.sql.Statement;
import java.util.Map;

import org.jooq.SQLDialect;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.extra.AbstractMagicSQLDAO;

public class TrinoDBDAO extends AbstractMagicSQLDAO {
	
	public TrinoDBDAO() {
		generatedKey = Statement.NO_GENERATED_KEYS;
	}
	
	@Override
	protected boolean enablePooling() {
		return false;
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
