package org.magic.game.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.magic.services.logging.MTGLogger;
import org.utils.patterns.observer.Observable;

public class SpellStack extends Observable {

	Deque<AbstractSpell> stack;
	protected Logger logger = MTGLogger.getLogger(this.getClass());

	public SpellStack() {
		stack = new ArrayDeque<>();
	}

	public void clean() {
		stack.clear();
		setChanged();
		notifyObservers(null);
	}

	public void put(AbstractSpell a) {
		if (a.isStackable())
		{
			stack.push(a);
			setChanged();
			notifyObservers(a);
		}
	}

	public AbstractSpell pop() {
		return stack.pop();
	}

	public List<AbstractSpell> toList() {
		return new ArrayList<>(stack);
	}

	@Override
	public String toString() {
		var b = new StringBuilder();
		Iterator<AbstractSpell> it = stack.iterator();
		while (it.hasNext()) {
			b.append(it.next()).append("\n");
		}
		return b.toString();
	}

	public void unstack() {
		while(!stack.isEmpty())
		{
			AbstractSpell sp = pop();
			sp.resolve();
			setChanged();
			notifyObservers(null);
		}
	}

}
