package org.magic.game.model;

import java.util.ArrayList;
import java.util.List;

public class Turn {

	public enum PHASES {Untap,Upkeep,Draw,Main,Combat,Attack,Block,Damage,End_Combat,Main_2,End,Cleanup};
	
	
	List<String> actions;
	int index=0;
	
	public Turn() {
		actions = new ArrayList<String>();
		index=0;
		
	}

	
	public PHASES current()
	{
		return PHASES.values()[index];
	}
	
	public PHASES next()
	{
		index++;
		return current();
	}
	
	public PHASES previous()
	{
		index--;
		return current();
	}
	
	public List<String> getActions() {
		return actions;
	}


	public void setActions(List<String> actions) {
		this.actions = actions;
	}
	
	
	
	
}
