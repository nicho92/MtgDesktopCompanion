package org.magic.game.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

public class ManaPool extends Observable implements Serializable{

	Map<String, Integer> pool ;
	
	public ManaPool() {
		pool= new HashMap<String,Integer>();
	}
	
	public void addMana(String mana)
	{
		addMana(mana,1);
	}
	
	public void addMana(String mana,Integer number)
	{
		try{
			pool.put(mana, pool.get(mana)+number);
		}catch(NullPointerException e)
		{
			setMana(mana, number);
		}
	}

	public void setMana(String color, int number) {
		pool.put(color, number);
		
	}
	
	public void useMana(String color,Integer number)
	{
		setMana(color, pool.get(color)-number);
	}
	
	public void clean(){
		pool.values().clear();
	}
	
	public String toString() {
		
		StringBuilder build = new StringBuilder();
		for(String key : pool.keySet())
			for(int i=0;i<pool.get(key);i++)
				build.append(key);
		
		
		return build.toString();
	}
	
	
	
}
