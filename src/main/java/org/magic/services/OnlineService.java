package org.magic.services;

import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.magic.api.interfaces.MTGNetworkClient;
import org.magic.api.network.impl.ActiveMQNetworkClient;
import org.magic.servers.impl.ActiveMQServer;
import org.magic.services.logging.MTGLogger;
import org.magic.services.tools.MTG;

public class OnlineService {

	private MTGNetworkClient client;
	private static OnlineService inst;
	   private Logger logger = MTGLogger.getLogger(OnlineService.class);

	
	private OnlineService() {
		 client = new ActiveMQNetworkClient();
		 
			
			if(MTG.readPropertyAsBoolean("network-config/online-query"))
			{
				var serv =MTGControler.getInstance().get("network-last-server", "tcp://mtgcompanion.me:61616");
				try {
					client.join(MTGControler.getInstance().getProfilPlayer(),
								serv,
								ActiveMQServer.DEFAULT_ADDRESS);
					logger.info("Connected to {}",serv);
				} catch (IOException e) {
					logger.error(e);
				}
			}
		 
	}
	
	public static OnlineService inst()
	{
		if(inst ==null)
			inst= new OnlineService();
		
		return inst;
	}
	
	public MTGNetworkClient getClient() {
		return client;
	}
	

}
