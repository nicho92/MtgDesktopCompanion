package org.magic.api.main;

import java.sql.SQLException;

import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.ScriptPanel;
import org.magic.services.MTGControler;
import org.magic.services.threads.ThreadManager;

public class ScriptsUIClient {

	public static void main(String[] args) throws SQLException {
		
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).init();
		MTGControler.getInstance().getEnabled(MTGDao.class).init();
		
		ThreadManager.getInstance().invokeLater(() -> MTGUIComponent.createJFrame(new ScriptPanel(), true, false,true).setVisible(true));
	}
	
}
