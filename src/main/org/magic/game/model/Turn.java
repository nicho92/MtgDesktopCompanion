package org.magic.game.model;

import java.util.ArrayList;
import java.util.List;

public class Turn {

	public enum PHASES {Untap,Upkeep,Draw,Main,Combat,Attack,Block,Damage,End_Combat,Main_2,End,Cleanup};
	
	
	List<String> actions;
	int index=0;
	PHASES current;
	
	
	public Turn() {
		actions = new ArrayList<String>();
		index=0;
		
	}

	
	public List<String> getActions() {
		return actions;
	}


	public void setActions(List<String> actions) {
		this.actions = actions;
	}

    public PHASES currentPhase()
    {
    	return current;
    }

	public void setCurrentPhase(PHASES p) {
		this.current=p;
		
	}
	
	
	
	
}
