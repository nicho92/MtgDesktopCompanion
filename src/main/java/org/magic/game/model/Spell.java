package org.magic.game.model;

import java.io.Serializable;
import java.util.List;

import org.magic.game.model.costs.Cost;
import org.magic.game.model.effects.AbstractEffect;

public interface Spell extends Serializable {


	public List<Cost> getCosts();
	public List<AbstractEffect> getEffects();
	public void resolve();
	public boolean isStackable();
}
