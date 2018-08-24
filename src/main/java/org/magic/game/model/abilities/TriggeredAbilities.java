package org.magic.game.model.abilities;

import java.util.List;

import org.magic.game.model.TriggerManager.TRIGGERS;
import org.magic.game.model.factories.EffectsFactory;

public class TriggeredAbilities extends AbstractAbilities {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String event;
	public enum KEYWORDS { WHEN, WHENEVER, AT}
	private KEYWORDS key;
	
	public String getEvent() {
		return event;
	}
	
	public TRIGGERS getTrigger()
	{
		
		if(event.indexOf("enters the battlefield")>-1)
		{
			if(event.substring(0, event.indexOf("enters the battlefield")).trim().equals(getCard().getName()))
				setTarget(this);
			
			return TRIGGERS.ENTER_THE_BATTLEFIELD;
		}
		
		
		return null;
		
	}
	
	
	@Override
	public boolean isTriggered() {
		return true;
	}

	public void init(KEYWORDS key,List<String> list) {
		this.key=key;
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
		build.append("\nTRIGGER:\n").append(key).append(":\n\t").append(event).append("\n\tDO :");
		getEffects().forEach(e->build.append("\n\t").append(e));
		build.append("\nEND");
		return build.toString();
		
	}
	
	@Override
	public String getTitle() {
		return getEvent();
	}

	
}
