package org.magic.game.model.costs;

public class ActionCost extends Cost {

	private String action;
	
	public void setAction(String c) {
		this.action=c;
	}
	
	
	@Override
	public String toString() {
		return action;
	}

	
	
	
}
