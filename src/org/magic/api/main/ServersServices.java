package org.magic.api.main;

import java.io.IOException;

import org.magic.api.interfaces.MTGServer;
import org.magic.servers.impl.MTGGameRoomServer;
import org.magic.services.MTGControler;

public class ServersServices {

	public static void main(String[] args) throws Exception {
		
		for(MTGServer s : MTGControler.getInstance().getServers())
			s.start();
	}

}
