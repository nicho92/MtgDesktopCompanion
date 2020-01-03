package org.magic.api.beans;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.magic.tools.ColorParser;

public class MTGCombo {
	
	private List<MagicCard> cards;
	private String name;
	private String comment;
	
	
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
	
	public String getColors() {
		Set<String> cmap = new LinkedHashSet<>();
		for (MagicCard mc : getCards()) {
			if ((mc.getCmc() != null))
				for (String c : mc.getColors())
					cmap.add(ColorParser.getCodeByName(c,true));
		}
		return cmap.toString();
	}
	
	
}
