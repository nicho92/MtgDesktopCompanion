package org.magic.game.model.abilities;

import org.magic.game.interfaces.AbstractAbilities;

@Deprecated
public class ActivatedAbilities extends AbstractAbilities {

	private static final long serialVersionUID = 1L;

	@Deprecated
	@Override
	public String toString() {
		final var build = new StringBuilder();
		build.append("\nACTIVATED WHEN PAID:").append(getCosts()).append("\n\tDO :").append(getEffects())
				.append("\nEND");
		return build.toString();
	}

	@Deprecated
	@Override
	public boolean isActivated() {
		return true;
	}
}
