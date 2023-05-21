package org.magic.services.network;

import java.io.IOException;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.Logger;
import org.magic.api.interfaces.MTGNetworkClient;
import org.magic.api.network.impl.ActiveMQNetworkClient;
import org.magic.servers.impl.ActiveMQServer;
import org.magic.services.MTGControler;
import org.magic.services.logging.MTGLogger;
import org.magic.services.threads.MTGRunnable;
import org.magic.services.tools.MTG;


public class OnlineService  {

	
	private MTGNetworkClient client;
	private final Logger logger = MTGLogger.getLogger(this.getClass());
	private static OnlineService inst;
	
	
	public static OnlineService inst()
	{
		if(inst ==null)
			inst=new OnlineService();
		
		return inst;
	}
	
	private OnlineService() {
		client = new ActiveMQNetworkClient();
		
		if(MTG.readPropertyAsBoolean("network-config/online-query"))
			try {
				connect();
			} catch (IOException e) {
				logger.error(e);
			}
		
	}
	
	public MTGNetworkClient getClient() {
		return client;
	}
	
	public void connect() throws IOException
	{
		client.join(MTGControler.getInstance().getProfilPlayer(), 
				  MTGControler.getInstance().get("network-config/network-last-server", "tcp://mtgcompanion.me:61616"), 
				  ActiveMQServer.DEFAULT_ADDRESS);

				Executors.newFixedThreadPool(1).execute(new MTGRunnable() {
					@Override
					protected void auditedRun() {
						while(client.isActive())
						{
							
							try {
								var msg = client.consume();
								
							} catch (Exception e) {
								logger.error(e);
							}
						}
					}
				});
	}
	
	

}
