package org.magic.game.model.abilities.effects;

public abstract class Effect {

	protected String effectDescription;
	protected Effect childEffect;
	
	public boolean hasChild()
	{
		return childEffect!=null;
	}
	
	
	public Effect getChildEffect() {
		return childEffect;
	}
	
	public void setChildEffect(Effect childEffect) {
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
		StringBuilder build = new StringBuilder();
		build.append(getEffectDescription());
		if(hasChild())
			build.append("\nAND ").append(getChildEffect());
		
		return build.toString();
		
	}
	
	
}
