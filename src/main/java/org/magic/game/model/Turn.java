package org.magic.game.model;

import java.util.ArrayList;
import java.util.List;

public class Turn {

	public enum PHASES {UNTAP,UPKEEP,DRAW,MAIN,COMBAT,ATTACK,BLOCK,DAMAGE,END_COMBAT,MAIN_2,END,CLEANUP}
	
	
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
