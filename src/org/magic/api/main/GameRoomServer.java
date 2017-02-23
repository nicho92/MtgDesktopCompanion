package org.magic.api.main;

import java.io.IOException;

import org.magic.servers.impl.MTGGameRoomServer;

public class GameRoomServer {

	public static void main(String[] args) throws Exception {
		MTGGameRoomServer server = new MTGGameRoomServer();
		server.start();

	}

}
