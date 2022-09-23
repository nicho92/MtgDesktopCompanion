package org.magic.api.beans;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.magic.api.beans.enums.MTGColor;
import org.magic.api.interfaces.MTGComboProvider;

public class MTGCombo {

	private List<MagicCard> cards;
	private String name;
	private String comment;
	private MTGComboProvider plugin;


	public void setPlugin(MTGComboProvider plugin) {
		this.plugin = plugin;
	}

	public MTGComboProvider getPlugin() {
		return plugin;
	}

	public List<MagicCard> getCards() {
		return cards;
	}
	public void setCards(List<MagicCard> cards) {
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
		for (MagicCard mc : getCards()) {
			if ((mc.getCmc() != null))
				for (MTGColor c : mc.getColors())
					cmap.add(c.toManaCode());
		}
		return cmap.toString();
	}


	@Override
	public String toString() {
		return getName();
	}
	public void addCard(MagicCard card) {
		cards.add(card);

	}

}
