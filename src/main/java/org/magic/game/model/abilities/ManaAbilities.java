package org.magic.game.model.abilities;

public abstract class ManaAbilities extends AbstractAbilities {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
