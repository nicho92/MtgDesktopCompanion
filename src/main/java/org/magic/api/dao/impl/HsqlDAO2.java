package org.magic.api.dao.impl;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.jooq.SQLDialect;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.extra.AbstractMagicSQLDAO;
import org.magic.services.MTGConstants;
import org.magic.services.tools.FileTools;

public class HsqlDAO2 extends AbstractMagicSQLDAO {

	private static final String MODE = "MODE";

	@Override
	protected boolean enablePooling() {
		return getString(MODE).equals("file");
	}


	@Override
	protected SQLDialect getDialect() {
		return SQLDialect.HSQLDB;
	}


	@Override
	protected String getjdbcnamedb() {
		return "hsqldb"+(getString(MODE).isEmpty()?"":":"+getString(MODE));
	}


	@Override
	protected String getdbSizeQuery() {
		return null;
	}

	@Override
	public String getDBLocation() {
		return getString(SERVERNAME);
	}

	@Override
	public Map<String,Long> getDBSize()
	{
		var map = new HashMap<String,Long>();


		if(getString(MODE).equals("mem"))
			map.put("mem", 0L);

		if(getFile(SERVERNAME).exists())
			map.put("file",FileTools.sizeOfDirectory(getFile(SERVERNAME)));


		return map;

	}


	@Override
	public String getName() {
		return "hSQLdb2";
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {

		var m = super.getDefaultAttributes();
		m.get(SERVERNAME).setDefaultValue(Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(),"hsqldao").toFile().getAbsolutePath());
		m.get(LOGIN).setDefaultValue("SA");
		m.put(MODE, new MTGProperty("file", "select storage mode. File will persiste data on drive, mem will store data in memory", "file","mem"));
		m.get(PARAMS).setDefaultValue(";sql.lowercase_ident=true");
		return m;



	}

}
