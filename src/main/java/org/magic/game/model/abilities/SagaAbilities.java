package org.magic.game.model.abilities;

import org.magic.game.model.beans.CardEffect;

@Deprecated
public class SagaAbilities extends ActivatedAbilities {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Deprecated
	@Override
	public boolean isLoyalty() {
		return false;
	}

	@Deprecated
	@Override
	public String toString() {
		final var build = new StringBuilder();
		build.append("\nSAGA: WHEN ").append(getCosts()).append(" DO ").append(getEffects().get(0)).append(" END");
		return build.toString();
	}

	@Deprecated
	@Override
	public boolean isResolved() {
		return true;
	}

	@Deprecated
	@Override
	public String getTitle() {
		return getCost() + ":" + getEffects();
	}

	@Deprecated
	public CardEffect getEffect() {
		return getEffects().get(0);
	}

}
