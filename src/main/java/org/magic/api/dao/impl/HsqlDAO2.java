package org.magic.api.dao.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.magic.api.interfaces.abstracts.AbstractMagicSQLDAO;
import org.magic.services.MTGConstants;
import org.magic.tools.FileTools;

public class HsqlDAO2 extends AbstractMagicSQLDAO {

	private static final String MODE = "MODE";

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
		return "hsqldb"+(getString(MODE).isEmpty()?"":":"+getString(MODE));
	}
	

	@Override
	protected String getdbSizeQuery() {
		return null;
	}
	
	@Override
	protected boolean isJsonCompatible() {
		return false;
	}

	@Override
	public String getDBLocation() {
		return getString(SERVERNAME);
	}

	@Override
	public long getDBSize() 
	{
		if(getString(MODE).equals("mem"))
			return 0;
		
		if(getFile(SERVERNAME).exists())
			return FileUtils.sizeOfDirectory(getFile(SERVERNAME));
		else
			return 0;
	}


	public String getName() {
		return "hSQLdb2";
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
		return Map.of(SERVERNAME, Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(),"hsqldao").toFile().getAbsolutePath(),
							  LOGIN, "SA",
							  PASS, "",
							  MODE,"file");
		
		
		
	}
}
