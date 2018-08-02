package org.magic.game.model.abilities;

import java.util.List;

import org.magic.game.model.GameManager;
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

	public void init(KEYWORDS key,List<String> list) {
		
		String s = list.get(0);
		if(getCard().getName().indexOf(',')<0)
		{
			event = s.substring(key.name().length(),s.indexOf(',')).trim();
			s=s.substring(s.indexOf(',')+1).trim();
		}
		else
		{
			event = s.substring(key.name().length(),s.lastIndexOf(',')).trim();
			s=s.substring(s.lastIndexOf(',')+1).trim();
		}
		
		list.set(0,s);
		setEffects(EffectsFactory.getInstance().parseEffect(getCard(),list));
	}

	
	@Override
	public String toString() {
		StringBuilder build = new StringBuilder();
		build.append("\nTRIGGER:\nWHEN:\n\t").append(event).append("\n\tDO :");
		getEffects().forEach(e->build.append("\n\t").append(e));
		build.append("\nEND");
		return build.toString();
		
	}
	
	@Override
	public String getTitle() {
		return getEvent();
	}

	
}
