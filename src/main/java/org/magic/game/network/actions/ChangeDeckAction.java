package org.magic.game.network.actions;

import org.magic.api.beans.MagicDeck;
import org.magic.game.model.Player;

public class ChangeDeckAction extends AbstractNetworkAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Player player;
	MagicDeck deck;

	public ChangeDeckAction(Player p, MagicDeck d) {
		setAct(ACTIONS.CHANGE_DECK);
		this.player = p;
		this.deck = d;
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

	@Override
	public String toString() {
		return getPlayer() + " change his deck with " + getDeck();
	}

}
