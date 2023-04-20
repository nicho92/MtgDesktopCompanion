package org.magic.main;

import java.sql.SQLException;

import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.services.MTGControler;
import org.magic.services.tools.MTG;

public class DBTester {

	public static void main(String[] args) throws SQLException {
		MTGControler.getInstance();
		MTG.getEnabledPlugin(MTGDao.class).init();

	}

}
