package org.magic.game.model.counters;

import org.magic.game.gui.components.DisplayableCard;

public class ItemCounter extends AbstractCounter {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String name;

	public ItemCounter(String name) {
		this.name = name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public void apply(DisplayableCard displayableCard) {
		// do nothing
	}

	@Override
	public void remove(DisplayableCard displayableCard) {
		// do nothing
	}

	@Override
	public String describe() {
		return name + " counter";
	}


	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {

		if(obj ==null)
			return false;

		return hashCode()==obj.hashCode();
	}



}
