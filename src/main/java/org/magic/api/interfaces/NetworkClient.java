package org.magic.api.interfaces;

import java.awt.Color;

import org.magic.api.beans.MagicDeck;
import org.magic.game.model.Player;
import org.magic.game.model.Player.STATE;
import org.magic.game.network.actions.ReponseAction.CHOICE;
import org.magic.game.network.actions.RequestPlayAction;
import org.utils.patterns.observer.Observer;

public interface NetworkClient {

	
	Player getPlayer();

	void join();

	void updateDeck(MagicDeck d);

	void sendMessage(String text);

	void sendDeck(MagicDeck d, Player to);

	void sendMessage(String text, Color c);

	void logout();

	void requestPlay(Player otherplayer);

	void reponse(RequestPlayAction pa, CHOICE c);

	void changeStatus(STATE selectedItem);

	boolean isActive();

	void addObserver(Observer obs);

		
}