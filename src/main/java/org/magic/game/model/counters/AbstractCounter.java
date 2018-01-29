package org.magic.game.model.counters;

import java.io.Serializable;

import org.magic.game.gui.components.DisplayableCard;

public abstract class AbstractCounter implements Serializable{

	public abstract void apply(DisplayableCard displayableCard);

	public abstract void remove(DisplayableCard displayableCard) ;

	public abstract String describe();
	
	public String toString()
	{
		return describe();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null)
			return false;
		
		if (this.getClass() != obj.getClass())
		    return false;

		return ((AbstractCounter)obj).hashCode() == this.hashCode();
		
	}
	
	@Override
	public int hashCode() {
		return describe().hashCode();
	}
	
}
