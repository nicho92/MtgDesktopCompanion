package org.magic.services;

import org.magic.api.interfaces.MTGNetworkClient;
import org.magic.api.network.impl.ActiveMQNetworkClient;
import org.magic.services.tools.MTG;

public class OnlineService {

	private MTGNetworkClient client;
	private static OnlineService inst;
	
	
	private OnlineService() {
		 client = new ActiveMQNetworkClient();
		 
			
			if(MTG.readPropertyAsBoolean("online-query"))
			{
				
			}
		 
	}
	
	public static OnlineService inst()
	{
		if(inst ==null)
			inst= new OnlineService();
		
		return inst;
	}

}
