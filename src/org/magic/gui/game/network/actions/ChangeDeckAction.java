package org.magic.gui.game.network.actions;

import org.magic.api.beans.MagicDeck;
import org.magic.game.Player;

public class ChangeDeckAction extends AbstractGamingAction {

	
	Player player;
	MagicDeck deck;
	
	public ChangeDeckAction(Player p,MagicDeck d) {
		setAct(ACTIONS.CHANGE_DECK);
		this.player=p;
		this.deck=d;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public MagicDeck getDeck() {
		return deck;
	}

	public void setDeck(MagicDeck deck) {
		this.deck = deck;
	}
	
	
	
}
