package org.magic.game.model.counters;

import org.magic.game.gui.components.DisplayableCard;

public abstract class AbstractCounter {

	public abstract void apply(DisplayableCard displayableCard);

	public abstract void remove(DisplayableCard displayableCard) ;

	public abstract String describe();
	
	public String toString()
	{
		return describe();
	}
	
	@Override
	public int hashCode() {
		return describe().hashCode();
	}
	
}
