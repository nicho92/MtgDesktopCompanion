package test.network;

import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.magic.api.beans.shop.Contact;
import org.magic.servers.impl.JSONHttpServer;
import org.magic.services.JWTServices;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class JWTester {

	@Test
	void testConnect() {

		MTGControler.getInstance().loadAccountsConfiguration();

		var jsonServer = new JSONHttpServer();

		var jwt = new JWTServices(jsonServer.getString("JWT_SECRET"), MTGConstants.MTG_APP_NAME);

		var c = new Contact();

		c.setName("Nicho");
		c.setLastName("Companion");
		c.setEmail("nicolas.pihen@gmail.com");

		var m = new HashMap<String, String>();
		m.put("name", c.getName() + " " + c.getLastName());
		m.put("mail", c.getEmail());

		var toke = jwt.generateToken("nicho", m, 60, false);

		var element = jwt.validateToken(toke);

		System.out.println(element);

	}

}
