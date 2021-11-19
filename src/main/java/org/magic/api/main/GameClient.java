package org.magic.api.main;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.sql.SQLException;

import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.GameGUI;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGControler;
import org.magic.services.threads.MTGRunnable;
import org.magic.services.threads.ThreadManager;

public class GameClient {

	public static void main(String[] args) throws SQLException {
		MTGControler.getInstance();
		
		getEnabledPlugin(MTGCardsProvider.class).init();
		getEnabledPlugin(MTGDao.class).init();
		ThreadManager.getInstance().invokeLater(new MTGRunnable() {
			
			@Override
			protected void auditedRun() {
				MTGUIComponent.createJFrame(new GameGUI(), true, true).setVisible(true);
				
			}
		},"Loading Game Client");
	}
	
}
