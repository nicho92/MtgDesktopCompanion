package org.magic.game.model.effects;

import org.magic.api.beans.game.AbstractSpell;

public abstract class AbstractEffect extends AbstractSpell {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String effectDescription;
	private AbstractEffect childEffect;



	@Override
	public boolean isStackable() {
		return true;
	}

	public AbstractEffect getChildEffect() {
		return childEffect;
	}

	public void setChildEffect(AbstractEffect childEffect) {
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
		if(childEffect!=null)
			build.append("\nAND ").append(getChildEffect());

		return build.toString();

	}


}
