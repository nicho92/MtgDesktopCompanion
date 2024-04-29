package org.magic.api.dao.impl;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.jooq.SQLDialect;
import org.magic.api.beans.technical.MTGDocumentation;
import org.magic.api.beans.technical.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.interfaces.abstracts.extra.AbstractMagicSQLDAO;
import org.magic.services.MTGConstants;
import org.magic.services.tools.FileTools;

public class H2DAO extends AbstractMagicSQLDAO {

	private static final String MODE = "MODE";

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	
	@Override
	protected SQLDialect getDialect() {
		return SQLDialect.H2;
	}

	@Override
	protected boolean enablePooling() {
		return !getString(MODE).equals("file");
	}

	@Override
	protected String getjdbcUrl() {
		return "jdbc:"+getjdbcnamedb()+(getString(MODE).equals("file")?"/":":")+getString(DB_NAME)+";CASE_INSENSITIVE_IDENTIFIERS=TRUE"+(getString(MODE).equals("mem")?";DB_CLOSE_DELAY=-1":"");
	}
	
	@Override
	protected String getjdbcnamedb() {
		return "h2:"+getString(MODE)+ (getString(MODE).equals("file")?":"+getString(SERVERNAME):"");
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
	public Map<String,Long> getDBSize() {
		var map = new HashMap<String,Long>();


		if(getString(MODE).equals("mem"))
			map.put("mem", 0L);

		if(getFile(SERVERNAME).exists())
			map.put("file",FileTools.sizeOfDirectory(getFile(SERVERNAME)));


		return map;
	}

	@Override
	public String getName() {
		return "h2";
	}


	@Override
	public Map<String, String> getDefaultAttributes() {
		var m = super.getDefaultAttributes();

		m.put(SERVERNAME, Paths.get(MTGConstants.DATA_DIR.getAbsolutePath()).toFile().getAbsolutePath());
		m.put(LOGIN, "SA");
		m.put(MODE,"file");

		return m;
	}

	@Override
	public MTGDocumentation getDocumentation() {
		return new MTGDocumentation("https://h2database.com/html/tutorial.html#connecting_using_jdbc",FORMAT_NOTIFICATION.HTML);
	}


}
