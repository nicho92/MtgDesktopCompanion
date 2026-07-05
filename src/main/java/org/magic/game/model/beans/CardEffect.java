package org.magic.game.model.beans;

import org.magic.game.interfaces.AbstractSpell;

public class CardEffect extends AbstractSpell {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String effectDescription;
	private CardEffect childEffect;

	@Override
	public boolean isStackable() {
		return true;
	}

	public CardEffect getChildEffect() {
		return childEffect;
	}

	public void setChildEffect(CardEffect childEffect) {
		this.childEffect = childEffect;
	}

	public String getEffectDescription() {
		return effectDescription;
	}

	public void setEffectDescription(String effectDescription) {
		this.effectDescription = effectDescription;
	}

	@Override
	public String toString() {
		var build = new StringBuilder();
		build.append(getEffectDescription());
		if (childEffect != null)
			build.append("\nAND ").append(getChildEffect());

		return build.toString();

	}

}
