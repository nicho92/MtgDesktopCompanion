package org.magic.game.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SpellStack {

	Stack<AbstractSpell> stack;
	
	public SpellStack() {
		stack= new Stack<AbstractSpell>();
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
		
		for(int i=stack.size()-1;i>=0;i--)
		{
			b.append(stack.get(i)).append("\n");
		}
		return b.toString();
	}
	
}
