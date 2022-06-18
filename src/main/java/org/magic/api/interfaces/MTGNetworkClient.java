package org.magic.api.interfaces;

import java.awt.Color;

import org.magic.game.model.Player;
import org.magic.game.model.Player.STATE;
import org.utils.patterns.observer.Observer;

public interface MTGNetworkClient {

	
	Player getPlayer();

	void join();

	void sendMessage(String text);

		void sendMessage(String text, Color c);

	void logout();
	
	void changeStatus(STATE selectedItem);

	boolean isActive();

	void addObserver(Observer obs);

		
}