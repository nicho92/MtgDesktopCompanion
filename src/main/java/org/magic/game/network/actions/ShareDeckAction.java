package org.magic.game.network.actions;

import org.magic.api.beans.MagicDeck;
import org.magic.game.model.Player;

public class ShareDeckAction extends AbstractNetworkAction {

	Player p;
	Player to;

	private MagicDeck deck;

	public ShareDeckAction(Player p, MagicDeck d, Player to) {
		this.p = p;
		this.deck = d;
		this.to = to;
		setAct(ACTIONS.SHARE);
	}

	public Player getP() {
		return p;
	}

	public void setP(Player p) {
		this.p = p;
	}

	public Player getTo() {
		return to;
	}

	public MagicDeck getDeck() {
		return deck;
	}

	public void setDeck(MagicDeck deck) {
		this.deck = deck;
	}

	@Override
	public String toString() {
		return getP() + " : is sharing " + deck.getName() + " with you";
	}

}
