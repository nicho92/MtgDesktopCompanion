package org.magic.api.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MagicCardAlert {

	private String id="";
	private MagicCard card;
	private Double price;
	private List<MagicPrice> offers;
	
	@Override
	public String toString() {
		return String.valueOf(card);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public MagicCardAlert() {
		offers= new ArrayList<MagicPrice>();
	}
	
	public List<MagicPrice> getOffers() {
		return offers;
	}

	public void setOffers(List<MagicPrice> offers) {
		this.offers = offers;
	}

	public MagicCard getCard() {
		return card;
	}
	public void setCard(MagicCard card) {
		this.card = card;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	
	public int hashCode() {
		return getId().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return ((MagicCardAlert)obj).getId().equals(getId());
	}

	
	public void orderDesc()
	{
		Collections.sort(this.offers, new Comparator<MagicPrice >() {
	        @Override public int compare(MagicPrice b1, MagicPrice b2) {
	        	int  val = ( b1.getValue()<b2.getValue() ? -1: 1);
	        	return val;
	        }

	    });
	}
	
	
	
}
