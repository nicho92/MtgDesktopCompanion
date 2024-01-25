package org.magic.api.beans;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.magic.api.beans.enums.EnumColors;
import org.magic.api.interfaces.MTGComboProvider;

public class MTGCombo {

	private List<MTGCard> cards;
	private String name;
	private String comment;
	private MTGComboProvider plugin;


	public void setPlugin(MTGComboProvider plugin) {
		this.plugin = plugin;
	}

	public MTGComboProvider getPlugin() {
		return plugin;
	}

	public List<MTGCard> getCards() {
		return cards;
	}
	public void setCards(List<MTGCard> cards) {
		this.cards = cards;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}

	public MTGCombo() {
		cards = new ArrayList<>();
	}

	public String getColors() {
		Set<String> cmap = new LinkedHashSet<>();
		for (MTGCard mc : getCards()) {
			if ((mc.getCmc() != null))
				for (EnumColors c : mc.getColors())
					cmap.add(c.toManaCode());
		}
		return cmap.toString();
	}


	@Override
	public String toString() {
		return getName();
	}
	public void addCard(MTGCard card) {
		cards.add(card);

	}

}
