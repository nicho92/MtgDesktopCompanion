package org.magic.api.main;

import java.io.IOException;
import java.sql.SQLException;

import org.magic.api.interfaces.MTGServer;
import org.magic.services.MTGControler;

public class ServerLauncher {

	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		MTGControler.getInstance().getEnabledCardsProviders().init();
		MTGControler.getInstance().getEnabledDAO().init();
		
		MTGControler.getInstance().getPlugin("console", MTGServer.class).start();
	}
}
