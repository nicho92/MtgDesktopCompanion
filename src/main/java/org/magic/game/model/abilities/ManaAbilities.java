package org.magic.game.model.abilities;

public abstract class ManaAbilities extends AbstractAbilities {

	@Override
	public boolean isStackable() {
		return false;
	}
	
	@Override
	public boolean isMana() {
		return true;
	}
	
	@Override
	public boolean isResolved() {
		return true;
	}
	
}
