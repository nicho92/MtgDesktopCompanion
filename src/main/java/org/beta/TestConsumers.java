package org.beta;

import java.io.IOException;

import org.magic.api.network.impl.ActiveMQNetworkClient;
import org.magic.game.model.Player;
import org.magic.services.network.URLTools;

public class TestConsumers {

	private static final String TCP_ADDRESS = "tcp://mtgcompanion.me:61616";
	private static final String WELCOME = "welcome";

	public static void main(String[] args) throws IOException {
		var client1 = new ActiveMQNetworkClient();
			client1.join(new Player("Totof"), TCP_ADDRESS, WELCOME);
			
		var client2 = new ActiveMQNetworkClient();
			client2.join(new Player("Tataf"), TCP_ADDRESS, WELCOME);
			
		var client3 = new ActiveMQNetworkClient();
			client3.join(new Player("Tutuf"), TCP_ADDRESS, WELCOME);
		
			runThreadFor(client1);
			runThreadFor(client2);
			runThreadFor(client3);
	}

	private static void runThreadFor(ActiveMQNetworkClient client) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
				//	client.sendMessage("Coucou de "+client.getPlayer(),Color.red);
					
					while(client.isActive())
						System.out.println(client.getPlayer() +" receive"+  URLTools.toJson(client.consume()).getAsJsonObject().get("message").getAsString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}).start();
		
		
	}

}
