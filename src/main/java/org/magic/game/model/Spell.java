package org.magic.game.model;

import java.util.List;

import org.magic.game.model.abilities.costs.Cost;
import org.magic.game.model.abilities.effects.Effect;

public interface Spell {

	
	public List<Cost> getCosts();
	public List<Effect> getEffects();
	public void resolve();
}
