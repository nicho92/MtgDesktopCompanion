package org.magic.game.model.abilities;

import org.magic.game.model.factories.EffectsFactory;

public class TriggeredAbilities extends AbstractAbilities {
	
	private String event;
	public enum KEYWORDS { WHEN, WHENEVER,AT}
	
	public String getEvent() {
		return event;
	}
	
	public void setEvent(String event) {
		this.event = event;
	}
	
	@Override
	public boolean isTriggered() {
		return true;
	}

	public void init(KEYWORDS key,String s) {
		event = s.substring(key.name().length(),s.indexOf(',')).trim();
		s=s.substring(s.indexOf(',')+1).trim();
		setEffects(EffectsFactory.getInstance().parseEffect(s));
		
	}

	
	@Override
	public String toString() {
		StringBuilder build = new StringBuilder();
		build.append("TRIGGER:\nWHEN:\n\t").append(event).append("\n\tDO :");
		getEffects().forEach(e->build.append("\n\t").append(e).append("\n"));
		build.append("END");
		return build.toString();
		
	}

	
	
}
