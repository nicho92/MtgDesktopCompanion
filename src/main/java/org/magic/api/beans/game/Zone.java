package org.magic.api.beans.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.magic.api.beans.MTGCard;

public class Zone implements Serializable {

	protected static final long serialVersionUID = 1L;
	protected transient List<MTGCard> cards;
	protected ZoneEnum location;

	public Zone(ZoneEnum zone) {
		cards = new ArrayList<>();
		this.location=zone;
	}

	public Zone(List<MTGCard> asList,ZoneEnum zone) {
		this.cards = asList;
		this.location=zone;
	}

	public List<MTGCard> getCards() {
		return cards;
	}

	public void setCards(List<MTGCard> cards) {
		this.cards = cards;
	}

	public void remove(MTGCard mc) {
		cards.remove(mc);
	}

	public void add(MTGCard mc) {
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

		for (MTGCard mc : cards) {
			set.addAll(mc.getTypes());
		}
		return set;
	}


	public void putCardAt(MTGCard mc, int position) {
		cards.set(position, mc);
	}

	@Override
	public String toString() {
		return size() + " (" + getTypesIncludes().size() + " types)";
	}

	public void shuffle() {
		Collections.shuffle(cards);
	}

	public List<MTGCard> subList(int i, int number) {
		return cards.subList(i, number);
	}


	public void add(int i, MTGCard mc) {
		cards.add(i, mc);

	}

	public ZoneEnum getLocation() {
		return location;
	}

}
