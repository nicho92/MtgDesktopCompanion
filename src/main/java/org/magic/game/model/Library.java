package org.magic.game.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.magic.api.beans.MagicCard;

public class Library implements Serializable {

	private List<MagicCard> cards;

	public List<MagicCard> getCards() {
		return cards;
	}

	public Library(List<MagicCard> cards) {
		this.cards = cards;
	}

	public Library() {
		cards = new ArrayList<>();
	}

	public void setCards(List<MagicCard> cards) {
		this.cards = cards;
	}

	public void remove(MagicCard mc) {
		cards.remove(mc);
	}

	public void add(MagicCard mc) {
		cards.add(mc);
	}

	public void putCardAt(MagicCard mc, int position) {
		cards.set(position, mc);
	}

	public void clear() {
		cards.clear();
	}

	public int size() {
		return cards.size();
	}

	@Override
	public String toString() {
		return "(" + size() + ")";
	}

	public void add(int i, MagicCard mc) {
		cards.add(i, mc);

	}

	public void shuffle() {
		Collections.shuffle(cards);
	}

	public List<MagicCard> subList(int i, int number) {
		return cards.subList(i, number);
	}

}
