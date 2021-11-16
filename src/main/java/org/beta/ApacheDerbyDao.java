package org.beta;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import org.magic.api.interfaces.abstracts.AbstractMagicSQLDAO;
import org.magic.services.MTGControler;

public class ApacheDerbyDao extends AbstractMagicSQLDAO {

	@Override
	public void backup(File dir) throws SQLException, IOException {
		// TODO Auto-generated method stub
	}

	@Override
	public String getName() {
		return "Derby";
	}

	@Override
	protected String getAutoIncrementKeyWord() {
		return "AUTO INCREMENT";
	}

	@Override
	protected String getjdbcnamedb() {
		return "derby";
	}

	@Override
	protected String beanStorage() {
		return "TEXT";
	}

	@Override
	protected String longTextStorage() {
		return "TEXT";
	}

	@Override
	protected String createListStockSQL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getdbSizeQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public Map<String, String> getDefaultAttributes() {
			var m = super.getDefaultAttributes();
		m.put(SERVERPORT, "1527");
		
		return m;
	}
	

	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		MTGControler.getInstance().loadAccountsConfiguration();
		var dao = new ApacheDerbyDao();
		dao.init();
	}

}
