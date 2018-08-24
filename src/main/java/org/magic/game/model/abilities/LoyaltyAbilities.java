package org.magic.game.model.abilities;

public class LoyaltyAbilities extends ActivatedAbilities {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
	
	@Override
	public String getTitle() {
		return getCost() + ":" + getEffects();
	}
	
	
	
	
}
