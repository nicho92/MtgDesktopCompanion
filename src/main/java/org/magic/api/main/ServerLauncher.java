package org.magic.api.main;

import static org.magic.tools.MTG.getEnabledPlugin;
import static org.magic.tools.MTG.getPlugin;

import java.io.IOException;
import java.sql.SQLException;

import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGServer;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class ServerLauncher {
	public static void main(String[] args) throws SQLException, IOException 
	{
		MTGControler.getInstance().loadAccountsConfiguration();
		if(args.length==0)
		{
			MTGLogger.getLogger(ServerLauncher.class).info("Usage : ServerLauncher <server name>");
			System.exit(-1);
		}
		
		MTGServer serv = getPlugin(args[0], MTGServer.class);
		if(!serv.isEnable())
		{
			MTGLogger.getLogger(ServerLauncher.class).error(args[0] + " is not enabled");
			System.exit(-1);
		}
	
		
		getEnabledPlugin(MTGCardsProvider.class).init();
		getEnabledPlugin(MTGDao.class).init();
		
		serv.start();
		
		
	}
}
