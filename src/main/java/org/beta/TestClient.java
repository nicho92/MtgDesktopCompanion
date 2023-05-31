package org.beta;

import java.io.IOException;

import org.magic.api.network.impl.ActiveMQNetworkClient;
import org.magic.game.model.Player;
import org.magic.services.tools.MTG;

public class TestClient {

	
	public static void main(String[] args) throws IOException {
		var client = new ActiveMQNetworkClient();
		
		client.join(new Player("Nicho2"),  "tcp://mtgcompanion.me:61616", "welcome");
	}
}
