package org.magic.game.model.abilities;

@Deprecated
public abstract class ManaAbilities extends ActivatedAbilities {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Deprecated
	@Override
	public boolean isStackable() {
		return false;
	}

	@Deprecated
	@Override
	public boolean isMana() {
		return true;
	}

	@Deprecated
	@Override
	public boolean isResolved() {
		return true;
	}

	@Deprecated
	@Override
	public boolean isActivated() {
		return false;
	}

}
