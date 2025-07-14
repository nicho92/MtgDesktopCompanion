package org.magic.game.model.abilities;

import org.magic.game.model.effects.AbstractEffect;

public class SagaAbilities extends ActivatedAbilities {


	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public boolean isLoyalty() {
		return false;
	}

	@Override
	public String toString() {
		var build = new StringBuilder();
		build.append("\nSAGA: WHEN ").append(getCosts()).append(" DO ").append(getEffects().get(0)).append(" END");
		return build.toString();
	}

	@Override
	public boolean isResolved() {
		return true;
	}

	@Override
	public String getTitle() {
		return getCost() + ":" + getEffects();
	}


	public AbstractEffect getEffect()
	{
		return getEffects().get(0);
	}



}
