package org.magic.api.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class EditionsShakers implements Iterable<CardShake> {

	private List<CardShake> shakes;
	private String providerName;
	private Date date;
	private MagicEdition edition;

	public EditionsShakers() {
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

	public int getSize()
	{
		return shakes.size();
	}

	public CardShake getShakeFor(MagicCard mc, boolean foil)
	{
		Optional<CardShake> opt = shakes.stream().filter(s->s.getName().equalsIgnoreCase(mc.getName()) && s.isFoil()==foil).findAny();
		if(opt.isPresent())
			return opt.get();
		else
			return null;

	}


}
