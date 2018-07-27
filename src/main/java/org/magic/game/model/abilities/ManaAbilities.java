package org.magic.game.model.abilities;

public abstract class ManaAbilities extends ActivatedAbilities {

	@Override
	public boolean isStackable() {
		return false;
	}
	
	@Override
	public boolean isMana() {
		return true;
	}
	
}
