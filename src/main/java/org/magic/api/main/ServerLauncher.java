package org.magic.api.main;

import java.io.IOException;
import java.sql.SQLException;

import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGServer;
import org.magic.services.MTGControler;

public class ServerLauncher {

	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		
		if(args.length==0)
		{
			System.out.println("Usage : ServerLauncher <server name>");
			System.exit(-1);
		}
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).init();
		MTGControler.getInstance().getEnabled(MTGDao.class).init();
		MTGControler.getInstance().getPlugin(args[0], MTGServer.class).start();
	}
}
