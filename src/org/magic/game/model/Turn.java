package org.magic.game.model;

import java.util.ArrayList;
import java.util.List;

public class Turn {

	List<String> actions;
	
	
	public Turn() {
		actions = new ArrayList<String>();
	}

	public List<String> getActions() {
		return actions;
	}


	public void setActions(List<String> actions) {
		this.actions = actions;
	}
	
}
