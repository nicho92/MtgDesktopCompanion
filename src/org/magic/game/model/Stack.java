package org.magic.game.model;

import java.util.ArrayList;
import java.util.List;

public class Stack {

	java.util.Stack<Stackable> stack;
	
	public Stack() {
		stack= new java.util.Stack<Stackable>();
	}
	
	public void clean()
	{
		stack.clear();
	}
	
	public void put(Stackable a)
	{
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
			b.append(s);
		}
		return b.toString();
		
	}
	
}
