package org.magic.game.model;

import java.util.ArrayList;
import java.util.List;

public class Stack {

	java.util.Stack<Ability> stack;
	
	public Stack() {
		stack= new java.util.Stack<Ability>();
	}
	
	public void clean()
	{
		stack.clear();
	}
	
	public void put(Ability a)
	{
		stack.push(a);
	}
	
	public Ability pop()
	{
		return stack.pop();
	}
	
	public List<Ability> toList()
	{
		return new ArrayList<Ability>(stack);
	}
	
	public String toString() {
		return stack.toString();
	}
	
}
