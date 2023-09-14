package test;

import java.io.IOException;

import org.magic.api.network.impl.ActiveMQNetworkClient;
import org.magic.game.model.Player;

public class TestConsumers {

	private static final String TCP_ADDRESS = "tcp://localhost:61616";
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
		new Thread(()->{
				try {
					while(client.isActive())
						System.out.println(client.getPlayer() +" receive"+  client.consume());
				} catch (IOException e) {
					e.printStackTrace();
				}
		}).start();
		
		
	}

}
