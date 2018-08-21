package org.magic.game.model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.magic.api.beans.MagicCard;
import org.utils.patterns.observer.Observable;

public class TriggerManager extends Observable {

	public enum TRIGGERS {ENTER_BATTLEFIELD,ENTER_GRAVEYARD}
	
	private Map<TRIGGERS,List<AbstractSpell>> triggers;
	
	public TriggerManager() {
		triggers=new EnumMap<>(TRIGGERS.class);
	}
	
	public void register(TRIGGERS t, AbstractSpell as)
	{
		triggers.computeIfAbsent(t, k->new ArrayList<>());
		triggers.get(t).add(as);
		setChanged();
		notifyObservers(as);
	}
	
	
	public void register(TRIGGERS t,List<AbstractSpell> a)
	{
		a.forEach(as->register(t, a));
	}
	
	public void trigger(TRIGGERS t,MagicCard mc)
	{
//		if(triggers.get(t)!=null)
//		{
//			triggers.get(t).forEach(as->as.resolve());
//		}
	}

	public Set<Entry<TRIGGERS, List<AbstractSpell>>> list() {
		return triggers.entrySet();
	}

	
}
