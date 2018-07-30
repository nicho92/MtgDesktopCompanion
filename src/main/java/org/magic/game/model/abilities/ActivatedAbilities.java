package org.magic.game.model.abilities;

public class ActivatedAbilities extends AbstractAbilities {


	@Override
	public String toString() {
		StringBuilder build = new StringBuilder();
		build.append("\nACTIVATED WHEN PAID:").append(getCosts()).append("\n\tDO :").append(getEffects()).append("\nEND");
		return build.toString();
	}

	@Override
	public void resolve() {
		// TODO Auto-generated method stub
		
	}
}
