package org.magic.game.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

public class SpellStack {

	Deque<AbstractSpell> stack;

	public SpellStack() {
		stack = new ArrayDeque<>();
	}

	public void clean() {
		stack.clear();
	}

	public void put(AbstractSpell a) {
		if (a.isStackable())
			stack.push(a);
	}

	public AbstractSpell pop() {
		return stack.pop();
	}

	public List<AbstractSpell> toList() {
		return new ArrayList<>(stack);
	}

	public String toString() {
		StringBuilder b = new StringBuilder();

		Iterator<AbstractSpell> it = stack.iterator();
		while (it.hasNext()) {
			b.append(it.next()).append("\n");
		}
		return b.toString();
	}

}
