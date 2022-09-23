package org.magic.game.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.magic.api.beans.MagicCard;

public class Zone implements Serializable {

	protected static final long serialVersionUID = 1L;
	protected transient List<MagicCard> cards;
	protected ZoneEnum location;

	public Zone(ZoneEnum zone) {
		cards = new ArrayList<>();
		this.location=zone;
	}

	public Zone(List<MagicCard> asList,ZoneEnum zone) {
		this.cards = asList;
		this.location=zone;
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


	public void putCardAt(MagicCard mc, int position) {
		cards.set(position, mc);
	}

	@Override
	public String toString() {
		return size() + " (" + getTypesIncludes().size() + " types)";
	}

	public void shuffle() {
		Collections.shuffle(cards);
	}

	public List<MagicCard> subList(int i, int number) {
		return cards.subList(i, number);
	}


	public void add(int i, MagicCard mc) {
		cards.add(i, mc);

	}

	public ZoneEnum getLocation() {
		return location;
	}

}
