package org.magic.game.model.abilities;

public class ActivatedAbilities extends AbstractAbilities {


	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		var build = new StringBuilder();
		build.append("\nACTIVATED WHEN PAID:").append(getCosts()).append("\n\tDO :").append(getEffects()).append("\nEND");
		return build.toString();
	}

	@Override
	public boolean isActivated() {
		return true;
	}
}
