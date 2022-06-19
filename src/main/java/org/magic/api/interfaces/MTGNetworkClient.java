package org.magic.api.interfaces;

import java.awt.Color;

import org.magic.game.model.Player;
import org.magic.game.model.Player.STATUS;
import org.utils.patterns.observer.Observer;

public interface MTGNetworkClient {

	
	Player getPlayer();

	void join();

	void sendMessage(String text);

		void sendMessage(String text, Color c);

	void logout();
	
	void changeStatus(STATUS selectedItem);

	boolean isActive();

	void addObserver(Observer obs);

		
}