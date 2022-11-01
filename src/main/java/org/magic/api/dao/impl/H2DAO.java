package org.magic.api.dao.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.MTGDocumentation;
import org.magic.api.beans.MTGNotification.FORMAT_NOTIFICATION;
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
	protected String longTextStorage() {
		return "LONGVARCHAR";
	}

	@Override
	protected String getjdbcnamedb() {
		return "h2"+(getString(MODE).isEmpty()?"":":"+getString(MODE));
	}

	@Override
	protected boolean isJsonCompatible()
	{
		return false;
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
			map.put("file",FileUtils.sizeOfDirectory(getFile(SERVERNAME)));


		return map;
	}

	@Override
	public String getName() {
		return "h2";
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
