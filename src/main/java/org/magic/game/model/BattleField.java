package org.magic.game.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicCard;

public class BattleField implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<MagicCard> cards;

	public List<MagicCard> getCards() {
		return cards;
	}

	public void setCards(List<MagicCard> cards) {
		this.cards = cards;
	}

	public BattleField() {
		cards = new ArrayList<>();
	}

	public void add(MagicCard mc) {
		cards.add(mc);
	}

	public void remove(MagicCard mc) {
		cards.remove(mc);
	}

	public int size() {
		return cards.size();
	}

	public void removeAll() {
		cards.clear();

	}

	@Override
	public String toString() {
		return "BattleFieldPanel";
	}

}
