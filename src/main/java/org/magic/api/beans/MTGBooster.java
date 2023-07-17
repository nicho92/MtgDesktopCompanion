package org.magic.api.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.enums.EnumExtra;

public class MTGBooster implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String boosterNumber;
	private List<MagicCard> cards;
	private Double price;
	private MagicEdition edition;
	private EnumExtra typeBooster;
	
	
	public MTGBooster() {
		cards = new ArrayList<>();
	}

	public MTGBooster(MagicEdition me, EnumExtra typeBooster) {
		this();
		this.edition=me;
		this.typeBooster=typeBooster;
	}
	
	public int boosterSize()
	{
		return cards.size();
	}
	
	public void add(MagicCard mc)
	{
		cards.add(mc);
	}
	
	public EnumExtra getTypeBooster() {
		return typeBooster;
	}
	
	public void setTypeBooster(EnumExtra typeBooster) {
		this.typeBooster = typeBooster;
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
		if(price==null)
			return 0.0;
		
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
