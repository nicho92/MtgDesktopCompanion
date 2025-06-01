package org.magic.api.beans.game;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.magic.api.beans.MTGCard;
import org.utils.patterns.observer.Observable;

public class TriggerManager extends Observable {

	public enum TRIGGERS {
		  CREATURE_CAST,
		  CRANK,
		  SPELL_CAST,
		  CREATURE_DEALS_DAMAGE,
		  CREATURE_DIES,
		  CREATURE_ATTACKS,
		  BECOME_BLOCKED,
		  AURA_ATTACH,
		  ENCOUNTER,
		  TAPPED}



	private Map<TRIGGERS,List<AbstractSpell>> triggers;

	public TriggerManager() {
		triggers=new EnumMap<>(TRIGGERS.class);
	}

	public void register(TRIGGERS t,List<AbstractSpell> a)
	{
		a.forEach(_->register(t, a));
	}

	public void trigger(TRIGGERS t,MTGCard mc)
	{
		//TODO implements triggering
	}

	public Set<Entry<TRIGGERS, List<AbstractSpell>>> list() {
		return triggers.entrySet();
	}


}
