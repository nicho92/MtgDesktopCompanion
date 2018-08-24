package org.magic.game.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.magic.api.beans.MagicCard;

public class Graveyard implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<MagicCard> cards;

	public Graveyard() {
		cards = new ArrayList<>();
	}

	public List<MagicCard> getCards() {
		return cards;
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

	public void clear() {
		cards.clear();
	}

	public int size() {
		return cards.size();
	}

	public Set<String> getTypesIncludes() {
		Set<String> set = new LinkedHashSet<>();

		for (MagicCard mc : cards) {
			set.addAll(mc.getTypes());
		}
		return set;
	}

	@Override
	public String toString() {
		return size() + " (" + getTypesIncludes().size() + " types)";
	}

}
