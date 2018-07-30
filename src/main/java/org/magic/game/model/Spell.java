package org.magic.game.model;

import java.util.List;

import org.magic.game.model.costs.Cost;
import org.magic.game.model.effects.AbstractEffect;

public interface Spell {

	
	public List<Cost> getCosts();
	public List<AbstractEffect> getEffects();
	public void resolve();
	public boolean isStackable();
}
