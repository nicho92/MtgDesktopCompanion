package org.magic.main;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.sql.SQLException;

import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.game.gui.components.NetworkChatPanel;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGControler;
import org.magic.services.threads.MTGRunnable;
import org.magic.services.threads.ThreadManager;

public class NetworkClient {

	public static void main(String[] args) throws SQLException {
		MTGControler.getInstance();

		getEnabledPlugin(MTGCardsProvider.class).init();
		getEnabledPlugin(MTGDao.class).init();
		ThreadManager.getInstance().invokeLater(new MTGRunnable() {

			@Override
			protected void auditedRun() {
				MTGUIComponent.createJFrame(new NetworkChatPanel(), true, true).setVisible(true);

			}
		},"Loading Network Client");
	}

}
