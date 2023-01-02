package org.magic.api.main;

import static org.magic.services.tools.MTG.getEnabledPlugin;
import static org.magic.services.tools.MTG.getPlugin;

import java.io.IOException;
import java.sql.SQLException;

import org.magic.api.interfaces.MTGCardsIndexer;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGServer;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.TechnicalMonitorPanel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.logging.MTGLogger;
import org.magic.services.threads.MTGRunnable;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.MTG;

public class ServerLauncher {
	public static void main(String[] args) throws SQLException, IOException
	{
		MTGControler.getInstance().loadAccountsConfiguration();
		if(args.length==0)
		{
			MTGLogger.getLogger(ServerLauncher.class).info("Usage : ServerLauncher <server name> [,<other server]");
			System.exit(-1);
		}


		getEnabledPlugin(MTGCardsProvider.class).init();
		getEnabledPlugin(MTGDao.class).init();


		try {
			MTG.getEnabledPlugin(MTGCardsIndexer.class).initIndex(false);
		} catch (IOException e) {
			//do nothing
		}





		var arg = args[0];

		if(arg.indexOf(',')>-1) {
			for(String s : arg.split(","))
				preparing(s).start();
		}
		else
		{
			preparing(arg).start();
		}


		if(MTGConstants.IS_GRAPHICAL_UI)
		{
			ThreadManager.getInstance().invokeLater(new MTGRunnable() {
				@Override
				protected void auditedRun() {
					MTGUIComponent.createJDialog(new TechnicalMonitorPanel(), true, false).setVisible(true);
				}
			},"running server console");
		}
	}


	private static MTGServer preparing(String servername)
	{


		MTGServer serv = getPlugin(servername, MTGServer.class);
		if(!serv.isEnable())
		{
			MTGLogger.getLogger(ServerLauncher.class).error("{} is not enabled",servername);
			System.exit(-1);
		}
		return serv;

	}

}
