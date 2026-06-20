package org.magic.game.interfaces;

import java.io.Serializable;
import java.util.List;

public interface Spell extends Serializable {

	public List<Cost> getCosts();
	public List<AbstractEffect> getEffects();
	public void resolve();
	public boolean isStackable();
}
