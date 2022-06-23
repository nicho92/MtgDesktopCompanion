package org.magic.api.interfaces;

import java.awt.Color;

import org.magic.api.beans.MagicCard;
import org.magic.game.model.Player.STATUS;
import org.utils.patterns.observer.Observer;

public interface MTGNetworkClient {

	public void join();

	public void sendMessage(String text);

	public void sendMessage(String text, Color c);

	public void logout();
	
	public void changeStatus(STATUS selectedItem);

	public boolean isActive();

	public void addObserver(Observer obs);

	public void search(MagicCard mc);

		
}