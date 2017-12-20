package org.magic.api.beans;

import java.util.ArrayList;
import java.util.List;

public class Booster {
	private String boosterNumber;
	private List<MagicCard> cards;
	private Double price;
	private MagicEdition edition;
	
	
	public Booster() {
		cards = new ArrayList<MagicCard>();
	}
	
	public MagicEdition getEdition() {
		return edition;
	}
	public void setEdition(MagicEdition edition) {
		this.edition = edition;
	}
	public String getBoosterNumber() {
		return boosterNumber;
	}
	public void setBoosterNumber(String boosterNumber) {
		this.boosterNumber = boosterNumber;
	}
	public List<MagicCard> getCards() {
		return cards;
	}
	public void setCards(List<MagicCard> cards) {
		this.cards = cards;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	
	@Override
	public String toString() {
		return "Booster " + getBoosterNumber();
	}
	
}
