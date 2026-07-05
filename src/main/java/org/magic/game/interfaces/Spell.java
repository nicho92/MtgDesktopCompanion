package org.magic.game.interfaces;

import java.io.Serializable;
import java.util.List;

import org.magic.game.model.beans.CardEffect;

public interface Spell extends Serializable {

	public List<Cost> getCosts();
	public List<CardEffect> getEffects();
	public void resolve();
	public boolean isStackable();
}
