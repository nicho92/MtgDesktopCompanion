package org.magic.api.main;

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
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).init();
		MTGControler.getInstance().getEnabled(MTGDao.class).init();
		MTGControler.getInstance().getPlugin(new JSONHttpServer().getName(), MTGServer.class).start();
		MTGControler.getInstance().getPlugin(new WebManagerServer().getName(), MTGServer.class).start();
	}

}
