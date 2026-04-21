package org.magic.main;

import java.sql.SQLException;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.network.NetworkChatPanel;
import org.magic.services.MTGControler;
import org.magic.services.threads.MTGRunnable;
import org.magic.services.threads.ThreadManager;

public class NetworkClient {

	public static void main(String[] args) throws SQLException {
		MTGControler.getInstance().init();
		ThreadManager.getInstance().invokeLater(new MTGRunnable() {

			@Override
			protected void auditedRun() {
				MTGUIComponent.createJFrame(new NetworkChatPanel(), true, true).setVisible(true);

			}
		}, "Loading Network Client");
	}

}
