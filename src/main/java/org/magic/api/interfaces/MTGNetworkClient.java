package org.magic.api.interfaces;

import java.awt.Color;
import java.io.IOException;

import org.magic.api.beans.JsonMessage;
import org.magic.api.beans.abstracts.AbstractMessage.MSG_TYPE;
import org.magic.game.model.Player;
import org.magic.game.model.Player.STATUS;

public interface MTGNetworkClient extends MTGPlugin{


	
	JsonMessage consume() throws IOException;

	void switchAddress(String topicName) throws IOException;

	void join(Player p, String url, String topic) throws IOException;

	void sendMessage(String text, Color c,MSG_TYPE type) throws IOException;

	void sendMessage(JsonMessage obj) throws IOException;
	
	void logout() throws IOException;

	void changeStatus(STATUS selectedItem) throws IOException;
	
	boolean isActive();

	void searchStock(JsonMessage s) throws IOException;

	public Player getPlayer();

}