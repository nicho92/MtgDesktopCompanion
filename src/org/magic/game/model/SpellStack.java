package org.magic.game.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SpellStack {

	Stack<Stackable> stack;
	
	public SpellStack() {
		stack= new java.util.Stack<Stackable>();
	}
	
	public void clean()
	{
		stack.clear();
	}
	
	public void put(Stackable a)
	{
		if(a.isStackable())
			stack.push(a);
	}
	
	public Stackable pop()
	{
		return stack.pop();
	}
	
	public List<Stackable> toList()
	{
		return new ArrayList<Stackable>(stack);
	}
	
	public String toString() {
		StringBuilder b = new StringBuilder();
		for(Stackable s : stack)
		{
			b.append(s).append("\n");
		}
		return b.toString();
	}
	
}
