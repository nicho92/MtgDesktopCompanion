package org.magic.game.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SpellStack {

	Stack<AbstractSpell> stack;
	
	public SpellStack() {
		stack= new java.util.Stack<AbstractSpell>();
	}
	
	public void clean()
	{
		stack.clear();
	}
	
	public void put(AbstractSpell a)
	{
		if(a.isStackable())
			stack.push(a);
	}
	
	public AbstractSpell pop()
	{
		return stack.pop();
	}
	
	public List<AbstractSpell> toList()
	{
		return new ArrayList<AbstractSpell>(stack);
	}
	
	public String toString() {
		StringBuilder b = new StringBuilder();
		for(AbstractSpell s : stack)
		{
			b.append(s).append("\n");
		}
		return b.toString();
	}
	
}
