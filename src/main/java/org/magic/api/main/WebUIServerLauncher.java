package org.magic.api.main;

import static org.magic.tools.MTG.getEnabledPlugin;
import static org.magic.tools.MTG.getPlugin;

import java.io.IOException;
import java.sql.SQLException;

import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGServer;
import org.magic.servers.impl.JSONHttpServer;
import org.magic.servers.impl.WebManagerServer;
import org.magic.services.MTGControler;

public class WebUIServerLauncher {

	public static void main(String[] args) throws SQLException, IOException {
		MTGControler.getInstance();
		getEnabledPlugin(MTGCardsProvider.class).init();
		getEnabledPlugin(MTGDao.class).init();
		getPlugin(new JSONHttpServer().getName(), MTGServer.class).start();
		getPlugin(new WebManagerServer().getName(), MTGServer.class).start();
	}

}
