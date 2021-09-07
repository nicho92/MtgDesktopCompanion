package org.beta;

import java.io.IOException;
import java.sql.SQLException;

import org.magic.api.beans.MagicCollection;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.components.dialog.AdvancedSearchQueryDialog;
import org.magic.services.MTGControler;
import org.magic.tools.MTG;

public class QueryableTest {

	public static void main(String[] args) throws SQLException {
		
		MTGControler.getInstance();
		MTG.getEnabledPlugin(MTGCardsProvider.class).init();
		MTG.getEnabledPlugin(MTGDao.class).init();
		
		var diag = new AdvancedSearchQueryDialog();
		diag.setVisible(true);
		
		try {
			MTG.getEnabledPlugin(MTGDao.class).searchByCriteria(diag.getCollection(), diag.getCrits()).forEach(mc-> {
				System.out.print(mc);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
}
