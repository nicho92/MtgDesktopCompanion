package org.magic.api.main;

import java.io.IOException;
import java.sql.SQLException;

import org.magic.api.interfaces.MTGServer;
import org.magic.servers.impl.JSONHttpServer;
import org.magic.servers.impl.WebManagerServer;
import org.magic.services.MTGControler;

public class WebUIServerLauncher {

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		MTGControler.getInstance().getEnabledCardsProviders().init();
		MTGControler.getInstance().getEnabledDAO().init();
		
		MTGControler.getInstance().getPlugin(new JSONHttpServer().getName(), MTGServer.class).start();
		MTGControler.getInstance().getPlugin(new WebManagerServer().getName(), MTGServer.class).start();
	}

}
