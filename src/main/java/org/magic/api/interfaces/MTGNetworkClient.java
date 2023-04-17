package org.magic.api.interfaces;

import java.awt.Color;
import java.io.IOException;

import org.magic.api.beans.JsonMessage;
import org.magic.game.model.Player;
import org.magic.game.model.Player.STATUS;

public interface MTGNetworkClient {


	
	JsonMessage consume() throws IOException;

	void switchAddress(String topicName) throws IOException;

	void join(Player p, String url, String topic) throws IOException;

	void sendMessage(String text, Color c) throws IOException;

	void sendMessage(JsonMessage obj) throws IOException;
	
	void logout() throws IOException;

	void changeStatus(STATUS selectedItem) throws IOException;
	
	boolean isActive();



}