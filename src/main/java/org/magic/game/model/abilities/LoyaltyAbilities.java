package org.magic.game.model.abilities;

public class LoyaltyAbilities extends ActivatedAbilities {

	
	@Override
	public boolean isLoyalty() {
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder build = new StringBuilder();
		build.append("\nLOYALTY: WHEN ").append("PUT ").append(getCosts()).append(" COUNTER DO ").append(getEffects().get(0)).append(" END");
		return build.toString();
	}

	@Override
	public boolean isResolved() {
		return true;
	}
	
}
