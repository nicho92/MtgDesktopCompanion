package org.magic.game.model.costs;

public class ActionCost implements Cost {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String action;

	public void setAction(String c) {
		this.action=c;
	}

	public ActionCost() {
	}
	
	public ActionCost(String action)
	{
		setAction(action);
	}
	

	@Override
	public String toString() {
		return action;
	}

}
