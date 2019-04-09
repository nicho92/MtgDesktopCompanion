package org.magic.api.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class EditionPriceVariations implements Iterable<CardShake> {

	private List<CardShake> shakes;
	private String providerName;
	private Date date;
	private MagicEdition edition;
	
	public EditionPriceVariations() {
		shakes = new ArrayList<>();
	}
	
	public void addShake(CardShake shake)
	{
		shakes.add(shake);
	}
	
	
	public MagicEdition getEdition() {
		return edition;
	}
	
	public void setEdition(MagicEdition edition) {
		this.edition = edition;
	}
	
	public List<CardShake> getShakes() {
		return shakes;
	}

	public void setShakes(List<CardShake> shakes) {
		this.shakes = shakes;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public Iterator<CardShake> iterator() {
		return shakes.iterator();
	}

	public boolean isEmpty() {
		return shakes.isEmpty();
	}

}
