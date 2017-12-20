package org.magic.game.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.magic.api.beans.MagicCard;
import org.magic.game.gui.components.GamePanelGUI;

public class ManaPool extends Observable implements Serializable{

	Map<String, Integer> pool ;
	
	public ManaPool() {
		pool= new HashMap<String,Integer>();
		addObserver(GamePanelGUI.getInstance().getManaPoolPanel());
	}
	
	public int getMana(String color)
	{
		Integer ret = pool.get(color);
		
		if(ret==null)
			return 0;
		
		return ret;
		
	}
	
	
	public void addMana(String mana)
	{
		addMana(mana,1);
	}
	
	public void addMana(String mana,Integer number)
	{
		try{
			pool.put(mana, pool.get(mana)+number);
			setChanged();
		}catch(NullPointerException e)
		{
			setMana(mana, number);
		}
	}

	public void setMana(String color, int number) {
		pool.put(color, number);
		setChanged();
		
	}
	
	public void useMana(String color,Integer number)
	{
		try{
			setMana(color, pool.get(color)-number);
		}catch(Exception e)
		{
			
		}
	}
	
	public void useMana(MagicCard mc)
	{
		if(mc.getCmc()==null)
			return;
		
		String regex ="\\{(.*?)\\}";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(mc.getCost());
		
		while(m.find())
		{
			String c = m.group();
			useMana(c, 1);
		}
		notifyObservers(this);
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
