package org.magic.api.main;

import java.io.IOException;
import java.sql.SQLException;

import org.magic.api.interfaces.MTGServer;
import org.magic.services.MTGControler;
import org.mozilla.javascript.GeneratedClassLoader;

public class ServerLauncher {

	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		
		if(args.length==0)
		{
			System.out.println("Usage : ServerLauncher <server name>");
			System.exit(-1);
		}
		
		MTGControler.getInstance().getEnabledCardsProviders().init();
		MTGControler.getInstance().getEnabledDAO().init();
		MTGControler.getInstance().getPlugin(args[0], MTGServer.class).start();
	}
}
