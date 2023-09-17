package org.magic.api.interfaces;

import java.awt.Color;
import java.io.IOException;

import org.magic.api.beans.abstracts.AbstractMessage;
import org.magic.api.beans.messages.SearchMessage;
import org.magic.game.model.Player;
import org.magic.game.model.Player.STATUS;

public interface MTGNetworkClient extends MTGPlugin{


	
	AbstractMessage consume() throws IOException;

	void switchAddress(String topicName) throws IOException;

	void join(Player p, String url, String topic) throws IOException;

	void sendMessage(String text, Color c) throws IOException;

	void sendMessage(AbstractMessage obj) throws IOException;
	
	void logout() throws IOException;

	void changeStatus(STATUS selectedItem) throws IOException;
	
	boolean isActive();

	void disableConsummer();
	
	void searchStock(SearchMessage s) throws IOException;

	public Player getPlayer();

}
